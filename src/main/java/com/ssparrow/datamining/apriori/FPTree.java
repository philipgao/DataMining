/**
 * 
 */
package com.ssparrow.datamining.apriori;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Gao, Fei
 *
 */
public class FPTree {
	private FPNode root=new FPNode("");
	private Map<String, List<FPNode>> headerTable=new LinkedHashMap<String, List<FPNode>>();
	
	/**
	 * initialize header table
	 * 
	 */
	public FPTree(List<String> singleCandidates) {
		for(String item:singleCandidates){
			headerTable.put(item, new LinkedList<FPNode>());
		}
	}

	/**
	 * @return the root
	 */
	public FPNode getRoot() {
		return root;
	}
	
	/**
	 * add frequent items in a transaction to the FP Tree
	 * 
	 * @param itemList
	 */
	public void addToTree(List<String> itemList){
		FPNode child = root.getChild(itemList.get(0));
		
		if(child!=null){
			int index=0;
			FPNode node=child;
			
			while(child!=null){
				child.increaseCount(1);
				
				node=child;
				
				index++;
				if(index<itemList.size()){
					child=node.getChild(itemList.get(index));
				}else{
					break;
				}
			}
			
			while(index<itemList.size()){
				FPNode newChild = node.addChild(itemList.get(index));
				node=newChild;
			}
			
		}else{
			FPNode node = root.addChild(itemList.get(0));
			
			for(int index=1;index<itemList.size();index++){
				FPNode newChild = node.addChild(itemList.get(index));
				node=newChild;
			}
		}
	}
}
