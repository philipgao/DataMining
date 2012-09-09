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
		int index=0;
		FPNode child = root.getChild(itemList.get(index));

		FPNode node=child;
		if(child!=null){
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
			
		}else{
			node = root.addChild(itemList.get(index));
			
			index++;
		}

		while(index<itemList.size()){
			String item = itemList.get(index);
			FPNode newChild = node.addChild(item);
			headerTable.get(item).add(newChild);
			
			node=newChild;
			index++;
		}
	}
}
