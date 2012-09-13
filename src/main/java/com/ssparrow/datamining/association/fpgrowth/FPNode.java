/**
 * 
 */
package com.ssparrow.datamining.association.fpgrowth;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gao, Fei
 *
 */
public class FPNode {
	private FPTree fpTree;
	private String item;
	private int count = 0;
	
	private List<FPNode> children = new ArrayList<FPNode>();
	private Map<String, FPNode> childrenMap=new LinkedHashMap<String, FPNode>();
	private List<FPNode> path =  new ArrayList<FPNode>();
	
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
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}
	
	/**
	 * @param amount
	 */
	public void increaseCount(int amount){
		this.count = this.count+amount;
	}

	
	/**
	 * @return
	 */
	public int getCount() {
		return count;
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
		
		List<FPNode> childPath=new ArrayList<FPNode>(this.path);
		childPath.add(child);
		child.setPath(childPath);
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
				existingChild.increaseCount(newChild.getCount());
				
				for(FPNode nextLevelNode:newChild.getChildren()){
					nextLevelNode.replaceInPath(newChild, existingChild);
				}

				existingChild.addAndMergeChildren(newChild.getChildren());
				
				newChild.getFpTree().removeFromHeaderTable(newChild);
				
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

	@Override
	protected Object clone() throws CloneNotSupportedException {
		FPNode clone=new FPNode(this.fpTree, this.getItem());
		clone.setCount(this.getCount());
		return clone;
	}

	public FPTree getFpTree() {
		return fpTree;
	}

	@Override
	public String toString() {
		return "[" + item + ":" + count + "]";
	}
	
	
	
	
	
}
