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
    public void findFrequentItemSets(Map<String, List<String>> transactions,  int threshold) {
		List<String> singleCandidates=new ArrayList<String>();
		
		FPTree fpTree=new FPTree(singleCandidates,false);
		for(String tid:transactions.keySet()){
			List<String> transaction =transactions.get(tid);
		    Collections.sort(transaction);
		    
		    fpTree.addToTree(tid, transaction);
		}
		
		fpTree.filterTree(threshold);
	
		FPTreeUtil.getFrequentItemSet(singleCandidates, fpTree, threshold, frequentItemSets, new LinkedHashMap<Set<String>, Integer>());
    }

}
