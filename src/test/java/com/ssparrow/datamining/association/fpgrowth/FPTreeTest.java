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
		
		FPTree fpTree1=new FPTree(singleCandidates, false);
		
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
		assertEquals(
				"{{[a:[11, 12, 13, 14, 17, 21, 22]]{[b:[11, 12, 13, 17]]{[d:[13, 17]]}{[c:[11, 12]]{[d:[12]]}}}{[c:[14, 21]]{[d:[21]]}}{[d:[22]]}}{[c:[15]]{[d:[15]]}}{[b:[16, 23, 24, 26]]{[c:[16, 23, 26]]{[d:[16, 23, 26]]}}{[d:[24]]}}{[d:[25]]}}",
				fpTree1.toString());

		fpTree1.filterTree(5);
		
		Map<Set<String>, Integer> frequentItemSets = new LinkedHashMap<Set<String>, Integer>();
		FPTreeUtil.getFrequentItemSet(singleCandidates, fpTree1, 5, frequentItemSets, new LinkedHashMap<Set<String>, Integer>());
		
		assertEquals(8, frequentItemSets.size());
	}
	
	@Test
	public void testMergeTreeWithSameTransaction1() {
		List<String> singleCandidates =  new ArrayList<String>();
		
		FPTree fpTree1=new FPTree(singleCandidates, false);
		
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
		assertEquals("{{[a:[1, 2, 3, 4, 7]]{[b:[1, 2, 3, 4, 7]]{[c:[1, 2, 3, 4, 7]]{[d:[1, 2, 3, 4, 7]]}}}}{[c:[5]]{[d:[5]]}}{[b:[6]]{[c:[6]]{[d:[6]]}}}}", fpTree1.toString());
		
		fpTree1.filterTree(5);
		
		Map<Set<String>, Integer> frequentItemSets = new LinkedHashMap<Set<String>, Integer>();
		FPTreeUtil.getFrequentItemSet(singleCandidates, fpTree1, 5, frequentItemSets, new LinkedHashMap<Set<String>, Integer>());
		
		assertEquals(15, frequentItemSets.size());
	}

	@Test
	public void testMergeTreeWithSameTransaction2() {
		List<String> singleCandidates =  new ArrayList<String>();
		
		FPTree fpTree1=new FPTree(singleCandidates, false);
		
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
		assertEquals("{{[a:[1, 2, 3, 4, 7]]{[b:[1, 2, 3, 4, 7]]{[c:[1, 2, 3, 4, 7]]{[d:[1, 2, 3, 4, 7]]}}}}{[b:[6]]{[c:[6]]{[d:[6]]}}}{[c:[5]]{[d:[5]]}}}", fpTree1.toString());
		
		fpTree1.filterTree(5);
		
		Map<Set<String>, Integer> frequentItemSets = new LinkedHashMap<Set<String>, Integer>();
		FPTreeUtil.getFrequentItemSet(singleCandidates, fpTree1, 5, frequentItemSets, new LinkedHashMap<Set<String>, Integer>());
		
		assertEquals(15, frequentItemSets.size());
	}
}
