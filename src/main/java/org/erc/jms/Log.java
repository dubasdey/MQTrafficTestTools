package org.erc.jms;

public class Log {

	public static void log(String str){
		System.out.println(str);
	}
	
	public static void err(String str){
		System.err.println(str);
	}	
	
	public static void err(Throwable t){
		System.err.println(t.getMessage());
		t.printStackTrace(System.err);
	}	
}
