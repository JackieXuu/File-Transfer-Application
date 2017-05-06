package com.udp;

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
import java.net.InetSocketAddress;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class UDPClient extends JFrame{
	private static byte[] buf = new byte[UDPUtil.BUFFER_SIZE];
	private static BufferedOutputStream file = null; 
	private static DatagramPacket packet = null;  
	private static DatagramSocket socket = null; 
	private static String SAVE_FILE_PATH = null;
	private static String LOAD_FILE_PATH = null;
	
	
	JPanel panel = new JPanel();
	JPanel panel2 = new JPanel();
	JLabel serverPath = new JLabel("File Path on the server:");
	JTextField serverText = new JTextField();
	JLabel clientPath = new JLabel("File Path save the client:");
	JTextField clientText = new JTextField();
	JButton downloadButton = new JButton("Download");
	JButton escButton = new JButton("Esc"); 
	JTextField ipText = new JTextField("localhost");
	JTextField portText = new JTextField("8821");
    JButton filePathonServer = new JButton("...");
    JButton filePathonClient = new JButton("...");
    
	
    public void init(){
    	this.setTitle("UDP Client");  
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
		this.setSize(500,150);
		this.setVisible(true);
		
		downloadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String download =serverText.getText();
				String save = clientText.getText();
				int port = Integer.parseInt(portText.getText());
				String ip = ipText.getText();
	        	download = download.replace("\\","\\\\");
	        	save = save.replace("\\", "\\\\");
	        	save = save + "\\\\";
	        	receive(download, save, ip, port);
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
    
    public void receive(String download, String save, String ipString, int port){
    	UDPUtil.PORT = port;
    	long startTime = System.currentTimeMillis();
    	LOAD_FILE_PATH = download;
    	String[] aa = download.split("\\\\");
    	SAVE_FILE_PATH = save + aa[aa.length-1];
		byte[] sendBuf = LOAD_FILE_PATH.getBytes();
		try{
			file = new BufferedOutputStream(new FileOutputStream(SAVE_FILE_PATH));
			packet = new DatagramPacket(buf, buf.length, new InetSocketAddress(InetAddress.getByName(ipString), UDPUtil.PORT+1));
			socket = new DatagramSocket(UDPUtil.PORT, InetAddress.getByName(ipString));
	 		
			packet.setData(sendBuf, 0, sendBuf.length);
			socket.send(packet);
			System.out.println("Start file transfer!");
			packet.setData(buf, 0, buf.length);
			socket.receive(packet);
			int size = 0;
			int totalSize = 0;
			int flushFlag = 0;
			while ((size = packet.getLength()) != 0){
				if (UDPUtil.isEquals(UDPUtil.exit, buf, size)){
					System.out.println("Client exit");
					packet.setData(UDPUtil.exit, 0, UDPUtil.exit.length);
					socket.send(packet);
					break;
				}
				file.write(buf, 0, size);
				if (++flushFlag % 1000 == 0){
					flushFlag = 0;
					file.flush();
				}
				
				packet.setData(UDPUtil.success, 0, UDPUtil.success.length);
				socket.send(packet);
				
				totalSize = totalSize + size;
				packet.setData(buf,0, buf.length); 
				socket.receive(packet);
				System.out.println("Received " + totalSize + "!");
			}
			file.flush();
			System.out.println("Received all " + totalSize + "!");
		}catch (Exception e){
			e.printStackTrace();
		} finally{
			if (file != null){
				try {
					file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (socket != null){
				socket.close();
			}
			}
		}
		
		long endTime = System.currentTimeMillis();
		long Time = endTime - startTime;
		System.out.print("The total time is " + Time);
    }
    
    public UDPClient(){
    	init();
    }
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new UDPClient();
	}

}
