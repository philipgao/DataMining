/**
 * 
 */
package com.ssparrow.datamining.association.apriori;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.ssparrow.datamining.association.apriori.AprioriAlgorithm;

/**
 * @author Gao, Fei
 *
 */
public class AprioriAlgorithmTest {

	/**
	 * test using the example on wikipedia
	 *   {1,2,3,4}, {1,2}, {2,3,4}, {2,3}, {1,2,4}, {3,4}, and {2,4}
	 */
	@Test
	public void testFindFrequentItemSetsWithMoreThanOneItems() {
		Map<String, List<String>> transactions = new LinkedHashMap<String, List<String>>();
		
		//{1,2,3,4}
		List<String> transaction=new ArrayList<String>();
		transaction.add("1");
		transaction.add("2");
		transaction.add("3");
		transaction.add("4");
		transactions.put("1", transaction);
		
		//{1,2}
		transaction=new ArrayList<String>();
		transaction.add("1");
		transaction.add("2");
		transactions.put("2", transaction);
		
		//{2,3,4}
		transaction=new ArrayList<String>();
		transaction.add("2");
		transaction.add("3");
		transaction.add("4");
		transactions.put("3", transaction);

		//{2,3}
		transaction=new ArrayList<String>();
		transaction.add("2");
		transaction.add("3");
		transactions.put("4", transaction);

		//{1,2,4}
		transaction=new ArrayList<String>();
		transaction.add("1");
		transaction.add("2");
		transaction.add("4");
		transactions.put("5", transaction);

		//{3,4}
		transaction=new ArrayList<String>();
		transaction.add("3");
		transaction.add("4");
		transactions.put("6", transaction);

		//{2,4}
		transaction=new ArrayList<String>();
		transaction.add("2");
		transaction.add("4");
		transactions.put("7", transaction);
		
		AprioriAlgorithm aprioriAlgorithm = new AprioriAlgorithm();
		aprioriAlgorithm.findFrequentItemSets(transactions, 3);;
		Map<Set<String>, Integer> patterns = aprioriAlgorithm.getFrequentItemSets(2);
		assertEquals(4, patterns.size());
		
		Iterator<Set<String>> iterator = patterns.keySet().iterator();
		
		Set<String> itemSet = iterator.next();
		Iterator<String> itemIterator = itemSet.iterator();
		assertEquals("1", itemIterator.next());
		assertEquals("2", itemIterator.next());
		assertEquals(Integer.valueOf(3), patterns.get(itemSet));
		
		itemSet = iterator.next();
		itemIterator = itemSet.iterator();
		assertEquals("2", itemIterator.next());
		assertEquals("3", itemIterator.next());
		assertEquals(Integer.valueOf(3), patterns.get(itemSet));
		
		itemSet = iterator.next();
		itemIterator = itemSet.iterator();
		assertEquals("2", itemIterator.next());
		assertEquals("4", itemIterator.next());
		assertEquals(Integer.valueOf(4), patterns.get(itemSet));
		
		itemSet = iterator.next();
		itemIterator = itemSet.iterator();
		assertEquals("3", itemIterator.next());
		assertEquals("4", itemIterator.next());
		assertEquals(Integer.valueOf(3), patterns.get(itemSet));
	}
	
	/**
	 * another example with the following data set
	 * A B
	 * A C E
	 * B C
	 * A C D
	 * A B C
	 * A B D
	 * B C D E
	 */
	@Test
	public void testFindFrequentItemSetsAllSizes(){
		Map<String, List<String>> transactions = new LinkedHashMap<String, List<String>>();
		
		//A B
		List<String> transaction=new ArrayList<String>();
		transaction.add("A");
		transaction.add("B");
		transactions.put("1", transaction);
		
		//A C E
		transaction=new ArrayList<String>();
		transaction.add("A");
		transaction.add("C");
		transaction.add("E");
		transactions.put("2", transaction);
		
		//B C
		transaction=new ArrayList<String>();
		transaction.add("B");
		transaction.add("C");
		transactions.put("3", transaction);
		
		//A C D
		transaction=new ArrayList<String>();
		transaction.add("A");
		transaction.add("C");
		transaction.add("D");
		transactions.put("4", transaction);

		//A B C
		transaction=new ArrayList<String>();
		transaction.add("A");
		transaction.add("B");
		transaction.add("C");
		transactions.put("5", transaction);

		//A B D
		transaction=new ArrayList<String>();
		transaction.add("A");
		transaction.add("B");
		transaction.add("D");
		transactions.put("6", transaction);

		//B C D E
		transaction=new ArrayList<String>();
		transaction.add("B");
		transaction.add("C");
		transaction.add("D");
		transaction.add("E");
		transactions.put("7", transaction);
		
		AprioriAlgorithm aprioriAlgorithm = new AprioriAlgorithm();
		aprioriAlgorithm.findFrequentItemSets(transactions, 3);;
		Map<Set<String>, Integer> patterns = aprioriAlgorithm.getFrequentItemSets(1);
		assertEquals(7, patterns.size());
		
		Iterator<Set<String>> iterator = patterns.keySet().iterator();
		
		Set<String> itemSet = iterator.next();
		Iterator<String> itemIterator = itemSet.iterator();
		assertEquals("A", itemIterator.next());
		assertEquals(Integer.valueOf(5), patterns.get(itemSet));

		itemSet = iterator.next();
		itemIterator = itemSet.iterator();
		assertEquals("B", itemIterator.next());
		assertEquals(Integer.valueOf(5), patterns.get(itemSet));
		
		itemSet = iterator.next();
		itemIterator = itemSet.iterator();
		assertEquals("C", itemIterator.next());
		assertEquals(Integer.valueOf(5), patterns.get(itemSet));
		
		itemSet = iterator.next();
		itemIterator = itemSet.iterator();
		assertEquals("D", itemIterator.next());
		assertEquals(Integer.valueOf(3), patterns.get(itemSet));
		
		itemSet = iterator.next();
		itemIterator = itemSet.iterator();
		assertEquals("A", itemIterator.next());
		assertEquals("B", itemIterator.next());
		assertEquals(Integer.valueOf(3), patterns.get(itemSet));
		
		itemSet = iterator.next();
		itemIterator = itemSet.iterator();
		assertEquals("A", itemIterator.next());
		assertEquals("C", itemIterator.next());
		assertEquals(Integer.valueOf(3), patterns.get(itemSet));
		
		itemSet = iterator.next();
		itemIterator = itemSet.iterator();
		assertEquals("B", itemIterator.next());
		assertEquals("C", itemIterator.next());
		assertEquals(Integer.valueOf(3), patterns.get(itemSet));
		
		Map<Set<String>, Integer> maximalFrequentItemSet = aprioriAlgorithm.getMaximalFrequentItemSet();
		assertEquals("{[D]=3, [A, B]=3, [A, C]=3, [B, C]=3}", maximalFrequentItemSet.toString());
		
		Map<Set<String>, Integer> closedFrequentItemSet = aprioriAlgorithm.getClosedFrequentItemSet();
		assertEquals(7, closedFrequentItemSet.size());
	}

}
