/**
 * 
 */
package com.ssparrow.datamining.association.fpgrowth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.ssparrow.datamining.association.AbstractAssociationMiningAlgorithm;

import sun.misc.FpUtils;

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
		
		//count the occurrence of each single item
		Map<String, Integer> singleItemCountMap=new TreeMap<String, Integer>();
		for(List<String> transaction:transactions){
			for(String item:transaction){
				int count = singleItemCountMap.get(item)==null?1:singleItemCountMap.get(item)+1;
				singleItemCountMap.put(item, count);
			}
		}
		
		//create reverse map of Count-FrequentItems, key ordered by descending count
		Map<Integer, List<String>> reverseCountMap=new TreeMap<Integer, List<String>>(new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return o2.intValue()-o1.intValue();
			}
		});
		
		//fill the reverse count map with frequent item sets
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
		
		//create the frequent single item list, with order of descending count
		List<Integer> countList=new ArrayList<Integer>(reverseCountMap.keySet());
		for(Integer count:countList){
			singleCandidates.addAll(reverseCountMap.get(count));
		}
		
		//create FP Tree
		FPTree fpTree=new FPTree(singleCandidates);
		
		for(List<String> transaction:transactions){
			//extract frequent single items from transaction
			Map<Integer, String> frequentItemsMap=new TreeMap<Integer, String>();
			for(String item:transaction){
				if(singleCandidates.contains(item)){
					frequentItemsMap.put(singleCandidates.indexOf(item), item);
				}
			}
			
			//create frequent single item list of current transaction, use it to grow FP Tree
			List<String> itemList = new ArrayList<String>(frequentItemsMap.values());
			fpTree.addToTree(itemList);
		}
		
		FPTreeUtil.getFrequentItemSet(singleCandidates, fpTree, threshold, frequentItemSets, new LinkedHashMap<Set<String>, Integer>());
	}

}
