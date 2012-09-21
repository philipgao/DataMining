/**
 * 
 */
package com.ssparrow.datamining.association.fpgrowth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Gao, Fei
 *
 */
public class FPTree {
	private List<String> singleCandidates;
	private FPNode root=new FPNode(this, "");
	private Map<String, List<FPNode>> headerTable;
	private boolean isSingleItemPruned;
	
	/**
	 * initialize header table
	 * 
	 */
	public FPTree(List<String> singleCandidates) {
		this(singleCandidates, true);
	}
	
	public FPTree(List<String> singleCandidates, boolean isSingleItemPruned) {
		this.isSingleItemPruned=isSingleItemPruned;
		
		if(isSingleItemPruned){
			headerTable=new LinkedHashMap<String, List<FPNode>>();
		}else{
			headerTable=new TreeMap<String, List<FPNode>>();
		}

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
	 * @return
	 */
	public boolean isEmpty(){
		return root.getChildren().size()==0;
	}
	
	/**
	 * add frequent items in a transaction to the FP Tree
	 * 
	 * @param itemList
	 */
	public void addToTree(String tid, List<String> itemList){
		int index=0;
		FPNode child = root.getChildByItem(itemList.get(index));

		FPNode node=child;
		if(child!=null){
			while(child!=null){
				child.addTransaction(tid);
				
				node=child;
				
				index++;
				if(index<itemList.size()){
					child=node.getChildByItem(itemList.get(index));
				}else{
					break;
				}
			}
			
		}else{
			String item = itemList.get(index);
			
			node = root.addChild(item);
			node.addTransaction(tid);
			
			index++;
		}

		while(index<itemList.size()){
			String item = itemList.get(index);
			
			FPNode newChild = node.addChild(item);
			newChild.addTransaction(tid);
			
			node=newChild;
			index++;
		}
	}
	
	/**
	 * @param item
	 * @param node
	 */
	public void addNodeToHeaderTable(FPNode node){
		String item=node.getItem();
		List<FPNode> itemNodeList = headerTable.get(item);
		if(itemNodeList==null){
			itemNodeList=new LinkedList<FPNode>();
			headerTable.put(item, itemNodeList);
			
			singleCandidates.add(item);
		}
		itemNodeList.add(node);
	}
	
	/**
	 * @param itemList
	 */
	public void addClonedNodesToTree(List<FPNode> itemList){
		int index=0;
		FPNode child = root.getChildByItem(itemList.get(index).getItem());

		FPNode node=child;
		if(child!=null){
			while(child!=null){
				child.addTransaction(itemList.get(index).getTransactionSet());
				
				node=child;
				
				index++;
				if(index<itemList.size()){
					child=node.getChildByItem(itemList.get(index).getItem());
				}else{
					break;
				}
			}
			
		}else{
			node = root.addChild(itemList.get(index).getItem());
			node.addTransaction(itemList.get(index).getTransactionSet());
			
			index++;
		}

		while(index<itemList.size()){
			String item = itemList.get(index).getItem();
			
			FPNode newChild = node.addChild(item);
			newChild.addTransaction(itemList.get(index).getTransactionSet());
			
			node=newChild;
			index++;
		}
	}
	
	public List<String> getSingleCandidates() {
		return singleCandidates;
	}
	
	/**
	 * @param item
	 * @return
	 */
	public int getItemCount(String item){
		List<FPNode> itemNodeList = headerTable.get(item);
		
		int count=0;
		for(FPNode node:itemNodeList){
			count+=node.getTransactionSet().size();
		}
		
		return count;
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
			
			Set<String> transactionSet = path.get(path.size()-1).getTransactionSet();
			//clonedPath.add(0, (FPNode) path.get(path.size()-1).clone());
			
			for(int i=path.size()-2;i>=0;i--){
				try {
					FPNode clone = (FPNode)path.get(i).clone();
					clone.setTransactionSet(transactionSet);
					clonedPath.add(0, clone);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
			
			conditionalPatternBase.add(clonedPath);
		}
		
		return conditionalPatternBase;
	}
	
	/**
	 * @param node
	 */
	public void removeFromHeaderTable(FPNode node){
		headerTable.get(node.getItem()).remove(node);
	}
	
	/**
	 * @return
	 */
	public boolean isSinglePathTree(){
		FPNode node=root;
		
		while(node.getChildren().size()<=1){
			if(node.getChildren().size()==0){
				return true;
			}else{
				node=node.getChildren().get(0);
			}
		}
		
		return false;
	}
	
	/**
	 * filter the tree to remove the single items in the tree that count less than the assigned threshold
	 * 
	 * please notice that filter is not necessary for the initial FP Tree since all the single items in it are frequent single item
	 * This is needed when we create FP Tree from Conditional Pattern Base, because it is just a sub-tree of the total FP Tree 
	 * 
	 * @param threshold
	 */
	public void filterTree(int threshold){
		
		if(!isSingleItemPruned){
			Collections.sort(singleCandidates);
		}
		
		List<String> deleteList=new ArrayList<String>();
		for(int index=singleCandidates.size()-1;index>=0;index--){
			String item=singleCandidates.get(index);
			List<FPNode> itemNodeList = headerTable.get(item);
			
			if(itemNodeList.size()>0){
				int count=0;
				for(FPNode node:itemNodeList){
					count+=node.getTransactionSet().size();
				}
				
				if(count<threshold){
					deleteList.add(item);
					pruneTree(item);
				}
			}
		}
		singleCandidates.removeAll(deleteList);
	}
	
	/**
	 * filter tree based on new single item candidates
	 * this happens when we build the tree with arbitrary order and then reconstruct it using frequent item order
	 * 
	 * @param frequentSingleItems
	 */
	public void filterTree(List<String> frequentSingleItems){
		List<String> deleteList=new ArrayList<String>();
		for(int index=singleCandidates.size()-1;index>=0;index--){
			String item=singleCandidates.get(index);
			if(!frequentSingleItems.contains(item) && headerTable.get(item).size()>0){
				deleteList.add(item);
				pruneTree(item);
			}
		}
		singleCandidates.removeAll(deleteList);
	}

	/**
	 * @param item
	 * @param itemNodeList
	 */
	private void pruneTree(String item) {
		List<FPNode> itemNodeList = headerTable.get(item);
		
		for(FPNode node:itemNodeList){
			List<FPNode> path = node.getPath();
			FPNode parent=path.size()>=2? path.get(path.size()-2): root;
			
			//remove this node as child of its parent
			parent.removeChild(node);

			//remove this node from path of all its descendants
			List<FPNode> children=node.getChildren();
			Queue<FPNode> queue=new LinkedList<FPNode>();
			queue.addAll(children);
			while(!queue.isEmpty()) {
				FPNode descendant = queue.poll();
				descendant.removeFromPath(node);
				queue.addAll(descendant.getChildren());
			}
			
			parent.addAndMergeChildren(children);
		}
		
		this.headerTable.put(item, new LinkedList<FPNode>());
	}

	/**
	 * merge another tree into current tree
	 * this is the base operation for distributed pattern, which requires we handle locally build tree on each node
	 * 
	 * @param targetTree
	 */
	public void mergeTree(FPTree targetTree){
		root.mergeSubTree(targetTree.getRoot());
	}

	@Override
	public String toString() {
	    StringBuffer sb= new StringBuffer();
	    
	    sb.append("{");
	    FPNode node = this.root;
	    List<FPNode> children = node.getChildren();
	    for(FPNode child:children){
		sb.append(child.toString());
	    }
	    sb.append("}");
	    
	    return sb.toString();
	}
	
}
