package com.ssparrow.datamining.association.fpgrowth;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.ssparrow.datamining.association.fpgrowth.FPGrowthAlgorithm;

public class FPGrowthAlgorithmTest {

	@Test
	public void testFindFrequentItemSets() {
List<List<String>> transactions = new ArrayList<List<String>>();
		
		//f, a, c, d, g, i, m, p
		List<String> transaction=new ArrayList<String>();
		transaction.add("f");
		transaction.add("c");
		transaction.add("a");
		transaction.add("d");
		transaction.add("g");
		transaction.add("i");
		transaction.add("m");
		transaction.add("p");
		transactions.add(transaction);
		
		//a, b, c, f, l, m, o
		transaction=new ArrayList<String>();
		transaction.add("a");
		transaction.add("b");
		transaction.add("c");
		transaction.add("f");
		transaction.add("l");
		transaction.add("m");
		transaction.add("o");
		transactions.add(transaction);
		
		//b, f, h, j, o
		transaction=new ArrayList<String>();
		transaction.add("b");
		transaction.add("f");
		transaction.add("h");
		transaction.add("j");
		transaction.add("o");
		transactions.add(transaction);

		//b, c, k, s, p
		transaction=new ArrayList<String>();
		transaction.add("b");
		transaction.add("c");
		transaction.add("k");
		transaction.add("s");
		transaction.add("p");
		transactions.add(transaction);

		//a, f, c, e, l, p, m, n
		transaction=new ArrayList<String>();
		transaction.add("a");
		transaction.add("f");
		transaction.add("c");
		transaction.add("e");
		transaction.add("l");
		transaction.add("p");
		transaction.add("m");
		transaction.add("n");
		transactions.add(transaction);
		
		FPGrowthAlgorithm fpGrowthAlgorithm = new FPGrowthAlgorithm();
		fpGrowthAlgorithm.findFrequentItemSets(transactions, 3);;
		Map<Set<String>, Integer> patterns = fpGrowthAlgorithm.getFrequentItemSets(2);
		assertEquals(12, patterns.size());
		
		assertEquals("{[c, p]=3, [c, m]=3, [f, m]=3, [c, f, m]=3, [a, m]=3, [a, c, m]=3, [a, f, m]=3, [a, c, f, m]=3, [a, c]=3, [a, f]=3, [a, c, f]=3, [c, f]=3}",
						patterns.toString());
		
	}

}
