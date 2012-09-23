/**
 * 
 */
package com.ssparrow.datamining.association.fpgrowth.distributed;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.ReceiverAdapter;

import com.ssparrow.datamining.association.fpgrowth.FPTree;

/**
 * @author Gao, Fei
 *
 */
public class DistributedPatternDiscoveryNode {
	protected String nodeName;
	protected String fileName;
	protected Address masterAddress;
	protected int threshold;
	protected Map<String, List<String>> transactions=new LinkedHashMap<String, List<String>>();
	
	protected JChannel channel;
	
	protected Lock lock;
	protected Condition condition;
	
	/**
	 * @param nodeName
	 * @param fileName
	 */
	public DistributedPatternDiscoveryNode(String nodeName, String fileName) {
		this.nodeName = nodeName;
		this.fileName = fileName;
	}

	private void init() throws Exception{
		this.lock=new ReentrantLock();
		this.condition=lock.newCondition();
		
		channel=new JChannel();
		channel.setReceiver(createrReceiver());
		channel.connect("DistributedPatternDiscovery");
		
		BufferedReader reader=new BufferedReader(new FileReader(fileName));
		String line;
		
		while((line=reader.readLine())!=null){
			StringTokenizer st=new StringTokenizer(line);
			
			String tid = st.nextToken();
			List<String> transaction=new ArrayList<String>();
			while(st.hasMoreTokens()){
				transaction.add(st.nextToken());
			}
			
			transactions.put(tid, transaction);
		}
	}
	
	protected Receiver createrReceiver(){
		return new PatternDiscoveryMessageProcessor();
	}
	
	public void start() throws Exception{
		init();
		
		run();
	}
	
	public void run() throws Exception{
		Message message=new Message(null, null, "NEW_NODE:"+nodeName);
		channel.send(message);
		
		lock.lock();
		
		System.out.println("waiting for master node information and threshold");
		while(masterAddress==null){
			condition.await();
		}
		
		lock.unlock();
		
		System.out.println("starting pattern discovery on node "+nodeName);
		List<String> singleCandidates=new ArrayList<String>();
		
		FPTree fpTree=new FPTree(singleCandidates,false);
		for(String tid:transactions.keySet()){
			List<String> transaction =transactions.get(tid);
		    Collections.sort(transaction);
		    
		    fpTree.addToTree(tid, transaction);
		}
		
		System.out.println("local FP Tree is bult, sending string representation of local tree to master node");
		message=new Message(masterAddress, null, "FP_TREE:"+fpTree.toString());
		channel.send(message);
	}
	
	
	public static void main(String []args) throws Exception{
		DistributedPatternDiscoveryNode node= new DistributedPatternDiscoveryNode(args[0], args[1]);
		node.start();
		
	}


	private class PatternDiscoveryMessageProcessor extends ReceiverAdapter{
	
		/* (non-Javadoc)
		 * @see org.jgroups.ReceiverAdapter#receive(org.jgroups.Message)
		 */
		@Override
		public void receive(Message msg) {
			String THRESHOLD="THRESHOLD:";
			
			String content  = msg.getObject().toString();
			
			if(content.startsWith("MASTER_NODE:")){
				masterAddress = msg.getSrc();
				
				String thresholdStr=content.substring(content.indexOf(THRESHOLD)+THRESHOLD.length());
				threshold=Integer.parseInt(thresholdStr);
				
				lock.lock();
				condition.signalAll();
				lock.unlock();
			}
		}
		
	}
}

