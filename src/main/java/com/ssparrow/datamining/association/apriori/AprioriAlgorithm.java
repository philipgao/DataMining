/**
 * 
 */
package com.ssparrow.datamining.association.apriori;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.ssparrow.datamining.association.AbstractAssociationMiningAlgorithm;

/**
 * @author Gao, Fei
 *
 */
public class AprioriAlgorithm extends AbstractAssociationMiningAlgorithm{

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
	public void findFrequentItemSets(Map<String, List<String>> transactions, int threshold){
		//count the occurrence of all single items
		Map<Set<String>, Integer> countMap=new LinkedHashMap<Set<String>, Integer>();
		for(String tid:transactions.keySet()){
			List<String> transaction = transactions.get(tid);
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
				
				frequentItemSets.put(itemSet, count);
			}
		}
		
		while(!candidates.isEmpty()){
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
				for(String tid:transactions.keySet()){
					List<String> transaction =transactions.get(tid);
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
					
					frequentItemSets.put(itemSet,count);
				}
			}
		}
		
		return;
	}
}
