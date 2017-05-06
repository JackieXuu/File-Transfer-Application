package com.udp;

public class UDPUtil {

	private UDPUtil(){};
	public static final int BUFFER_SIZE = 8 * 1024;
	
	public static int PORT = 8821;
	
	public static final byte[] success = "success".getBytes();
	
	public static final byte[] exit = "exit".getBytes();
	
    public static boolean isEquals(byte[] buf1,byte[] buf2,int len){  
        if (buf2 == null || buf2.length == 0 || buf2.length < len || buf1.length < len)  
            return false;  
          
        boolean flag = true;  
          
        int minlen = Math.min(buf1.length, len);  
            for (int i = 0; i < minlen; i++) {    
                if(buf2[i] != buf1[i]){  
                    flag = false;  
                    break;  
                }  
            }  
        return flag;  
    }  
	
	
}
