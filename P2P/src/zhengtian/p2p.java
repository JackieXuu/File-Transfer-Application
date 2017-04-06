package zhengtian;

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

public class p2p extends JFrame{
	JPanel panel = new JPanel();
	JPanel panel2 = new JPanel();
	JPanel panel1 = new JPanel();
	JLabel serverPath = new JLabel("Peer1");
	JTextField filePathsaveOnClient2Text = new JTextField();
	JTextField serverText = new JTextField();
	JLabel clientPath = new JLabel("Peer2");
	JTextField clientText = new JTextField();
	JTextField filePathsaveOnClient1Text = new JTextField();

	JLabel filePathClient1 = new JLabel("Path of file on peer1:");
	JLabel filePathClient2 = new JLabel("Path of file on peer2:");
	JLabel filePathsaveOnClient2 = new JLabel("Path to save on peer2:");
	JLabel filePathsaveOnClient1 = new JLabel("Path to save on peer1:");
	
	JTextField serverPort = new JTextField("5880");
	JTextField clientPort = new JTextField("5881");
	JButton downloadButton1 = new JButton("Send file to peer2");
	JButton downloadButton2 = new JButton("Send file to peer1");
	JButton escButton = new JButton("Esc"); 
    JButton filePathonClient1 = new JButton("...");
    JButton filePathonClient2 = new JButton("...");
    
    JButton filePathsaveonClient2 = new JButton("...");
    JButton filePathsaveonClient1 = new JButton("...");
    peer peer = new peer();
    private String ip = "localhost";
    
    public p2p(){
    	this.setTitle("Peer to Peer");  
		this.setLayout(new BorderLayout());
		this.setLocationRelativeTo(null);
		panel1.setLayout(new GridLayout(2, 2));
		panel1.add(serverPath);
		panel1.add(clientPath);
		panel1.add(serverPort);
		panel1.add(clientPort);
		this.add(panel1, BorderLayout.NORTH);
		panel.setLayout(new GridLayout(0, 6));
		panel.add(filePathClient1);
		panel.add(serverText);
		panel.add(filePathonClient1);
		
		panel.add(filePathClient2);
		panel.add(clientText);
		panel.add(filePathonClient2);
		
		
		panel.add(filePathsaveOnClient2);
		panel.add(filePathsaveOnClient2Text);
		panel.add(filePathsaveonClient2);
		
		panel.add(filePathsaveOnClient1);
		panel.add(filePathsaveOnClient1Text);
		panel.add(filePathsaveonClient1);
		

		this.add(panel, BorderLayout.CENTER);
		panel2.add(downloadButton1, BorderLayout.WEST);
		panel2.add(downloadButton2, BorderLayout.CENTER);
		panel2.add(escButton,  BorderLayout.EAST);
		this.add(panel2, BorderLayout.SOUTH);
		this.setSize(850,250);
		this.setVisible(true);
		
		downloadButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String download =serverText.getText();
				String save = filePathsaveOnClient2Text.getText();
				String port1 = clientPort.getText();
				int ClientPort =  Integer.parseInt(port1);
				String port2 = serverPort.getText();
				int ServerPort =  Integer.parseInt(port2);
	        	download = download.replace("\\","\\\\");
	        	save = save.replace("\\", "\\\\");
	        	save = save + "\\\\";
	        	try {
		        	peer.run1(download, ClientPort, ServerPort, save, ip);
		        	peer.flag1 = true;
					JOptionPane.showMessageDialog(null, "Successfully Download!", "File Status", JOptionPane.INFORMATION_MESSAGE);
	        	}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Something must be wrong!", "File Status", JOptionPane.INFORMATION_MESSAGE);
	        	}

			}
		});
		
		downloadButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String download =clientText.getText();
				String save = filePathsaveOnClient1Text.getText();
				String port1 = serverPort.getText();
				int ClientPort =  Integer.parseInt(port1);
				String port2 = clientPort.getText();
				int ServerPort =  Integer.parseInt(port2);
	        	download = download.replace("\\","\\\\");
	        	save = save.replace("\\", "\\\\");
	        	save = save + "\\\\";
	        	try {
		        	peer.run2(download, ClientPort, ServerPort, save, ip);
		        	peer.flag2 = true;
					JOptionPane.showMessageDialog(null, "Successfully Download!", "File Status", JOptionPane.INFORMATION_MESSAGE);
	        	}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Something must be wrong!", "File Status", JOptionPane.INFORMATION_MESSAGE);
	        	}
			}
		});
		
		escButton.addActionListener (new ActionListener (){
			public void actionPerformed(ActionEvent e) {
                int exi = JOptionPane.showConfirmDialog (null, "Do you want to close peers?", "Click a button", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (exi == JOptionPane.YES_OPTION){
                    System.exit (0);
                }
                else{
                    return;
                }
            }
        });
		
		filePathonClient1.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			JFileChooser jfc = new JFileChooser();
    			jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    				if(jfc.showOpenDialog(panel)==JFileChooser.APPROVE_OPTION ){
    					serverText.setText(jfc.getSelectedFile().getAbsolutePath());
    				}
    		}
    	});
		
		filePathsaveonClient2.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			JFileChooser jfc = new JFileChooser();
    			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    				if(jfc.showOpenDialog(panel)==JFileChooser.APPROVE_OPTION ){
    					filePathsaveOnClient2Text.setText(jfc.getSelectedFile().getAbsolutePath());
    				}
    		}
    	});
    	
		filePathonClient2.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			JFileChooser jfc = new JFileChooser();
    			jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    				if(jfc.showOpenDialog(panel)==JFileChooser.APPROVE_OPTION ){
    					clientText.setText(jfc.getSelectedFile().getAbsolutePath());
    				}
    		}
    	});
		
		filePathsaveonClient1.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			JFileChooser jfc = new JFileChooser();
    			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    				if(jfc.showOpenDialog(panel)==JFileChooser.APPROVE_OPTION ){
    					filePathsaveOnClient1Text.setText(jfc.getSelectedFile().getAbsolutePath());
    				}
    		}
    	});
    }
    
    public static void main(String[] args) {
        new p2p();
    }
}
