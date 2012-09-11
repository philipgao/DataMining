package com.ssparrow.datamining.apriori;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
	public static void getFrequentItemSet(List<String> singleCandidates, Map<String, Integer> singleItemCountMap, FPTree fpTree, int threshold,Map<Set<String>, Integer> frequentItemSets, Map<Set<String>, Integer> baseItemSets){
		if(fpTree.isEmpty()){
			return;
		}
		
		if(fpTree.isSinglePathTree()){
			getFrequentItemSetForSinglePathTree(singleCandidates, singleItemCountMap, fpTree, threshold, frequentItemSets, baseItemSets);
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
			
			getFrequentItemSet(singleCandidates, singleItemCountMap, newFpTreee, threshold, frequentItemSets, newBaseItemSets);
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
	private static void getFrequentItemSetForSinglePathTree(List<String> singleCandidates, Map<String, Integer> singleItemCountMap, FPTree fpTree, int threshold,Map<Set<String>, Integer> frequentItemSets, Map<Set<String>, Integer> baseItemSets){
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
		FPTree fpTree=new FPTree(singleCandidates);
		
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
			int count=0;
			
			//go through all the paths in each loop, until all the path are finished
			for(int index=0;index<conditionalPatternBase.size();index++){
				List<FPNode> path = conditionalPatternBase.get(index);
				
				//get the first path that still has unvisited nodes
				if(path.size()>0){
					finished=false;
					
					if(path.get(0).getItem().equals(item)){
						count+=path.get(0).getCount();
						
						//remove the top node anyway, whether it is kept or discarded
						path.remove(0);
					}
					
					
				}
			}
			
			//if the count match threshold, create node for the merged count and add the node to the tree
			if(count>=threshold){
				FPNode child=new FPNode(fpTree, item);
				child.setCount(count);
				
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
