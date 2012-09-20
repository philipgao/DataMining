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
			
			Set<String> transactionSet = new HashSet<String>(leaf.getTransactionSet());
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
