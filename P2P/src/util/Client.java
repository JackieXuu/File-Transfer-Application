package util;

import java.io.BufferedOutputStream;  
import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.io.FileOutputStream;
import javax.swing.JFrame;



public class Client extends JFrame {  
    private ClientSocket socket = null;  
  
    private String ip = "null";
  
    private int port; 
    
    public Client(int port, String clientIP) { 
    	this.port = port;
    	this.ip = clientIP;
    }  
  
    private void shutdown(){
    	try{
    		socket.shutDownConnection();
    		System.out.println("Connection shut down!");
    	} catch (Exception ex){
    		ex.printStackTrace();
    	}
    }
    private boolean createConnection() {  
        socket = new ClientSocket(ip, port);  
        try {  
            socket.CreateConnection();  
            System.out.print("Connection Successfully!" + "\n");  
            return true;  
        } catch (Exception e) {  
            System.out.print("Connection Failed!" + "\n");  
            return false;  
        }  
  
    }  
  
    
    public boolean getMessage(String downloadFile, String savePath) {  
        if (socket == null)  
            return false;  
        DataInputStream inputStream = null;  
        DataOutputStream outputStream = null;
		try {  
            inputStream = socket.getMessageStream(); 
            outputStream  = socket.sendMessageStream();
        } catch (Exception e) {  
            System.out.print("Receive message failed\n");  
            return false;  
        }  
  
        try {  
            outputStream.writeUTF(downloadFile);
            int bufferSize = 8192;  
            byte[] buf = new byte[bufferSize];  
            int passedlen = 0;  
            long len = 0;  
  
            savePath += inputStream.readUTF();  
            DataOutputStream fileOut = new DataOutputStream(  
                    new BufferedOutputStream(new FileOutputStream(savePath)));  
            len = inputStream.readLong();  
            System.out.println("The length of the file: " + len + "\n");  
            System.out.println("Begin receiving file" + "\n");  
  
            while (true) {  
                int read = 0;  
                if (inputStream != null) {  
                    read = inputStream.read(buf);  
                }  
                passedlen += read;  
                if (read == -1) {  
                	System.out.println("Read finished!");
                    break;  
                }  
                System.out.println("The progress of receiving file: " + (passedlen * 100 / len) + "%\n");  
                fileOut.write(buf, 0, read);  
            }  
            System.out.println("Received file and save it in " + savePath + "\n");  
  
            fileOut.close();
            shutdown();
            return true;
        } catch (Exception e) {  
            System.out.println("Wrong information in receiving." + "\n");  
            return false;  
        }
    }  
  public boolean implement(String downloadFile ,int port, String savePath, String clientIP){
	try {  
		if (createConnection()) {  
			System.out.println("Connection created!\n");  
        }  
    } catch (Exception ex) {  
        ex.printStackTrace();  
    }  
	if (getMessage(downloadFile, savePath)){
		return true;
	}
	else{
		return false;
	}
  }

}  
