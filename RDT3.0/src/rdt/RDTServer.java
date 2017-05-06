package rdt; 
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;  
import java.net.DatagramPacket;  
import java.net.DatagramSocket;  
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

public class RDTServer {
    private DatagramPacket datagramPacket;
    private int port;
    private String ip;
    private InetAddress inetAddress;
    private DatagramSocket datagramSocket;
    private int pkt = 0;
    private int seq = 0;
    private File file;

    public RDTServer (String ip, int port) throws IOException {
    	this.port = port;
        this.ip = ip;
        datagramSocket = new DatagramSocket(port);
        inetAddress = InetAddress.getByName(ip);
        datagramPacket = new DatagramPacket(new byte[2*1024], 2*1024, inetAddress, port+1);
        System.out.println("Wait the client...");
        datagramSocket.receive(datagramPacket);
        String filePath = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
        
        System.out.println(filePath);
        file = new File(filePath);        
        send();
    }
    void close() {
        if (datagramSocket != null && !datagramSocket.isClosed()) {
            datagramSocket.close();
        }
    }

    void send() {
    	int sum = 0;
        System.out.println(String.format("Sending to %s:%d", ip, port));
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            sendName(file.getName());
            ++pkt;
            sendSize(fin.getChannel().size());
            ++pkt;
            System.out.println("size:" + fin.getChannel().size());
            byte[] buf = new byte[SpecialPacket.MAX_DATA_SIZE];
            int readLen;
            while ((readLen = fin.read(buf)) != -1) {
                sendData(buf, 0, readLen);
                ++pkt;
                sum += readLen;
                System.out.println("Send " + sum + '/' + fin.getChannel().size());
            }
            System.out.println();
            sEnd();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                //    close();
                }
            } catch (Exception e) {
            }
        }

    }

    public void  sendName(String name) throws IOException {
		SpecialPacket packet = new SpecialPacket(seq, SpecialPacket.FLAG_FILENAME, name.getBytes(StandardCharsets.UTF_8));
		DatagramPacket newdatagramPacket = new DatagramPacket(packet.getPacket(), packet.getPacket().length, inetAddress, port+1);
        datagramSocket.send(newdatagramPacket);
        ACK(packet);
        ++seq;
        if (seq == 65536) seq = 1;
    }

     public void  sendSize(long size) throws IOException {
        SpecialPacket packet = new SpecialPacket(seq, SpecialPacket.FLAG_FILESIZE, String.valueOf(size).getBytes(StandardCharsets.UTF_8));
		DatagramPacket newdatagramPacket = new DatagramPacket(packet.getPacket(), packet.getPacket().length, inetAddress, port+1);
        datagramSocket.send(newdatagramPacket);
        ACK(packet);
        ++seq;
        if (seq == 65536) seq = 1;
    }

    public void  sendData(byte[] data, int offset, int length) throws IOException {
        SpecialPacket packet = new SpecialPacket(seq, SpecialPacket.FLAG_FILEDATA, data, offset, length);
		DatagramPacket newdatagramPacket = new DatagramPacket(packet.getPacket(), packet.getPacket().length, inetAddress, port+1);
        datagramSocket.send(newdatagramPacket);
        ACK(packet);
        ++seq;
        if (seq == 65536) seq = 1;
    }
    
    public void sEnd() throws IOException {
    	System.out.println(String.format("Sending file end, ALL_PACKET = %d.", pkt+1));
        SpecialPacket packet = new SpecialPacket(seq, SpecialPacket.FLAG_FILEEND, new byte[0], 0, 0);
		DatagramPacket newdatagramPacket = new DatagramPacket(packet.getPacket(), packet.getPacket().length, inetAddress, port+1);
        datagramSocket.send(newdatagramPacket);
        ACK(packet);
    }

    public static void main(String[] args) throws IOException {
    	new RDTServer("localhost", 8821);
    }
    
    public void ACK(SpecialPacket packet) throws IOException {
        int waitTimeout = 2;
        datagramSocket.setSoTimeout(10 * 1000);
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
            		DatagramPacket newdatagramPacket = new DatagramPacket(packet.getPacket(), packet.getPacket().length, inetAddress, port+1);
                    datagramSocket.send(newdatagramPacket);
                } catch (IOException e) {
                    System.out.println("Error while resending packet: " + e.getMessage());
                }
            }
        }, waitTimeout * 1000, waitTimeout * 1000);
        while (true) {
            try {
                datagramSocket.receive(datagramPacket);
            } catch (SocketTimeoutException e) {
            	System.out.println("Socket timeout: while waiting for ack.");
                continue;
            }
            SpecialPacket ack = new SpecialPacket(datagramPacket.getData(), 0, datagramPacket.getLength()-1);
            if (ack.getFlag() == SpecialPacket.FLAG_ACK
                && ack.getSeq() == packet.getSeq()
                && ack.getChecksum() == SpecialPacket.generateChecksum(datagramPacket.getData(),SpecialPacket.CHECKSUM_LENGTH,datagramPacket.getLength() - SpecialPacket.CHECKSUM_LENGTH)) {
                timer.cancel();
                break;
            }
        }
    }
}



