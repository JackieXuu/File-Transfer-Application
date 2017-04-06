package zhengtian;

import java.io.BufferedInputStream;  
import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.io.File;  
import java.io.FileInputStream;  
import java.net.ServerSocket;  
import java.net.Socket;  
  
public class Server {  
    int port = 8821;  
  
    void start() {  
        Socket clientSocket = null;  
        try {  
            ServerSocket socket = new ServerSocket(port);  
            while (true) {  
                clientSocket = socket.accept();  
                System.out.println("Enstablish Socket!");  
                DataInputStream in = new DataInputStream(  
                        new BufferedInputStream(clientSocket.getInputStream()));  
                String filePath = in.readUTF();  
                File file = new File(filePath); 
                System.out.println("The length of file:" + (int) file.length());  

                DataInputStream fileIn = new DataInputStream(  
                        new BufferedInputStream(new FileInputStream(filePath)));  
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());  
                out.writeUTF(file.getName());  
                System.out.println(file.getName());
                out.flush();  
                out.writeLong((long) file.length());  
                out.flush();  
  
                int bufferSize = 8192;  
                byte[] buf = new byte[bufferSize];  
  
                while (true) {  
                    int read = 0;  
                    if (fileIn != null) {  
                        read = fileIn.read(buf);  
                    }  
                    if (read == -1) {  
                        break;  
                    }  
                    out.write(buf, 0, read);  
                }  
                out.flush();  
                fileIn.close();  
                clientSocket.close(); 
                System.out.println("Finish transporting file.");  
            }  
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    public static void main(String arg[]) {  
        new Server().start();  
    }  
}  