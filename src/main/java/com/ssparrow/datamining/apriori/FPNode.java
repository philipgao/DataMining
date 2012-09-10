/**
 * 
 */
package com.ssparrow.datamining.apriori;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gao, Fei
 *
 */
public class FPNode {
	private String item;
	private int count = 0;
	
	private List<FPNode> children = new ArrayList<FPNode>();
	private Map<String, FPNode> childrenMap=new LinkedHashMap<String, FPNode>();
	private List<FPNode> path =  new ArrayList<FPNode>();
	
	/**
	 * @param item
	 */
	public FPNode(String item) {
		super();
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
		FPNode child=new FPNode(Item);
		this.addChild(child);
		return child;
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

	@Override
	protected Object clone() throws CloneNotSupportedException {
		FPNode clone=new FPNode(this.getItem());
		clone.setCount(this.getCount());
		return clone;
	}

	@Override
	public String toString() {
		return "[" + item + ":" + count + "]";
	}
	
	
	
	
	
}
