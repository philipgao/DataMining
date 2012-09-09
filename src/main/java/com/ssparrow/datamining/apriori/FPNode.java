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
	
}
