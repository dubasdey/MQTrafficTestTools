package org.erc.jms;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

public class Receiver {
	public static void main(String[] args) {
		
		Log.log("Reading properties");
		if(args.length<5){
			System.out.println("Invalid number of parameters");
			System.out.println("required: <host> <port> <channel> <manager> <queue> <threads> <threadDelay>");
			System.exit(1);
		}
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String channel = args[2];
		String manager = args[3];
		String queue = args[4];

		int threads = 1;
		int threadDelay = 1000;
		if(args.length>5){
			threads = Integer.parseInt(args[5]);
		}
		if(args.length>6){
			threadDelay = Integer.parseInt(args[6]); //ms
		}
		
		try {
			MQEnvironment.hostname = host;
			MQEnvironment.channel = channel;
			MQEnvironment.port = port;
			
			MQRun.queueName = queue ;
			MQRun.managerName = manager;
			MQRun.threadStop = threadDelay;
					
			Log.log("Starting Thread group");
			ThreadGroup tgroup = new ThreadGroup("JMS-RECEIVE");
			tgroup.setDaemon(true);
			tgroup.setMaxPriority(Thread.MAX_PRIORITY);
			
			for(int i=0;i<threads;i++){
				Log.log("Starting Thread " + i);
				Thread tread = new Thread(tgroup,new MQRun(),"tr-receive-" + i);
				tread.start();
			}
			Log.log("All threads started. Press Ctrl+C to stop");
			System.in.read();
			
		} catch (Exception e) {
			Log.err(e.getMessage());	
		}
	}
	
	
	public static class MQRun implements Runnable{

		private static String queueName;
		private static String managerName;
		private static int threadStop;
		
		@Override
		public void run() {
			
			MQQueueManager manager = null;
			MQQueue queue = null;
			
			try {
				manager = new MQQueueManager(managerName);
				queue = manager.accessQueue(queueName, MQC.MQOO_INPUT_SHARED + MQC.MQOO_FAIL_IF_QUIESCING,null, null, null );
	
				while(true){
					MQMessage theMessage    = new MQMessage();
					MQGetMessageOptions gmo = new MQGetMessageOptions();
					queue.get(theMessage,gmo); 
					
					int len =theMessage.getMessageLength();		
					byte[] strData = new byte[len];
					
					theMessage.readFully(strData,0,len);
					String name = new String(strData);
					Log.log("Message received:" + name);
					Thread.sleep(threadStop);
				}
			} catch (Exception e) {
				Log.err("MQGetter:" + e.getMessage());
			} 
		}
	}	
}
