package com.udp;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class UDPServer {

	public static void main(String[] args) {
		byte[] buf = new byte[UDPUtil.BUFFER_SIZE];
		byte[] receive = new byte[1];
        RandomAccessFile file = null;  
        DatagramPacket packet = null;  
        DatagramSocket socket = null; 
        try{
			packet = new DatagramPacket(buf, buf.length, new InetSocketAddress(InetAddress.getByName("localhost"), UDPUtil.PORT));
			socket = new DatagramSocket(UDPUtil.PORT+1, InetAddress.getByName("localhost"));
			System.out.println("Wait the client...");
			socket.receive(packet);
			String file_path = new String(packet.getData(), 0, packet.getLength());
			System.out.println(file_path);
        	file = new RandomAccessFile(file_path, "r");
        	int size = -1;
        	int totalCount = 0;
        	while((size = file.read(buf, 0, buf.length)) != -1){
        		packet.setData(buf, 0, size);
        		socket.send(packet); 
        		totalCount = totalCount + size;
				/*
        		while (true){
        			packet.setData(receive, 0, receive.length);
        			socket.receive(packet);
        			if (!UDPUtil.isEquals(UDPUtil.success, receive, packet.getLength())){
        				packet.setData(buf, 0, size);
                		socket.send(packet);
        			}
        			else{
        				break;
        			}
        			
        		}*/
        	}
        	System.out.println("Send " + totalCount + "!");
        	while (true){
        		packet.setData(UDPUtil.exit, 0, UDPUtil.exit.length);
        		socket.send(packet);
        		
        		packet.setData(receive, 0, receive.length);
        		socket.receive(packet);
        		if (!UDPUtil.isEquals(UDPUtil.exit, receive, packet.getLength())){
            		packet.setData(UDPUtil.exit, 0, UDPUtil.exit.length);
            		socket.send(packet);
        		}
        		else{
        			break;
        		}
        	}
        	
        }catch (Exception e){
        	e.printStackTrace();
        }finally{
        	if (file != null){
        		try {
					file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	if (socket != null){
        		socket.close();
       	}
        	
         }
	}

}
