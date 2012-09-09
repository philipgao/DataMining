/**
 * 
 */
package com.ssparrow.datamining.apriori;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Gao, Fei
 *
 */
public class FPGrowthAlgorithm extends AbstractAssociationMiningAlgorithm {

	/* (non-Javadoc)
	 * @see com.ssparrow.datamining.apriori.AbstractAssociationMiningAlgorithm#findFrequentItemSets(java.util.List, int)
	 */
	@Override
	public void findFrequentItemSets(List<List<String>> transactions,int threshold) {
		List<String> singleCandidates=new ArrayList<String>();
		
		Map<String, Integer> singleItemCountMap=new HashMap<String, Integer>();
		for(List<String> transaction:transactions){
			for(String item:transaction){
				int count = singleItemCountMap.get(item)==null?1:singleItemCountMap.get(item)+1;
				singleItemCountMap.put(item, count);
			}
		}
		
		Map<Integer, List<String>> reverseCountMap=new HashMap<Integer, List<String>>();
		for(String item:singleItemCountMap.keySet()){
			int count=singleItemCountMap.get(item);
			
			if(count>=threshold){
				List<String> itemList=reverseCountMap.get(count);
				if(itemList==null){
					itemList = new ArrayList<String>();
					reverseCountMap.put(count, itemList);
				}
				itemList.add(item);
			}
		}
		
		List<Integer> countList=new ArrayList<Integer>(reverseCountMap.keySet());
		Collections.sort(countList);
		for(Integer count:countList){
			singleCandidates.addAll(reverseCountMap.get(count));
		}
		
		FPTree fpTree=new FPTree(singleCandidates);
		
		for(List<String> transaction:transactions){
			Map<Integer, String> frequentItemsMap=new TreeMap<Integer, String>();
			for(String item:transaction){
				if(singleCandidates.contains(item)){
					frequentItemsMap.put(singleCandidates.indexOf(item), item);
				}
			}
			
			List<String> itemList = new ArrayList<String>(frequentItemsMap.values());
			fpTree.addToTree(itemList);
		}
		

	}

}
