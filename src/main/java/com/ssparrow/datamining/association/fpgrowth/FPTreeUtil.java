package com.ssparrow.datamining.association.fpgrowth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class FPTreeUtil {

	
	/**
	 * @param singleCandidates
	 * @param singleItemCountMap
	 * @param fpTree
	 * @param threshold
	 * @param baseItemSets
	 * @return
	 */
	public static void getFrequentItemSet(List<String> singleCandidates, FPTree fpTree, int threshold,Map<Set<String>, Integer> frequentItemSets, Map<Set<String>, Integer> baseItemSets){
		if(fpTree.isEmpty()){
			return;
		}
		
		if(fpTree.isSinglePathTree()){
			getFrequentItemSetForSinglePathTree(fpTree, threshold, frequentItemSets, baseItemSets);
			return;
		}
		
		for(int index=singleCandidates.size()-1;index>=0;index--){
			Map<Set<String>, Integer> newBaseItemSets = new LinkedHashMap<Set<String>, Integer>();
			newBaseItemSets.putAll(baseItemSets);
			
			String item=singleCandidates.get(index);
			int count=fpTree.getItemCount(item);
			
			if(count>=threshold){
				if(baseItemSets.size()==0){
					Set<String> singleItemSet=new TreeSet<String>();
					singleItemSet.add(item);
					
					newBaseItemSets.put(singleItemSet, count);
					frequentItemSets.put(singleItemSet, count);
				}else{
					for(Set<String> baseItemSet:baseItemSets.keySet()){
						Set<String> newItemSet=new TreeSet<String>(baseItemSet);
						newItemSet.add(item);
						
						newBaseItemSets.put(newItemSet, count);
						frequentItemSets.put(newItemSet, count);
					}
				}
			}
			
			List<List<FPNode>> conditionalPatternBase = fpTree.getConditionalPatternBase(item);
			FPTree newFpTreee = createFpTreeFromConditionalPatternBase(singleCandidates, conditionalPatternBase, threshold);
			
			getFrequentItemSet(singleCandidates, newFpTreee, threshold, frequentItemSets, newBaseItemSets);
		}
		
		return;
	}
	
	/**
	 * @param singleCandidates
	 * @param singleItemCountMap
	 * @param fpTree
	 * @param threshold
	 * @param frequentItemSets
	 * @param baseItemSets
	 */
	private static void getFrequentItemSetForSinglePathTree(FPTree fpTree, int threshold,Map<Set<String>, Integer> frequentItemSets, Map<Set<String>, Integer> baseItemSets){
		FPNode node=fpTree.getRoot();
		FPNode child=node.getChildren().size()>0?node.getChildren().get(0):null;
		
		while(child!=null){
			if(baseItemSets.size()==0){
				Set<String> singleItemSet=new TreeSet<String>();
				singleItemSet.add(child.getItem());
				
				frequentItemSets.put(singleItemSet, child.getCount());
				baseItemSets.put(singleItemSet, child.getCount());
			}else{
				Map<Set<String>, Integer> newItemSets=new LinkedHashMap<Set<String>, Integer>();
				
				for(Set<String> baseItemSet:baseItemSets.keySet()){
					Set<String> newItemSet=new TreeSet<String>(baseItemSet);
					newItemSet.add(child.getItem());
					
					int count=Math.min(baseItemSets.get(baseItemSet), child.getCount());
					
					newItemSets.put(newItemSet, count);
				}
				
				frequentItemSets.putAll(newItemSets);
				baseItemSets.putAll(newItemSets);
			}
			
			node=child;
			child=node.getChildren().size()>0?node.getChildren().get(0):null;
		}
	}
	
	/**
	 * 
	 * @param singleCandidates
	 * @param conditionalPatternBase
	 * @param threshold
	 * @return
	 */
	private static FPTree createFpTreeFromConditionalPatternBase(List<String> singleCandidates, List<List<FPNode>> conditionalPatternBase, int threshold){
		List<String> baseCandidates=new ArrayList<String>(singleCandidates);
		FPTree fpTree=new FPTree(baseCandidates);
		
		//create fp tree from the conditional pattern base
		for(List<FPNode> path:conditionalPatternBase){
			if(path.size()>0){
				fpTree.addClonedNodesToTree(path);
			}
		}
		
		fpTree.filterTree(threshold);
		
		return fpTree;
	}
	

	
	/**
	 * @param frequentSingleItems
	 */
	public static FPTree reconstructTree(FPTree fpTree, final List<String> frequentSingleItems){
		fpTree.filterTree(frequentSingleItems);
		
		FPNode root = fpTree.getRoot();
		List<String> singleCandidates = fpTree.getSingleCandidates();
		
		if(singleCandidates.equals(frequentSingleItems)){
			return fpTree;
		}
		
		FPTree newFPTree=new FPTree(frequentSingleItems);
		
		Queue<FPNode> leafs=new LinkedList<FPNode>();
		
		Queue<FPNode> queue=new LinkedList<FPNode>();
		queue.offer(root);
		while(!queue.isEmpty()){
			FPNode node = queue.poll();
			List<FPNode> children = node.getChildren();
			if(children.isEmpty()){
				leafs.add(node);
			}else{
				queue.addAll(children);
			}
		}
		
		Comparator<FPNode> nodeComparator=new Comparator<FPNode>() {
			@Override
			public int compare(FPNode o1, FPNode o2) {
				return frequentSingleItems.indexOf(o1.getItem())-frequentSingleItems.indexOf(o2.getItem());
			}
		};
		
		while(!leafs.isEmpty()){
			FPNode leaf=leafs.poll();
			
			List<FPNode> nodeList=new ArrayList<FPNode>();
			
			Set<String> transactionSet = new TreeSet<String>(leaf.getTransactionSet());
			List<FPNode> path = leaf.getPath();
			
			FPNode nodeToDelete=null;
			for(int index=path.size()-1; index>=0;index--){
				FPNode node = path.get(index);
				try {
					FPNode clone = (FPNode)node.clone();
					clone.setTransactionSet(transactionSet);
					nodeList.add(clone);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				
				if(nodeToDelete!=null){
					node.removeChild(nodeToDelete);
				}
				
				node.removeTransaction(transactionSet);
				int newCount=node.getCount();
				
				if(newCount==0){
					nodeToDelete=node;
				}else if(node.getChildren().size()==0){
					leafs.offer(node);
				}
			}
			
			Collections.sort(nodeList, nodeComparator);
			newFPTree.addClonedNodesToTree(nodeList);
		}
		return newFPTree;
	}
	
	/**
	 * @param sourceTree
	 * @param targetTree
	 */
	public static FPTree mergeTrees(List<FPTree> treeList, final List<String> frequentSingleItems){
		Comparator<FPNode> nodeComparator=new Comparator<FPNode>() {
			@Override
			public int compare(FPNode o1, FPNode o2) {
				return frequentSingleItems.indexOf(o1.getItem())-frequentSingleItems.indexOf(o2.getItem());
			}
		};
		
		FPTree newTree=new FPTree(frequentSingleItems);
		
		for(FPTree fpTree:treeList){
			FPNode root=fpTree.getRoot();
			List<FPNode> children = root.getChildren();
			
			for(FPNode child:children){
				Set<String> transactionSet=new HashSet<String>(child.getTransactionSet());
				for(String tid:transactionSet){
					try {
						Map<String, FPNode> transactionPath = child.cloneTransactionWithChildren(tid);
						child.removeTransactionInSubpath(tid);
						
						for(FPTree otherFpTree:treeList){
							if(!fpTree.equals(otherFpTree)){
								FPNode otherRoot=otherFpTree.getRoot();
								FPNode otherChild = otherRoot.getChildByTransaction(tid);
								
								if(otherChild!=null){
									Map<String, FPNode> otherPath = otherChild.cloneTransactionWithChildren(tid);
									otherChild.removeTransactionInSubpath(tid);
									
									transactionPath.putAll(otherPath);
								}
							}
						}
						
						List<FPNode> transactionNodes = new  ArrayList<FPNode>(transactionPath.values());
						Collections.sort(transactionNodes, nodeComparator);
						
						newTree.addClonedNodesToTree(transactionNodes);
						
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return newTree;
	}
	
	
	/**
	 * @param str
	 * @return
	 */
	public static FPTree buildTreeFromStr(String str){
		List<String> singleCandidates=new ArrayList<String>();
		FPTree fpTree=new FPTree(singleCandidates, false);
		
		buildSubTreeFromStr(fpTree, fpTree.getRoot(), str);
		
		return fpTree;
	}
	
	private static void buildSubTreeFromStr(FPTree fpTree, FPNode parent, String str){
		if(str.startsWith("{")  && str.endsWith("}")){
			FPNode node = parent;
			String content=str.substring(1, str.length()-1);
			
			int startClause = 0;
			if(content.startsWith("[")){
				int nodeStart=0;
				int nodeEnd=content.indexOf("]")+1;
				
				String nodeStr=content.substring(nodeStart+1, nodeEnd);
				node=buildFPNode(fpTree, nodeStr);
				parent.addChild(node);
			
				startClause = content.indexOf("{", nodeEnd);
			}
				
			while(startClause>=0 && startClause<=content.length()-1){
				int endClause = findEndClause(content, startClause);
				
				String subTreeStr=content.substring(startClause, endClause+1);
				buildSubTreeFromStr(fpTree, node, subTreeStr);
				
				startClause=content.indexOf("{", endClause);
			}
		}
	}
	
	private static int findEndClause(String str, int startClause){
		int count=1;
		
		int index=startClause+1; 
		
		while(index<str.length()){
			int nextStart=str.indexOf("{", index)==-1?str.length():str.indexOf("{", index);
			int nextEnd=str.indexOf("}", index)==-1?str.length():str.indexOf("}", index);
			
			count = nextStart<nextEnd? count+1:count-1;
			index=Math.min(nextStart, nextEnd)+1;
			
			if(count==0){
				return index-1;
			}
		}
		
		return str.length();
	}
	
	private static FPNode buildFPNode(FPTree fpTree, String str){
		int seperator = str.indexOf(":");
		
		String item=str.substring(0, seperator);
		
		String transactionSetStr=str.substring(str.indexOf("[", seperator)+1, str.indexOf("]", seperator));
		Set<String> transactionSet=new TreeSet<String>();
		StringTokenizer st=new StringTokenizer(transactionSetStr," ,");
		while(st.hasMoreTokens()){
			transactionSet.add(st.nextToken());
		}
		
		FPNode node=new FPNode(fpTree, item);
		node.setTransactionSet(transactionSet);
		
		return node;
	}
	
	/**
	 * Deprecated because it may have bug when the FP tree have multiple distinct paths
	 * 
	 * @param singleCandidates
	 * @param singleItemCountMap
	 * @param fpTree
	 * @param threshold
	 * @return
	 * 
	 * @deprecated
	 */
	public static Map<Set<String>, Integer> getFrequentItemSetByMergingPaths(List<String> singleCandidates, Map<String, Integer> singleItemCountMap, FPTree fpTree, int threshold){
		Map<Set<String>, Integer> frequentItemSets = new LinkedHashMap<Set<String>, Integer>();

		for(int index=singleCandidates.size()-1;index>=0;index--){
			String item=singleCandidates.get(index);
			int count = singleItemCountMap.get(item);
			
			Map<Set<String>, Integer> currentFrequentItemSets = new LinkedHashMap<Set<String>, Integer>();
			Set<String> singleItemSet=new LinkedHashSet<String>();
			singleItemSet.add(item);
			currentFrequentItemSets.put(singleItemSet, count);
			
			List<List<FPNode>> conditionalPatternBase = fpTree.getConditionalPatternBase(item);
			FPTree fpTreeFromCPB = FPTreeUtil.createFpTreeFromConditionalPatternBaseByMergingPaths(singleCandidates, conditionalPatternBase, threshold);
			
			FPNode node = fpTreeFromCPB.getRoot();
			FPNode child = node.getChildren().size()>0?node.getChildren().get(0):null;
			while(child!=null){
				Map<Set<String>, Integer> newFrequentItemSets = new LinkedHashMap<Set<String>, Integer>();
				
				for(Set<String> itemSet:currentFrequentItemSets.keySet()){
					Set<String> newItemSet=new LinkedHashSet<String>(itemSet);
					newItemSet.add(child.getItem());
					
					int newItemSetCount = Math.min(currentFrequentItemSets.get(itemSet), child.getCount());
					
					newFrequentItemSets.put(newItemSet, newItemSetCount);
				}
				
				currentFrequentItemSets.putAll(newFrequentItemSets);
				
				node=child;
				child = node.getChildren().size()>0?node.getChildren().get(0):null;
			}
			
			frequentItemSets.putAll(currentFrequentItemSets);
		}
		
		
		return frequentItemSets;
	}
	
	/**
	 * Deprecated because it may have bug when the FP tree have multiple distinct paths
	 * 
	 * @param singleCandidates
	 * @param conditionalPatternBase
	 * @param threshold
	 * @return
	 * 
	 * @deprecated
	 */
	private static FPTree createFpTreeFromConditionalPatternBaseByMergingPaths(List<String> singleCandidates, List<List<FPNode>> conditionalPatternBase, int threshold){
		FPTree fpTree=new FPTree(singleCandidates);
		
		FPNode node=fpTree.getRoot();
		
		for(String item:singleCandidates){
			boolean finished=true;
			Set<String> transactionSet=new HashSet<String>();
			
			//go through all the paths in each loop, until all the path are finished
			for(int index=0;index<conditionalPatternBase.size();index++){
				List<FPNode> path = conditionalPatternBase.get(index);
				
				//get the first path that still has unvisited nodes
				if(path.size()>0){
					finished=false;
					
					if(path.get(0).getItem().equals(item)){
						transactionSet.addAll(path.get(0).getTransactionSet());
						
						//remove the top node anyway, whether it is kept or discarded
						path.remove(0);
					}
					
					
				}
			}
			
			//if the count match threshold, create node for the merged count and add the node to the tree
			if(transactionSet.size()>=threshold){
				FPNode child=new FPNode(fpTree, item);
				child.setTransactionSet(transactionSet);
				
				node.addChild(child);
				node=child;
			}
			
			if(finished){
				break;
			}
		}
		
		
		return fpTree;
	}
	
}
