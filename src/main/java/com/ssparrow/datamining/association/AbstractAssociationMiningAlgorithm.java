/**
 * 
 */
package com.ssparrow.datamining.association;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Gao, Fei
 *
 */
public abstract class AbstractAssociationMiningAlgorithm {

	protected Map<Set<String>, Integer> frequentItemSets = new LinkedHashMap<Set<String>, Integer>();
	
	/**
	 * @param transactions
	 * @param threshold
	 */
	public abstract void findFrequentItemSets(List<List<String>> transactions, int threshold);
	
	/**
	 * @return the frequentItemSets
	 */
	public Map<Set<String>, Integer> getFrequentItemSets(int minSize) {
		if(minSize==1){
			return frequentItemSets;
		}else if(minSize>1){
			Map<Set<String>, Integer> resultItemSets = new LinkedHashMap<Set<String>, Integer>();
			
			for(Set<String> itemSet:frequentItemSets.keySet()){
				if(itemSet.size()>=minSize){
					resultItemSets.put(itemSet, frequentItemSets.get(itemSet));
				}
			}
			
			return resultItemSets;
		}
		
		return null;
	}

	/**
	 * @return
	 */
	public Map<Set<String>, Integer> getMaximalFrequentItemSet(){
		Map<Set<String>, Integer> maximalFrequentItemSets=new LinkedHashMap<Set<String>, Integer>();
		
		for(Set<String> itemSet:frequentItemSets.keySet()){
			boolean isMaximal=true;
			for(Set<String> otherItemSet:frequentItemSets.keySet()){
				if(!itemSet.equals(otherItemSet) && otherItemSet.containsAll(itemSet)){
					isMaximal=false;
					break;
				}
			}
			if(isMaximal){
				maximalFrequentItemSets.put(itemSet, frequentItemSets.get(itemSet));
			}
		}
		
		return maximalFrequentItemSets;
	}
	
	/**
	 * @return
	 */
	public Map<Set<String>, Integer> getClosedFrequentItemSet(){
		Map<Set<String>, Integer> closedFrequentItemSets=new LinkedHashMap<Set<String>, Integer>();
		
		for(Set<String> itemSet:frequentItemSets.keySet()){
			boolean isClosed=true;;
			for(Set<String> otherItemSet:frequentItemSets.keySet()){
				if(!otherItemSet.equals(itemSet) 
						&& otherItemSet.containsAll(itemSet) 
						&& frequentItemSets.get(otherItemSet).equals(frequentItemSets.get(itemSet))){
						isClosed=false;
						break;
				}
			}
			
			if(isClosed){
				closedFrequentItemSets.put(itemSet, frequentItemSets.get(itemSet));
			}
		}
		
		return closedFrequentItemSets;
	}
}
