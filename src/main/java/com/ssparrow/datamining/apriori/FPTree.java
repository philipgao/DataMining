/**
 * 
 */
package com.ssparrow.datamining.apriori;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Gao, Fei
 *
 */
public class FPTree {
	private List<String> singleCandidates;
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
		this.singleCandidates=singleCandidates;
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
			newChild.increaseCount(1);
			
			headerTable.get(item).add(newChild);
			
			node=newChild;
			index++;
		}
	}
	
	
	public List<String> getSingleCandidates() {
		return singleCandidates;
	}

	/**
	 * get conditional pattern base for specified item
	 * clone all the nodes to be used from creating new FP Tree
	 * @param item
	 * @return
	 * @throws CloneNotSupportedException 
	 */
	public List<List<FPNode>> getConditionalPatternBase(String item){
		List<List<FPNode>> conditionalPatternBase=new ArrayList<List<FPNode>>();
		
		List<FPNode> itemNodeList = headerTable.get(item);
		List<List<FPNode>> pathList=new ArrayList<List<FPNode>>();
		for(FPNode itemNode: itemNodeList){
			pathList.add(itemNode.getPath());
		}
		
		for(List<FPNode> path:pathList){
			List<FPNode> clonedPath=new ArrayList<FPNode>();
			
			int count=path.get(path.size()-1).getCount();
			//clonedPath.add(0, (FPNode) path.get(path.size()-1).clone());
			
			for(int i=path.size()-2;i>=0;i--){
				try {
					FPNode clone = (FPNode)path.get(i).clone();
					clone.setCount(count);
					clonedPath.add(0, clone);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
			
			conditionalPatternBase.add(clonedPath);
		}
		
		return conditionalPatternBase;
	}
}
