/**
 * 
 */
package com.ssparrow.datamining.association.fpgrowth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Gao, Fei
 *
 */
public class FPNode {
	private FPTree fpTree;
	private String item;
	
	private List<FPNode> children = new ArrayList<FPNode>();
	private Map<String, FPNode> childrenMap=new LinkedHashMap<String, FPNode>();
	private List<FPNode> path =  new ArrayList<FPNode>();
	
	private Set<String> transactionSet=new HashSet<String>();
	
	/**
	 * @param item
	 */
	public FPNode(FPTree fpTree, String item) {
		this.fpTree=fpTree;
		this.item = item;
	}
	
	/**
	 * @return the item
	 */
	public String getItem() {
		return item;
	}

	
	/**
	 * @return
	 */
	public int getCount() {
		return transactionSet.size();
	}
	
	
	/**
	 * @return the transactionSet
	 */
	public Set<String> getTransactionSet() {
		return transactionSet;
	}

	/**
	 * @param transactionSet
	 */
	public void setTransactionSet(Set<String> transactionSet){
		this.transactionSet=transactionSet;
	}
	
	/**
	 * @param tid
	 */
	public void addTransaction(String tid){
		transactionSet.add(tid);
	}
	
	/**
	 * @param newTransactions
	 */
	public void addTransaction(Set<String> transactionSet){
		this.transactionSet.addAll(transactionSet);
	}
	
	/**
	 * @param tid
	 */
	public void removeTransaction(String tid){
		transactionSet.remove(tid);
	}
	
	/**
	 * @param newTransactions
	 */
	public void removeTransaction(Set<String> transactionSet){
		this.transactionSet.removeAll(transactionSet);
	}
	
	/**
	 * @param tid
	 * @return
	 */
	public boolean hasTransaction(String tid){
		return transactionSet.contains(tid);
	}

	/**
	 * @return the children
	 */
	public List<FPNode> getChildren() {
		return children;
	}
	
	/**
	 * @param child
	 */
	public void addChild(FPNode child){
		children.add(child);
		childrenMap.put(child.getItem(), child);
		
		child.setFpTree(this.getFpTree());
		
		child.updatePath(this);
		
		child.addSubTreeToHeadertable();
	}
	
	/**
	 * add node to header table of dp tree
	 * this usually happens when we merge nodes in one tree to another
	 */
	public void addSubTreeToHeadertable(){
		this.getFpTree().addNodeToHeaderTable(this);
		
		for(FPNode child:this.getChildren()){
			child.addSubTreeToHeadertable();
		}
	}
	
	/**
	 * @param Item
	 */
	public FPNode addChild(String Item){
		FPNode child=new FPNode(this.fpTree, Item);
		this.addChild(child);
		return child;
	}
	
	/**
	 * @param child
	 */
	public void removeChild(FPNode child){
		children.remove(child);
		childrenMap.remove(child.getItem());
	}
	
	/**
	 * add list of FP Nodes as children of current node, merge with exisiting child if they are about the same item
	 * 
	 * @param addedChildren
	 */
	public void addAndMergeChildren(List<FPNode> addedChildren){
		for(FPNode newChild:addedChildren){
			String childItem = newChild.getItem();
			FPNode existingChild = this.getChild(childItem);
			if(existingChild!=null){
				existingChild.addTransaction(newChild.getTransactionSet());
				
				for(FPNode nextLevelNode:newChild.getChildren()){
					nextLevelNode.replaceInPath(newChild, existingChild);
				}

				existingChild.addAndMergeChildren(newChild.getChildren());
				
				newChild.getFpTree().removeFromHeaderTable(newChild);
				
			}else{
			    children.add(newChild);
			    childrenMap.put(newChild.getItem(), newChild);
			}
		}
	}
	
	/**
	 * @param item
	 * @return
	 */
	public FPNode getChild(String item){
		return childrenMap.get(item);
	}

	/**
	 * @return the path
	 */
	public List<FPNode> getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(List<FPNode> path) {
		this.path = path;
	}
	
	/**
	 * @param node
	 */
	public void removeFromPath(FPNode node){
		path.remove(node);
	}
	
	/**
	 * @param srcNode
	 * @param tgtNode
	 */
	public void replaceInPath(FPNode srcNode, FPNode tgtNode){
		int index = path.indexOf(srcNode);
		
		if(index>=0){
			path.remove(srcNode);
			path.add(index, tgtNode);
		}
	}
	
	/**
	 * @param parent
	 */
	public void updatePath(FPNode parent){
		List<FPNode> parentPath = parent.getPath();
		List<FPNode> newPath=new ArrayList<FPNode>(parentPath);
		newPath.add(this);
		this.path=newPath;
		
		for(FPNode child:children){
			child.updatePath(this);
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		FPNode clone=new FPNode(this.fpTree, this.getItem());
		clone.setTransactionSet(transactionSet);
		return clone;
	}

	/**
	 * clone current node and all the node under it
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public FPNode cloneWithChildren() throws CloneNotSupportedException{
		FPNode clone = (FPNode) this.clone();
		
		for(FPNode child:children){
			FPNode childClone = (FPNode) child.cloneWithChildren();
			clone.addChild(childClone);
		}
		
		return clone;
	}
	
	/**
	 * merge this node with target node, along with the sub-tree below each node
	 * 
	 * @param targetNode
	 */
	public void mergeSubTree(FPNode targetNode){
		if(this.getItem().equals(targetNode.getItem())){
			this.addTransaction(targetNode.getTransactionSet());
			
			List<FPNode> targetChildren = targetNode.getChildren();

			for(FPNode targetChild:targetChildren){
				FPNode child = this.getChild(targetChild.getItem());
				if(child!=null){
					child.mergeSubTree(targetChild);
				}else{
					try {
						FPNode cloneWithChildren = targetChild.cloneWithChildren();
						this.addChild(cloneWithChildren);
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * @return
	 */
	public FPTree getFpTree() {
		return fpTree;
	}

	/**
	 * @param fpTree
	 */
	public void setFpTree(FPTree fpTree) {
		this.fpTree = fpTree;
	}

	@Override
	public String toString() {
	    StringBuffer sb=new StringBuffer();
	    sb.append("{");
	    sb.append("[").append(item).append(":").append(transactionSet.size()).append("]");
	    for(FPNode child:children){
		sb.append(child.toString());
	    }
	    sb.append("}");
	    return sb.toString();
	}
	
	
	
	
	
}
