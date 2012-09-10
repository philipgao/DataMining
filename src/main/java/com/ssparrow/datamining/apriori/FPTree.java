/**
 * 
 */
package com.ssparrow.datamining.apriori;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * @author Gao, Fei
 *
 */
public class FPTree {
	private List<String> singleCandidates;
	private FPNode root=new FPNode(this, "");
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
			node.increaseCount(1);
			
			headerTable.get(itemList.get(index)).add(node);
			
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
	
	/**
	 * @param itemList
	 */
	public void addClonedNodesToTree(List<FPNode> itemList){
		int index=0;
		FPNode child = root.getChild(itemList.get(index).getItem());

		FPNode node=child;
		if(child!=null){
			while(child!=null){
				child.increaseCount(itemList.get(index).getCount());
				
				node=child;
				
				index++;
				if(index<itemList.size()){
					child=node.getChild(itemList.get(index).getItem());
				}else{
					break;
				}
			}
			
		}else{
			node = root.addChild(itemList.get(index).getItem());
			node.increaseCount(itemList.get(index).getCount());
			
			headerTable.get(itemList.get(index).getItem()).add(node);
			
			index++;
		}

		while(index<itemList.size()){
			String item = itemList.get(index).getItem();
			
			FPNode newChild = node.addChild(item);
			newChild.increaseCount(itemList.get(index).getCount());
			
			headerTable.get(item).add(newChild);
			
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
			count+=node.getCount();
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
	
	/**
	 * @param node
	 */
	public void removeFromHeaderTable(FPNode node){
		headerTable.get(node.getItem()).remove(node);
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
		
		for(int index=singleCandidates.size()-1;index>=0;index--){
			String item=singleCandidates.get(index);
			List<FPNode> itemNodeList = headerTable.get(item);
			
			if(itemNodeList.size()>0){
				int count=0;
				for(FPNode node:itemNodeList){
					count+=node.getCount();
				}
				
				if(count<threshold){
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
			}
		}
	}
}
