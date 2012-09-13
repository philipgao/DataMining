/*
* Copyright (c) 2001-2011 Hewlett-Packard Development Company, L.P.
* Confidential commercial computer software. Valid license required.
*/
package com.ssparrow.datamining.association.fpgrowth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.ssparrow.datamining.association.AbstractAssociationMiningAlgorithm;

/**
 * @author Gao, Fei(fei.gao@hp.com)
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
	Set<String> singleCandidateSet=new TreeSet<String>();
	for(List<String> transaction:transactions){
	    for(String item:transaction){
		singleCandidateSet.add(item);
	    }
	}
	
	List<String> singleCandidates=new ArrayList<String>(singleCandidateSet);
	
	FPTree fpTree=new FPTree(singleCandidates);
	for(List<String > transaction:transactions){
	    Collections.sort(transaction);
	    
	    fpTree.addToTree(transaction);
	}
	
	fpTree.filterTree(threshold);

	FPTreeUtil.getFrequentItemSet(singleCandidates, fpTree, threshold, frequentItemSets, new LinkedHashMap<Set<String>, Integer>());
    }

}
