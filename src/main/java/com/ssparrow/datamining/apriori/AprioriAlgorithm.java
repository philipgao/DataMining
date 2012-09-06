/**
 * 
 */
package com.ssparrow.datamining.apriori;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Gao, Fei
 *
 */
public class AprioriAlgorithm {
	
	/**
	 * implementation according to pseudo code of apriori algorithm on wikipedia
	 * it will only return items sets with or more than 2 items
	 * 
	 * http://en.wikipedia.org/wiki/Apriori_algorithm
	 * 
	 * @param transactions
	 * @param threshold
	 * @param minSize
	 * @return
	 */
	public static Map<Set<String>, Integer> findFrequentItemSets(List<List<String>> transactions, int threshold, int minSize){
		int size=1;
		
		Map<Set<String>, Integer> frequentItemSets = new LinkedHashMap<Set<String>, Integer>();
		
		//count the occurrence of all single items
		Map<Set<String>, Integer> countMap=new LinkedHashMap<Set<String>, Integer>();
		for(List<String> transaction:transactions){
			for(String item: transaction){
				Set<String> itemSet=new TreeSet<String>();
				itemSet.add(item);
				
				int count=countMap.get(itemSet)==null?1:countMap.get(itemSet)+1;
				countMap.put(itemSet, count);
			}
		}
		
		//find the single items that are candidates for memebers of possible pattern set
		Set<String> singleCandidates=new TreeSet<String>();
		Map<Set<String>, Integer> candidates=new LinkedHashMap<Set<String>, Integer>();
		for(Set<String> itemSet:countMap.keySet()){
			int count=countMap.get(itemSet)==null?0:countMap.get(itemSet);
			
			if(count>=threshold){
				candidates.put(itemSet,count);
				singleCandidates.addAll(itemSet);
				
				if(size>=minSize){
					frequentItemSets.put(itemSet, count);
				}
			}
		}
		
		while(!candidates.isEmpty()){
			size++;
			
			//generate the next level item sets by adding possible single item to current level set
			List<Set<String>> nextLevelItemSets=new ArrayList<Set<String>>();
			for(Set<String> itemSet:candidates.keySet()){
				for(String item:singleCandidates){
					if(!itemSet.contains(item)){
						Set<String> newItemSet=new TreeSet<String>(itemSet);
						newItemSet.add(item);
						
						if(!nextLevelItemSets.contains(newItemSet)){
							nextLevelItemSets.add(newItemSet);
						}
					}
				}
			}
			
			//count the support of each item set
			countMap=new LinkedHashMap<Set<String>, Integer>();
			for(Set<String> itemSet:nextLevelItemSets){
				for(List<String> transaction:transactions){
					if(transaction.containsAll(itemSet)){
						int count=countMap.get(itemSet)==null?1:countMap.get(itemSet)+1;
						countMap.put(itemSet, count);
					}
				}
			}	
			
			//find the patterns for next level(start with level 2)
			candidates=new LinkedHashMap<Set<String>, Integer>();
			for(Set<String> itemSet:countMap.keySet()){
				int count=countMap.get(itemSet)==null?0:countMap.get(itemSet);
				
				if(count>=threshold){
					candidates.put(itemSet,count);
					
					if(size>=minSize){
						frequentItemSets.put(itemSet,count);
					}
				}
			}
		}
		
		return frequentItemSets;
	}
	
	
}
