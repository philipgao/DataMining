/*
* Copyright (c) 2001-2011 Hewlett-Packard Development Company, L.P.
* Confidential commercial computer software. Valid license required.
*/
package com.ssparrow.datamining.association.fpgrowth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ssparrow.datamining.association.AbstractAssociationMiningAlgorithm;

/**
 * @author Gao, Fei
 *
 */
public class FPGrowthAlgorithmWithoutPruning extends
	AbstractAssociationMiningAlgorithm {

    /**
     * @param transactions
     * @param threshold
     */
    @Override
    public void findFrequentItemSets(List<List<String>> transactions,  int threshold) {
		List<String> singleCandidates=new ArrayList<String>();
		
		FPTree fpTree=new FPTree(singleCandidates,false);
		for(List<String > transaction:transactions){
		    Collections.sort(transaction);
		    
		    fpTree.addToTree(transaction);
		}
		
		fpTree.filterTree(threshold);
	
		FPTreeUtil.getFrequentItemSet(singleCandidates, fpTree, threshold, frequentItemSets, new LinkedHashMap<Set<String>, Integer>());
    }
    
    /**
     * @param treeList
     * @param singleCandidates
     * @param threshold
     */
    public void findFrequentItemSets(List<FPTree> treeList,  List<String> singleCandidates, int threshold) {
    	frequentItemSets = new LinkedHashMap<Set<String>, Integer>();
    	
    	FPTree mergedTree = treeList.get(0);
    	for(int i=1;i<treeList.size();i++){
    		mergedTree.mergeTree(treeList.get(i));
    	}
    	
    	System.out.println("merged tree:"+mergedTree.toString());
		
		mergedTree.filterTree(threshold);
		
		FPTreeUtil.getFrequentItemSet(singleCandidates, mergedTree, threshold, frequentItemSets, new LinkedHashMap<Set<String>, Integer>());
    }

}
