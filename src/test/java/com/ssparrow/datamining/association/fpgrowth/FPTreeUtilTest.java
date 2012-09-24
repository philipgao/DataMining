/**
 * 
 */
package com.ssparrow.datamining.association.fpgrowth;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * @author Gao, Fei
 *
 */
public class FPTreeUtilTest {

	@Test
	public void testReconstructTree() {
		List<String> singleCandidates =  new ArrayList<String>();
		
		FPTree fpTree=new FPTree(singleCandidates, false);
		
		//f, a, c, d, g, i, m, p
		List<String> itemList=new ArrayList<String>();
		itemList.add("f");
		itemList.add("c");
		itemList.add("a");
		itemList.add("d");
		itemList.add("g");
		itemList.add("i");
		itemList.add("m");
		itemList.add("p");
		Collections.sort(itemList);
		fpTree.addToTree(itemList);
		
		//a, b, c, f, l, m, o
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("b");
		itemList.add("c");
		itemList.add("f");
		itemList.add("l");
		itemList.add("m");
		itemList.add("o");
		Collections.sort(itemList);
		fpTree.addToTree(itemList);
		
		//b, f, h, j, o
		itemList=new ArrayList<String>();
		itemList.add("b");
		itemList.add("f");
		itemList.add("h");
		itemList.add("j");
		itemList.add("o");
		Collections.sort(itemList);
		fpTree.addToTree(itemList);

		//b, c, k, s, p
		itemList=new ArrayList<String>();
		itemList.add("b");
		itemList.add("c");
		itemList.add("k");
		itemList.add("s");
		itemList.add("p");
		Collections.sort(itemList);
		fpTree.addToTree(itemList);

		//a, f, c, e, l, p, m, n
		itemList=new ArrayList<String>();
		itemList.add("a");
		itemList.add("f");
		itemList.add("c");
		itemList.add("e");
		itemList.add("l");
		itemList.add("p");
		itemList.add("m");
		itemList.add("n");
		Collections.sort(itemList);
		fpTree.addToTree(itemList);
		
		//c, f, a, b, m, p
		List<String> frequentSingleItems=new ArrayList<String>();
		frequentSingleItems.add("c");
		frequentSingleItems.add("f");
		frequentSingleItems.add("a");
		frequentSingleItems.add("b");
		frequentSingleItems.add("m");
		frequentSingleItems.add("p");
		
		fpTree = FPTreeUtil.reconstructTree(fpTree, frequentSingleItems);
		
		Map<Set<String>, Integer> frequentItemSets = new LinkedHashMap<Set<String>, Integer>();
		FPTreeUtil.getFrequentItemSet(frequentSingleItems, fpTree, 3, frequentItemSets, new LinkedHashMap<Set<String>, Integer>());
		assertEquals(18, frequentItemSets.size());
	}

	//"{{[a:7]{[b:4]{[c:2]{[d:1]}}{[d:2]}}{[c:2]{[d:1]}}{[d:1]}}{[c:1]{[d:1]}}{[b:4]{[c:3]{[d:3]}}{[d:1]}}{[d:1]}}"
	@Test
	public void testBuildTreeFromStr(){
		String str="{{[a:7]{[b:4]{[c:2]{[d:1]}}{[d:2]}}{[c:2]{[d:1]}}{[d:1]}}{[c:1]{[d:1]}}{[b:4]{[c:3]{[d:3]}}{[d:1]}}{[d:1]}}";
		FPTree fpTree = FPTreeUtil.buildTreeFromStr(str);
		assertEquals(str, fpTree.toString());
		
	}
}
