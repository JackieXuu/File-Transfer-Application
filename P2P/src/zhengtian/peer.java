package zhengtian;

import util.Client;
import util.Server;

class clientThread extends Thread {
    public volatile boolean exit = false; 
	String savePath = "null";
	String clientIP = "null";
	String downloadFile = "null";
	int port;
	public clientThread(String downloadFile,int port, String savePath, String clientIP) {
		this.port = port;
		this.savePath = savePath;
		this.clientIP = clientIP;
		this.downloadFile = downloadFile;
	}
	@Override
	public void run(){
		Client client = new Client(this.port, this.clientIP);
		if (!client.implement(downloadFile, port, savePath, clientIP)){
			System.out.println("Something must be wrong!");
		}
	}
}

class serverThread implements Runnable{
	int port;
	public serverThread(int port) {
		this.port = port;
	}
	@Override
	public void run() {
		Server server = new Server(this.port);
		server.implement();
	}
}

public class peer {
	public boolean flag1 = false;
	public boolean flag2 = false;
	public void run1(String FileonServerPath,int port1,int port2, String savePath, String clientIP) {
		System.out.println(port1);
		serverThread server = new serverThread(port1);
		Thread thread1 = new Thread(server);
		clientThread thread2 = new clientThread(FileonServerPath, port1, savePath, clientIP);
		if (!flag1){
			thread1.start();
		}
		thread2.start();
        thread2.exit = true;  
		thread2.interrupt(); 
	}
	public void run2(String FileonServerPath,int port1,int port2, String savePath, String clientIP) {
		System.out.println(port1);
		serverThread server1 = new serverThread(port1);
		Thread thread3 = new Thread(server1);
		clientThread thread4 = new clientThread(FileonServerPath, port1, savePath, clientIP);
		if (!flag2){
			thread3.start();
		}
		thread4.start();
        thread4.exit = true;  
		thread4.interrupt(); 
	}
}