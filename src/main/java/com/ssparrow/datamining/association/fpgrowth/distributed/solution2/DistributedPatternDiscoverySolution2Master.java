/**
 * 
 */
package com.ssparrow.datamining.association.fpgrowth.distributed.solution2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.ReceiverAdapter;

import com.ssparrow.datamining.association.fpgrowth.FPGrowthAlgorithmWithoutPruning;
import com.ssparrow.datamining.association.fpgrowth.FPTree;
import com.ssparrow.datamining.association.fpgrowth.FPTreeUtil;

/**
 * @author Gao, Fei
 *
 */
public class DistributedPatternDiscoverySolution2Master extends DistributedPatternDiscoverySolution2Worker{
	private Map<Address, String> children=new HashMap<Address, String>();
	private Map<Address, FPTree> localTreeMap=new HashMap<Address, FPTree>();
	private int threshold;
	
	public DistributedPatternDiscoverySolution2Master(String nodeName, String fileName, int threshold) {
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
		while(children.size()==0){
			System.out.println("no worker connected yet wait for another 10 seconds");
			Thread.sleep(10*1000);
		}
		
		System.out.println("starting pattern discovery on node "+nodeName);
		List<String> singleCandidates=new ArrayList<String>();
		
		FPTree fpTree=new FPTree(singleCandidates,false);
		for(String tid:transactions.keySet()){
			List<String> transaction =transactions.get(tid);
		    Collections.sort(transaction);
		    
		    fpTree.addToTree(transaction);
		}
		System.out.println("master local tree:"+fpTree.toString());
		
		System.out.println("waiting for worker nodes to send their local trees");
		lock.lock();
		while(children.keySet().size()!=localTreeMap.keySet().size()){
			condition.await();
		}
		lock.unlock();
		
		List<FPTree> treeList=new ArrayList<FPTree>();
		treeList.add(fpTree);
		for(Address address:localTreeMap.keySet()){
			treeList.add(localTreeMap.get(address));
		}
		
		FPGrowthAlgorithmWithoutPruning fpGrowthAlgorithmWithoutPruning=new FPGrowthAlgorithmWithoutPruning();
		fpGrowthAlgorithmWithoutPruning.findFrequentItemSets(treeList, singleCandidates, threshold);
		Map<Set<String>, Integer> frequentItemSets = fpGrowthAlgorithmWithoutPruning.getFrequentItemSets(2);
		
		System.out.println("found frequrent item set with size equal or larger than 2 as followed:\n"+frequentItemSets);
	}
	
	public static void main(String []args) throws Exception{
		DistributedPatternDiscoverySolution2Master node= new DistributedPatternDiscoverySolution2Master(args[0], args[1], Integer.parseInt(args[2]));
		node.start();
	}

	private class MasterNodeMessageProcessor extends ReceiverAdapter{
		
		/* (non-Javadoc)
		 * @see org.jgroups.ReceiverAdapter#receive(org.jgroups.Message)
		 */
		@Override
		public void receive(Message msg) {
			String content  = msg.getObject().toString();
			
			System.out.println("recevice message - "+content);
			
			Address srfcAddress = msg.getSrc();
			
			if(content.startsWith("NEW_NODE:")){
				String newNodeName = content.substring("NEW_NODE:".length());
				children.put(srfcAddress,newNodeName);
				
				Message newMessage=new Message(null, null, "MASTER_NODE:"+nodeName+",THRESHOLD:"+threshold);
				try {
					channel.send(newMessage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(content.startsWith("FP_TREE:")){
				String fpTreeStr=content.substring("FP_TREE:".length());
				FPTree fpTree = FPTreeUtil.buildTreeFromStr(fpTreeStr);
				
				lock.lock();
				
				localTreeMap.put(srfcAddress, fpTree);
				
				condition.signalAll();
				lock.unlock();
			}
		}
		
	}
}
