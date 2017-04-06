package zhengtian;

import java.io.BufferedInputStream;
import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.net.Socket;  
  
public class ClientSocket {  
    private String ip;  
  
    private int port;  
  
    private Socket socket = null;  
  
    DataOutputStream sendMessageStream = null;  
  
    DataInputStream getMessageStream = null;  
  
    public ClientSocket(String ip, int port) {  
        this.ip = ip;  
        this.port = port;  
    }  
  
    public void CreateConnection() throws Exception {  
        try {  
            socket = new Socket(ip, port);  
        } catch (Exception e) {  
            e.printStackTrace();  
            if (socket != null)  
                socket.close();  
            throw e;  
        } finally {  
        }  
    }  
    public DataInputStream getMessageStream() throws Exception {  
        try {  
            getMessageStream = new DataInputStream(new BufferedInputStream(  
                    socket.getInputStream()));  
            return getMessageStream;  
        } catch (Exception e) {  
            e.printStackTrace();  
            if (getMessageStream != null)  
                getMessageStream.close();  
            throw e;  
        } finally {  
        }  
    }  
    

    public DataOutputStream sendMessageStream() throws Exception {  
        try {  
            sendMessageStream = new DataOutputStream(socket.getOutputStream());  
            return sendMessageStream;  
        } catch (Exception e) {  
            e.printStackTrace();  
            if (sendMessageStream != null)  
            	sendMessageStream.close();  
            throw e;  
        } finally {  
        }  
    } 
  
    public void shutDownConnection() {  
        try {  
            if (sendMessageStream != null)  
            	sendMessageStream.close();  
            if (getMessageStream != null)  
                getMessageStream.close();  
            if (socket != null)  
                socket.close();  
        } catch (Exception e) {  
        	return;
        }  
    }  
}  
