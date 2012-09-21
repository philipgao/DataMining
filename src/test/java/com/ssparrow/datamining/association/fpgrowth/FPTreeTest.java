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
	public void testMergeTreeWithoutSameTransaction() {
		List<String> singleCandidates =  new ArrayList<String>();
		singleCandidates.add("a");
		singleCandidates.add("b");
		singleCandidates.add("c");
		singleCandidates.add("d");
		
		FPTree fpTree1=new FPTree(singleCandidates);
		
		List<String> itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("b");
		itemList.add("c");
		fpTree1.addToTree("11", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("b");
		itemList.add("c");
		itemList.add("d");
		fpTree1.addToTree("12", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("b");
		itemList.add("d");
		fpTree1.addToTree("13", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("c");
		fpTree1.addToTree("14", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("c");
		itemList.add("d");
		fpTree1.addToTree("15", itemList);

		itemList=new ArrayList<String>();
		itemList.add("b");
		itemList.add("c");
		itemList.add("d");
		fpTree1.addToTree("16", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("b");
		itemList.add("d");
		fpTree1.addToTree("17", itemList);
		
		FPTree fpTree2=new FPTree(singleCandidates);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("c");
		itemList.add("d");
		fpTree2.addToTree("21", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("d");
		fpTree2.addToTree("22", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("b");
		itemList.add("c");
		itemList.add("d");
		fpTree2.addToTree("23", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("b");
		itemList.add("d");
		fpTree2.addToTree("24", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("d");
		fpTree2.addToTree("25", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("b");
		itemList.add("c");
		itemList.add("d");
		fpTree2.addToTree("26", itemList);
		
		List<FPTree> treeList=new ArrayList<FPTree>();
		treeList.add(fpTree1);
		treeList.add(fpTree2);
		
		fpTree1 =FPTreeUtil.mergeTrees(treeList, singleCandidates);
		assertEquals("{{[a:7]{[b:4]{[d:2]}{[c:2]{[d:1]}}}{[c:2]{[d:1]}}{[d:1]}}{[c:1]{[d:1]}}{[b:4]{[c:3]{[d:3]}}{[d:1]}}{[d:1]}}", fpTree1.toString());
		
		fpTree1.filterTree(5);
		
		Map<Set<String>, Integer> frequentItemSets = new LinkedHashMap<Set<String>, Integer>();
		FPTreeUtil.getFrequentItemSet(singleCandidates, fpTree1, 5, frequentItemSets, new LinkedHashMap<Set<String>, Integer>());
		
		assertEquals(8, frequentItemSets.size());
	}
	
	@Test
	public void testMergeTreeWithSameTransaction1() {
		List<String> singleCandidates =  new ArrayList<String>();
		singleCandidates.add("a");
		singleCandidates.add("b");
		singleCandidates.add("c");
		singleCandidates.add("d");
		
		FPTree fpTree1=new FPTree(singleCandidates);
		
		List<String> itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("b");
		itemList.add("c");
		fpTree1.addToTree("1", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("b");
		itemList.add("c");
		itemList.add("d");
		fpTree1.addToTree("2", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("b");
		itemList.add("d");
		fpTree1.addToTree("3", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("c");
		fpTree1.addToTree("4", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("c");
		itemList.add("d");
		fpTree1.addToTree("5", itemList);

		itemList=new ArrayList<String>();
		itemList.add("b");
		itemList.add("c");
		itemList.add("d");
		fpTree1.addToTree("6", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("b");
		itemList.add("d");
		fpTree1.addToTree("7", itemList);
		
		FPTree fpTree2=new FPTree(singleCandidates);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("c");
		itemList.add("d");
		fpTree2.addToTree("1", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("d");
		fpTree2.addToTree("2", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("b");
		itemList.add("c");
		itemList.add("d");
		fpTree2.addToTree("3", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("b");
		itemList.add("d");
		fpTree2.addToTree("4", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("d");
		fpTree2.addToTree("5", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("b");
		itemList.add("c");
		itemList.add("d");
		fpTree2.addToTree("6", itemList);

		itemList=new ArrayList<String>();
		itemList.add("b");
		itemList.add("c");
		itemList.add("d");
		fpTree2.addToTree("7", itemList);
		
		List<FPTree> treeList=new ArrayList<FPTree>();
		treeList.add(fpTree1);
		treeList.add(fpTree2);
		
		fpTree1 =FPTreeUtil.mergeTrees(treeList, singleCandidates);
		assertEquals("{{[a:5]{[b:5]{[c:5]{[d:5]}}}}{[c:1]{[d:1]}}{[b:1]{[c:1]{[d:1]}}}}", fpTree1.toString());
		
		fpTree1.filterTree(5);
		
		Map<Set<String>, Integer> frequentItemSets = new LinkedHashMap<Set<String>, Integer>();
		FPTreeUtil.getFrequentItemSet(singleCandidates, fpTree1, 5, frequentItemSets, new LinkedHashMap<Set<String>, Integer>());
		
		assertEquals(15, frequentItemSets.size());
	}

	@Test
	public void testMergeTreeWithSameTransaction2() {
		List<String> singleCandidates =  new ArrayList<String>();
		singleCandidates.add("a");
		singleCandidates.add("b");
		singleCandidates.add("c");
		singleCandidates.add("d");
		
		FPTree fpTree1=new FPTree(singleCandidates);
		
		List<String> itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("b");
		itemList.add("c");
		fpTree1.addToTree("1", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("b");
		itemList.add("c");
		itemList.add("d");
		fpTree1.addToTree("2", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("b");
		itemList.add("d");
		fpTree1.addToTree("3", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("c");
		fpTree1.addToTree("4", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("c");
		itemList.add("d");
		fpTree1.addToTree("5", itemList);

		itemList=new ArrayList<String>();
		itemList.add("b");
		itemList.add("c");
		itemList.add("d");
		fpTree1.addToTree("6", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("b");
		itemList.add("d");
		fpTree1.addToTree("7", itemList);
		
		FPTree fpTree2=new FPTree(singleCandidates);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("c");
		itemList.add("d");
		fpTree2.addToTree("1", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("d");
		fpTree2.addToTree("2", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("b");
		itemList.add("c");
		itemList.add("d");
		fpTree2.addToTree("3", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("b");
		itemList.add("d");
		fpTree2.addToTree("4", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("d");
		fpTree2.addToTree("5", itemList);
		
		itemList=new ArrayList<String>();
		itemList.add("b");
		itemList.add("c");
		itemList.add("d");
		fpTree2.addToTree("6", itemList);

		itemList=new ArrayList<String>();
		itemList.add("b");
		itemList.add("c");
		itemList.add("d");
		fpTree2.addToTree("7", itemList);
		
		List<FPTree> treeList=new ArrayList<FPTree>();
		treeList.add(fpTree2);
		treeList.add(fpTree1);
		
		fpTree1 =FPTreeUtil.mergeTrees(treeList, singleCandidates);
		assertEquals("{{[a:5]{[b:5]{[c:5]{[d:5]}}}}{[b:1]{[c:1]{[d:1]}}}{[c:1]{[d:1]}}}", fpTree1.toString());
		
		fpTree1.filterTree(5);
		
		Map<Set<String>, Integer> frequentItemSets = new LinkedHashMap<Set<String>, Integer>();
		FPTreeUtil.getFrequentItemSet(singleCandidates, fpTree1, 5, frequentItemSets, new LinkedHashMap<Set<String>, Integer>());
		
		assertEquals(15, frequentItemSets.size());
	}
}
