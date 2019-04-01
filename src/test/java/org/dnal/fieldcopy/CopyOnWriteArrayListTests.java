package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dnal.fieldcopy.util.ThreadSafeList;
import org.junit.Test;

public class CopyOnWriteArrayListTests extends BaseTest {
	
	@Test
	public void test() {
		CopyOnWriteArrayList<Integer> numbers 
		= new CopyOnWriteArrayList<>(new Integer[]{1, 3, 5, 8});
		Iterator<Integer> iterator = numbers.iterator();

		numbers.add(10);
		List<Integer> result = new ArrayList<>();
		iterator.forEachRemaining(result::add);
		assertEquals(4, result.size());
		assertEquals("1,3,5,8", flatten(result));	
		
		iterator = numbers.iterator();
		result = new ArrayList<>();
		iterator.forEachRemaining(result::add);
		assertEquals(5, result.size());
		assertEquals("1,3,5,8,10", flatten(result));	
	}
	
	@Test
	public void test2() {
		ThreadSafeList<Integer> numbers = new ThreadSafeList<>();
		numbers.addAll(Arrays.asList(1, 3, 5, 8));
		
		Iterator<Integer> iterator = numbers.iterator();
		numbers.add(10);
		List<Integer> result = new ArrayList<>();
		iterator.forEachRemaining(result::add);
		assertEquals(4, result.size());
		assertEquals("1,3,5,8", flatten(result));	
		
		iterator = numbers.iterator();
		result = new ArrayList<>();
		iterator.forEachRemaining(result::add);
		assertEquals(5, result.size());
		assertEquals("1,3,5,8,10", flatten(result));	
	}

	//--
	private String flatten(List<Integer> list) {
		StringJoiner joiner = new StringJoiner(",");
		list.stream().forEach(n -> joiner.add(n.toString()));
		return joiner.toString();
	}
}
