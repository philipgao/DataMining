package com.ssparrow.datamining.association.fpgrowth;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class FPTreeTest {

	@Test
	public void testMergeTree() {
		List<String> singleCandidates =  new ArrayList<String>();
		
		FPTree fpTree1=new FPTree(singleCandidates, false);
		
		List<String> itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("b");
		itemList.add("c");
		fpTree1.addToTree(itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("b");
		itemList.add("c");
		itemList.add("d");
		fpTree1.addToTree(itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("b");
		itemList.add("d");
		fpTree1.addToTree(itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("c");
		fpTree1.addToTree(itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("c");
		itemList.add("d");
		fpTree1.addToTree(itemList);

		itemList=new ArrayList<String>();
		itemList.add("b");
		itemList.add("c");
		itemList.add("d");
		fpTree1.addToTree(itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("b");
		itemList.add("d");
		fpTree1.addToTree(itemList);
		
		FPTree fpTree2=new FPTree(singleCandidates);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("c");
		itemList.add("d");
		fpTree2.addToTree(itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("d");
		fpTree2.addToTree(itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("b");
		itemList.add("c");
		itemList.add("d");
		fpTree2.addToTree(itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("b");
		itemList.add("d");
		fpTree2.addToTree(itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("d");
		fpTree2.addToTree(itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("b");
		itemList.add("c");
		itemList.add("d");
		fpTree2.addToTree(itemList);
		
		fpTree1.mergeTree(fpTree2);
		assertEquals("{{[a:7]{[b:4]{[c:2]{[d:1]}}{[d:2]}}{[c:2]{[d:1]}}{[d:1]}}{[c:1]{[d:1]}}{[b:4]{[c:3]{[d:3]}}{[d:1]}}{[d:1]}}", fpTree1.toString());
		
		fpTree1.filterTree(5);
		
		Map<Set<String>, Integer> frequentItemSets = new LinkedHashMap<Set<String>, Integer>();
		FPTreeUtil.getFrequentItemSet(singleCandidates, fpTree1, 5, frequentItemSets, new LinkedHashMap<Set<String>, Integer>());
		
		assertEquals(8, frequentItemSets.size());
	}

}
