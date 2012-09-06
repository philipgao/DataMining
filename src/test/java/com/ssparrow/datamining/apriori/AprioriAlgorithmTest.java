/**
 * 
 */
package com.ssparrow.datamining.apriori;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

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
		List<List<String>> transactions = new ArrayList<List<String>>();
		
		//{1,2,3,4}
		List<String> transaction=new ArrayList<String>();
		transaction.add("1");
		transaction.add("2");
		transaction.add("3");
		transaction.add("4");
		transactions.add(transaction);
		
		//{1,2}
		transaction=new ArrayList<String>();
		transaction.add("1");
		transaction.add("2");
		transactions.add(transaction);
		
		//{2,3,4}
		transaction=new ArrayList<String>();
		transaction.add("2");
		transaction.add("3");
		transaction.add("4");
		transactions.add(transaction);

		//{2,3}
		transaction=new ArrayList<String>();
		transaction.add("2");
		transaction.add("3");
		transactions.add(transaction);

		//{1,2,4}
		transaction=new ArrayList<String>();
		transaction.add("1");
		transaction.add("2");
		transaction.add("4");
		transactions.add(transaction);

		//{3,4}
		transaction=new ArrayList<String>();
		transaction.add("3");
		transaction.add("4");
		transactions.add(transaction);

		//{2,4}
		transaction=new ArrayList<String>();
		transaction.add("2");
		transaction.add("4");
		transactions.add(transaction);
		
		Map<Set<String>, Integer> patterns = AprioriAlgorithm.findFrequentItemSets(transactions, 3, 2);
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
		List<List<String>> transactions = new ArrayList<List<String>>();
		
		//A B
		List<String> transaction=new ArrayList<String>();
		transaction.add("A");
		transaction.add("B");
		transactions.add(transaction);
		
		//A C E
		transaction=new ArrayList<String>();
		transaction.add("A");
		transaction.add("C");
		transaction.add("E");
		transactions.add(transaction);
		
		//B C
		transaction=new ArrayList<String>();
		transaction.add("B");
		transaction.add("C");
		transactions.add(transaction);
		
		//A C D
		transaction=new ArrayList<String>();
		transaction.add("A");
		transaction.add("C");
		transaction.add("D");
		transactions.add(transaction);

		//A B C
		transaction=new ArrayList<String>();
		transaction.add("A");
		transaction.add("B");
		transaction.add("C");
		transactions.add(transaction);

		//A B D
		transaction=new ArrayList<String>();
		transaction.add("A");
		transaction.add("B");
		transaction.add("D");
		transactions.add(transaction);

		//B C D E
		transaction=new ArrayList<String>();
		transaction.add("B");
		transaction.add("C");
		transaction.add("D");
		transaction.add("E");
		transactions.add(transaction);
		
		Map<Set<String>, Integer> patterns = AprioriAlgorithm.findFrequentItemSets(transactions, 3, 1);
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
	}

}
