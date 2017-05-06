package rdt;
import java.awt.BorderLayout;  
import java.awt.GridLayout;  
import java.awt.event.ActionEvent;  
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.net.DatagramPacket;  
import java.net.DatagramSocket;  
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

import javax.swing.*;



public class RDTClient extends JFrame {
    private DatagramSocket datagramSocket = null;
    private int sum = 0, error = 0;
    private int pkt = 0;
    BufferedOutputStream file = null; 
    private long fileSize;
    
	private static String SAVE_FILE_PATH = null;
	private static String LOAD_FILE_PATH = null;
    
	JPanel panel = new JPanel();
	JPanel panel2 = new JPanel();
	JLabel serverPath = new JLabel("File Path on the server:");
	JTextField serverText = new JTextField();
	JLabel clientPath = new JLabel("File Path save the client:");
	JTextField clientText = new JTextField();
	JButton downloadButton = new JButton("Download");
	JTextField ipText = new JTextField("localhost");
	JTextField portText = new JTextField("8821");
	JButton escButton = new JButton("Esc"); 
    JButton filePathonServer = new JButton("...");
    JButton filePathonClient = new JButton("...");
    public void init(){
    	this.setTitle("RDT3.0 Client");  
		this.setLayout(new BorderLayout());
		this.setLocationRelativeTo(null);
		panel.setLayout(new GridLayout(0, 3));
		panel.add(serverPath, BorderLayout.WEST);
		panel.add(serverText, BorderLayout.CENTER);
		panel.add(filePathonServer, BorderLayout.EAST);
		panel.add(clientPath, BorderLayout.WEST);
		panel.add(clientText, BorderLayout.CENTER);		
		panel.add(filePathonClient, BorderLayout.EAST);
		panel.add(ipText, BorderLayout.CENTER);	
		panel.add(portText, BorderLayout.CENTER);
		this.add(panel, BorderLayout.NORTH);
		panel2.add(downloadButton, BorderLayout.WEST);
		panel2.add(escButton,  BorderLayout.EAST);
		this.add(panel2, BorderLayout.CENTER);
		this.setSize(500,175);
		this.setVisible(true);
		
		
		downloadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String download =serverText.getText();
				String save = clientText.getText();
				String ip = ipText.getText();
				int port = Integer.parseInt(portText.getText());
	        	download = download.replace("\\","\\\\");
	        	save = save.replace("\\", "\\\\");
	        	save = save + "\\\\";
	        	try {
					receive(download, save, ip, port);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		
		escButton.addActionListener (new ActionListener (){
			public void actionPerformed(ActionEvent e) {
	            int exi = JOptionPane.showConfirmDialog (null, "Do you want to close client?", "Click a button", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
	            if (exi == JOptionPane.YES_OPTION){
	                System.exit (0);
	            }
	            else{
	                return;
	            }
	        }
	    });
		
    	filePathonServer.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			JFileChooser jfc = new JFileChooser();
    			jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    				if(jfc.showOpenDialog(panel)==JFileChooser.APPROVE_OPTION ){
    					serverText.setText(jfc.getSelectedFile().getAbsolutePath());
    				}
    		}
    	});
    	
    	filePathonClient.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			JFileChooser jfc = new JFileChooser();
    			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    				if(jfc.showOpenDialog(panel)==JFileChooser.APPROVE_OPTION ){
    					clientText.setText(jfc.getSelectedFile().getAbsolutePath());
    				}
    		}
    	});
		
    }
    
    public RDTClient() {
    	init();
    }

    public void receive(String download, String save, String ipString, int port) throws IOException {
    	long startTime = System.currentTimeMillis();
    	int sum_all = 0;
        int timeoutTimes = 0, curSeq = -1, endSeq = -1;
        boolean fileEnd = false;
        boolean lastPacketIsFileData = false;   	        
        
    	LOAD_FILE_PATH = download;
    	String[] aa = download.split("\\\\");
    	SAVE_FILE_PATH = save + aa[aa.length-1];
    	
		byte[] sendBuf = LOAD_FILE_PATH.getBytes();
		
		System.out.println(LOAD_FILE_PATH + " " + SAVE_FILE_PATH);
		
		InetAddress ip = InetAddress.getByName(ipString);
		DatagramPacket pp = new DatagramPacket(sendBuf, sendBuf.length, ip, port);
		datagramSocket = new DatagramSocket(port+1);
		datagramSocket.send(pp);
		
        DatagramPacket datagramPacket = new DatagramPacket(new byte[2*1024], 2*1024);

        while (!(fileEnd && curSeq == endSeq) && timeoutTimes != 3) {
            try {
                datagramSocket.receive(datagramPacket);
            } catch (SocketTimeoutException e) {
                ++timeoutTimes;
                System.out.println("Socket timeout: while waiting for package.");
                continue;
            }

            SpecialPacket packet = new SpecialPacket(datagramPacket.getData(), 0, datagramPacket.getLength()-1);
            ++sum;
            if (((curSeq+1) != packet.getSeq() && curSeq != 65535 )||(curSeq == 65535 && (curSeq+1)%65535 != packet.getSeq()) ) {
            	if (curSeq == packet.getSeq()) {
            		SpecialPacket response = new SpecialPacket(curSeq, SpecialPacket.FLAG_ACK, new byte[0], 0, 0);
            		DatagramPacket newdatagramPacket = new DatagramPacket(response.getPacket(), response.getPacket().length, ip, port);
                    datagramSocket.send(newdatagramPacket);
            	}
                ++error;
                continue;
            } else if (packet.getChecksum() != SpecialPacket.generateChecksum(
                    datagramPacket.getData(),
                    SpecialPacket.CHECKSUM_LENGTH,
                    datagramPacket.getLength() - SpecialPacket.CHECKSUM_LENGTH)) {
                ++error;
                SpecialPacket response = new SpecialPacket(curSeq-1, SpecialPacket.FLAG_ACK, new byte[0], 0, 0);
        		DatagramPacket newdatagramPacket = new DatagramPacket(response.getPacket(), response.getPacket().length, ip, port);
                datagramSocket.send(newdatagramPacket);
                continue;
            } else {
                ++curSeq;
                ++pkt;
                if (curSeq == 65536) curSeq = 1;
                SpecialPacket response = new SpecialPacket(curSeq, SpecialPacket.FLAG_ACK, new byte[0], 0, 0);
        		DatagramPacket newdatagramPacket = new DatagramPacket(response.getPacket(), response.getPacket().length, ip, port);
                datagramSocket.send(newdatagramPacket);
            }

            switch (packet.getFlag()) {
                case SpecialPacket.FLAG_FILENAME:
                    file = new BufferedOutputStream(new FileOutputStream(SAVE_FILE_PATH));
                    lastPacketIsFileData = false;
                    break;
                case SpecialPacket.FLAG_FILESIZE:
                    fileSize = Long.parseLong(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8));
                    lastPacketIsFileData = false;
                    break;
                case SpecialPacket.FLAG_FILEDATA:
                    lastPacketIsFileData = true;
                    file.write(packet.getData(), 0, packet.getLength());
                    sum_all += packet.getLength();
                    System.out.println("Receive: " + sum_all +"/"+fileSize);
                    break;
                case SpecialPacket.FLAG_FILEEND:
                    fileEnd = true;
                    endSeq = packet.getSeq();
                    if (lastPacketIsFileData) {
                        System.out.println("receive end");
                    }
                    lastPacketIsFileData = false;
                    break;
            }
        }
        long endTime = System.currentTimeMillis();
        long Time = endTime - startTime;
        System.out.println(String.format("Receive file end, ALL_PACKET = %d.", pkt));
        System.out.println(String.format("Receive file end, Error = %d.", error));
        System.out.print("The total time is " + Time);
        if (file != null) {
           file.close();
           close();
        }

    }

    public void close() {
        if (datagramSocket != null && !datagramSocket.isClosed()) {
            datagramSocket.close();
        }
    }

    public static void main(String[] args) {   
    	new RDTClient();
    }
}
