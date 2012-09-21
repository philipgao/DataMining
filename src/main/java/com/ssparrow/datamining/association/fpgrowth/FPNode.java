/**
 * 
 */
package com.ssparrow.datamining.association.fpgrowth;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * @author Gao, Fei
 *
 */
public class FPNode {
	private FPTree fpTree;
	private FPNode parent;
	private String item;
	
	private List<FPNode> children = new ArrayList<FPNode>();
	private Map<String, FPNode> childrenMap=new LinkedHashMap<String, FPNode>();
	private List<FPNode> path =  new ArrayList<FPNode>();
	
	private Set<String> transactionSet=new HashSet<String>();
	private Map<String, FPNode> transactionChildrenMap=new LinkedHashMap<String, FPNode>();
	
	/**
	 * @param item
	 */
	public FPNode(FPTree fpTree, String item) {
		this.fpTree=fpTree;
		this.parent=parent;
		this.item = item;
	}
	
	/**
	 * @return the item
	 */
	public String getItem() {
		return item;
	}
	
	/**
	 * @return the parent
	 */
	public FPNode getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(FPNode parent) {
		this.parent = parent;
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
		
		parent.addChildTransaction(tid, this);
	}
	
	/**
	 * @param newTransactions
	 */
	public void addTransaction(Set<String> transactionSet){
		this.transactionSet.addAll(transactionSet);
		
		for(String tid:transactionSet){
			parent.addChildTransaction(tid, this);
		}
	}
	
	/**
	 * @param tid
	 */
	public void removeTransaction(String tid){
		transactionSet.remove(tid);
		
		parent.removeChildTransaction(tid);
	}
	
	/**
	 * @param newTransactions
	 */
	public void removeTransaction(Set<String> transactionSet){
		this.transactionSet.removeAll(transactionSet);
		
		for(String tid:transactionSet){
			parent.removeChildTransaction(tid);
		}
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
		child.setParent(this);
		
		children.add(child);
		childrenMap.put(child.getItem(), child);
		
		for(String tid: child.getTransactionSet()){
			transactionChildrenMap.put(tid, child);
		}
		
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

		for(String tid: child.getTransactionSet()){
			transactionChildrenMap.remove(tid);
		}
	}
	
	/**
	 * @param tid
	 * @param child
	 */
	public void addChildTransaction(String tid, FPNode child){
		transactionChildrenMap.put(tid, child);
	}
	
	/**
	 * @param tid
	 */
	public void removeChildTransaction(String tid){
		transactionChildrenMap.remove(tid);
	}
	
	/**
	 * add list of FP Nodes as children of current node, merge with exisiting child if they are about the same item
	 * 
	 * @param addedChildren
	 */
	public void addAndMergeChildren(List<FPNode> addedChildren){
		for(FPNode newChild:addedChildren){
			String childItem = newChild.getItem();
			FPNode existingChild = this.getChildByItem(childItem);
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

				for(String tid: newChild.getTransactionSet()){
					transactionChildrenMap.put(tid, newChild);
				}
			}
		}
	}
	
	/**
	 * @param item
	 * @return
	 */
	public FPNode getChildByItem(String item){
		return childrenMap.get(item);
	}
	
	/**
	 * @param tid
	 * @return
	 */
	public FPNode getChildByTransaction(String tid){
		return transactionChildrenMap.get(tid);
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
	 * @param tid
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public Map<String, FPNode> cloneTransactionWithChildren(String tid) throws CloneNotSupportedException{
		Map<String, FPNode> path=new LinkedHashMap<String, FPNode>();
		
		FPNode clone = (FPNode) this.clone();
		path.put(clone.getItem(), clone);
		
		HashSet<String> singleTransactionSet = new HashSet<String>();
		singleTransactionSet.add(tid);
		clone.setTransactionSet(singleTransactionSet);
		
		FPNode childByTransaction = this.getChildByTransaction(tid);
		while(childByTransaction!=null){
			FPNode childClone = (FPNode)childByTransaction.clone();
			
			HashSet<String> childTransactionSet = new HashSet<String>();
			childTransactionSet.add(tid);
			childClone.setTransactionSet(childTransactionSet);
			
			clone.addChild(childClone);
			
			clone=childClone;
			
			path.put(clone.getItem(), clone);
			
			childByTransaction=childByTransaction.getChildByTransaction(tid);
		}
		
		return path;
	}
	
	/**
	 * @param tid
	 * @param item
	 * @return
	 */
	public FPNode getDescendant(String tid, String item){
		FPNode childByTransaction = this.getChildByTransaction(tid);
		
		while(childByTransaction !=null && !childByTransaction.getItem().equals(item)){
			childByTransaction=childByTransaction.getChildByTransaction(tid);
		}
		
		return childByTransaction;
		
	}
	
	public void removeTransactionInSubpath(String tid){
		Stack<FPNode> descendants=new Stack<FPNode>();
		descendants.push(this);
		
		FPNode childByTransaction = this.getChildByTransaction(tid);
		while(childByTransaction!=null){
			descendants.push(childByTransaction);
			
			childByTransaction=childByTransaction.getChildByTransaction(tid);
		}
		
		FPNode nodeToDelete=null;
		while(!descendants.isEmpty()){
			FPNode node = descendants.pop();
			node.removeTransaction(tid);
			
			if(nodeToDelete!=null){
				node.removeChild(nodeToDelete);
			}
			
			if(node.getCount()==0){
				nodeToDelete=node;
			}
			
		}
		
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
				FPNode child = this.getChildByItem(targetChild.getItem());
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
