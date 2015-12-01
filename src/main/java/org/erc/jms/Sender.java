package org.erc.jms;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

public class Sender {

	public static void main(String[] args) {
		
		Log.log("Reading properties");
		if(args.length<6){
			System.out.println("Invalid number of parameters");
			System.out.println("required: <host> <port> <channel> <manager> <queue> <file> <threads> <threadDelay>");
			System.exit(1);
		}
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String channel = args[2];
		String manager = args[3];
		String queue = args[4];
		String file = args[5];
		
		int threads = 1;
		int threadDelay = 1000;
		if(args.length>6){
			threads = Integer.parseInt(args[6]);
		}
		if(args.length>7){
			threadDelay = Integer.parseInt(args[7]); //ms
		}
		
		try {
			MQEnvironment.hostname = host;
			MQEnvironment.channel = channel;
			MQEnvironment.port = port;
			
			MQRun.queueName = queue ;
			MQRun.managerName = manager;
			MQRun.xmlString  = Util.getStringFromInputStream(file);
			if(MQRun.xmlString == null){
				Log.err("Invalid file");
				System.exit(1);
			}
			MQRun.threadStop = threadDelay;
					
			Log.log("Starting Thread group");
			ThreadGroup tgroup = new ThreadGroup("JMS-SEND");
			tgroup.setDaemon(true);
			tgroup.setMaxPriority(Thread.MAX_PRIORITY);
			
			for(int i=0;i<threads;i++){
				Log.log("Starting Thread " + i);
				Thread tread = new Thread(tgroup,new MQRun(),"tr-send-" + i);
				tread.start();
			}
			Log.log("All threads started. Press Ctrl+C to stop");
			System.in.read();
			
		} catch (Exception e) {
			Log.err(e.getMessage());	
		}		
	}
	
	public static class MQRun implements Runnable{  
		
		private static String xmlString;
		private static String queueName;
		private static String managerName;
		private static int threadStop;
		
		@Override
		public void run() {
			MQQueueManager manager = null;
			MQQueue queue = null;
			try {
				MQPutMessageOptions pmo = new MQPutMessageOptions();
				manager = new MQQueueManager(managerName);
				queue = manager.accessQueue(queueName, MQC.MQOO_OUTPUT + MQC.MQOO_FAIL_IF_QUIESCING,null, null, null );
				while(true){
					try{
						MQMessage sendmsg = new MQMessage();
						sendmsg.format = MQC.MQFMT_STRING;
						sendmsg.feedback = MQC.MQFB_NONE;
						sendmsg.messageType = MQC.MQMT_DATAGRAM;
						sendmsg.clearMessage();
						sendmsg.messageId = MQC.MQMI_NONE;
						sendmsg.correlationId = MQC.MQCI_NONE;
						sendmsg.writeString(xmlString);
						queue.put(sendmsg, pmo);
						Log.log("Message sent");
					}catch(Throwable t){
						Log.err(t);
					}
					Thread.sleep(threadStop);
				}
			} catch (Exception e) {
				Log.err(e.getMessage());
			} finally{
				if(queue!=null){
					try { queue.close(); } catch (MQException e) { }
				}
				if(manager!=null){
					try { manager.disconnect(); } catch (MQException e) { }
				}
			}
		}
	}	

}
