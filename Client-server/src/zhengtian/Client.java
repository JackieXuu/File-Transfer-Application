package zhengtian;

import java.io.BufferedOutputStream;  
import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.io.FileOutputStream;
import java.util.Scanner;  
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class Client extends JFrame {  
    private ClientSocket socket = null;  
  
    private String ip = "localhost";
  
    private int port = 8821;  
	JPanel panel = new JPanel();
	JPanel panel2 = new JPanel();
	JLabel serverPath = new JLabel("File Path on the server:");
	JTextField serverText = new JTextField();
	JLabel clientPath = new JLabel("File Path save the client:");
	JTextField clientText = new JTextField();
	JButton downloadButton = new JButton("Download");
	JButton escButton = new JButton("Esc"); 
    JButton filePathonServer = new JButton("...");
    JButton filePathonClient = new JButton("...");
    
    public Client() { 
    	this.setTitle("Client");  
		this.setLayout(new BorderLayout());
		this.setLocationRelativeTo(null);
		panel.setLayout(new GridLayout(0, 3));
		panel.add(serverPath, BorderLayout.WEST);
		panel.add(serverText, BorderLayout.CENTER);
		panel.add(filePathonServer, BorderLayout.EAST);
		panel.add(clientPath, BorderLayout.WEST);
		panel.add(clientText, BorderLayout.CENTER);		
		panel.add(filePathonClient, BorderLayout.EAST);
		this.add(panel, BorderLayout.NORTH);
		panel2.add(downloadButton, BorderLayout.WEST);
		panel2.add(escButton,  BorderLayout.EAST);
		this.add(panel2, BorderLayout.CENTER);
		this.setSize(500,150);
		this.setVisible(true);
		
		
		downloadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        try {  
		            if (createConnection()) {  
		                System.out.println("Connection created!\n");  
		            }  
		        } catch (Exception ex) {  
		            ex.printStackTrace();  
		        }  
				String download =serverText.getText();
				String save = clientText.getText();
	        	download = download.replace("\\","\\\\");
	        	save = save.replace("\\", "\\\\");
	        	save = save + "\\\\";
	        	if (getMessage(download, save)){
					JOptionPane.showMessageDialog(null, "Successfully Download!", "File Status", JOptionPane.INFORMATION_MESSAGE);
				}
				else{
					JOptionPane.showMessageDialog(null, "Something must be wrong!", "File Status", JOptionPane.INFORMATION_MESSAGE);
				}
				
			}
		});
		
		escButton.addActionListener (new ActionListener (){
			public void actionPerformed(ActionEvent e) {
                int exi = JOptionPane.showConfirmDialog (null, "Do you want to close client?", "Click a button", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (exi == JOptionPane.YES_OPTION){
                	shutdown();
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
  
    
    
    private boolean getMessage(String downloadFile, String savePath) {  
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
            return true;
        } catch (Exception e) {  
            System.out.println("Wrong information in receiving." + "\n");  
            return false;  
        }  
    }  
  
    public static void main(String arg[]) {
    	new Client();
 /*
    	while (true){
        	Scanner scanner = new Scanner(System.in);
        	System.out.println("Please enter the upload file path(end to close):");
        	String download = scanner.nextLine();
        	if (download.equals("end")) {
        		client.shutdown();
        		scanner.close();
        		break;
        	}
        	System.out.println("Please enter the save file path:");
        	String save = scanner.nextLine(); 	
        	download = download.replace("\\","\\\\");
        	save = save.replace("\\", "\\\\");
            client.getMessage(download, save);
    	}   */ 
    }  

}  
