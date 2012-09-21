/**
 * 
 */
package com.ssparrow.datamining.association.fpgrowth.distributed;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.ReceiverAdapter;

/**
 * @author Gao, Fei
 *
 */
public class DistributedPatternDiscoveryMaster extends DistributedPatternDiscoveryNode{
	private Map<Address, String> children=new HashMap<Address, String>();
	private int threshold;
	
	public DistributedPatternDiscoveryMaster(String nodeName, String fileName, int threshold) {
		super(nodeName, fileName);
		this.threshold=threshold;
	}
	
	
	/* (non-Javadoc)
	 * @see com.ssparrow.datamining.association.fpgrowth.distributed.DistributedPatternDiscoveryNode#createrReceiver()
	 */
	@Override
	protected Receiver createrReceiver() {
		return new MasterNodeMessageProcessor();
	}
	
	/* (non-Javadoc)
	 * @see com.ssparrow.datamining.association.fpgrowth.distributed.DistributedPatternDiscoveryNode#run()
	 */
	public void run() throws Exception{
		Thread.sleep(60*1000);
	}
	
	public static void main(String []args) throws Exception{
		DistributedPatternDiscoveryMaster node= new DistributedPatternDiscoveryMaster(args[0], args[1], Integer.parseInt(args[2]));
		node.start();
	}

	private class MasterNodeMessageProcessor extends ReceiverAdapter{
		
		/* (non-Javadoc)
		 * @see org.jgroups.ReceiverAdapter#receive(org.jgroups.Message)
		 */
		@Override
		public void receive(Message msg) {
			String content  = msg.getObject().toString();
			System.out.println(content);
			
			if(content.startsWith("NEW_NODE:")){
				String newNodeName = content.substring("NEW_NODE:".length());
				children.put(msg.getSrc(),newNodeName);
				
				Message newMessage=new Message(null, null, "MASTER_NODE:"+nodeName+",THRESHOLD:"+threshold);
				try {
					channel.send(newMessage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
