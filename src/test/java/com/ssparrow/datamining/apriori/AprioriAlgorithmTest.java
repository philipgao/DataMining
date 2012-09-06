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
	public void testFindPatterns() {
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
		
		Map<Set<String>, Integer> patterns = AprioriAlgorithm.findPatterns(transactions, 3);
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

}
