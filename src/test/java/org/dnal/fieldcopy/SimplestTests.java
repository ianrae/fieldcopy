package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


public class SimplestTests {

	/**
	 * A class that loads the Values into a loader.
	 * This can be done from within the class (by implementing FCBean)
	 * or externally by a separate class.
	 * 
	 * Then reflection or dynamic bytecode generation is not needed.
	 *
	 */
	public interface ConverterLoader {
		ConverterLoader add(String fieldName, Value value);
	}
	
	public interface FCBean {
		void loadIntoConverter(ConverterLoader loader);
	}
	
	public static class Person implements FCBean {
		public final StringValue firstName = new StringValue();
		public final StringValue lastName = new StringValue();
		public final IntegerValue age = new IntegerValue();
		
		//do this with codegen
		@Override
		public void loadIntoConverter(ConverterLoader loader) {
			loader.add("firstName", firstName)
			.add("lastName", lastName)
			.add("age", age);
		}
	}
	
	public static class MyLoader implements ConverterLoader {
		private Map<String,Value> map = new HashMap<>();
		
		@Override
		public ConverterLoader add(String fieldName, Value value) {
			map.put(fieldName, value);
			return this;
		}
	}
	
	public static class FCConverter {
		
		public void convert(FCBean src, FCBean dest) {
			MyLoader loader = new MyLoader();
			src.loadIntoConverter(loader);
			
			MyLoader loader2 = new MyLoader();
			dest.loadIntoConverter(loader2);
			
			for(String fieldName: loader.map.keySet()) {
				Value srcval = loader.map.get(fieldName);
				Value destval = loader2.map.get(fieldName);
				destval.setRawObject(srcval.getRawObject());
			}
		}
	}
	
	@Test
	public void test() {
		Person p = buildPerson();
		
		MyLoader loader = new MyLoader();
		p.loadIntoConverter(loader);
		assertEquals(3, loader.map.size());
		
		Person p2 = new Person();
		MyLoader loader2 = new MyLoader();
		p2.loadIntoConverter(loader2);
		assertEquals(3, loader2.map.size());
		
		for(String fieldName: loader.map.keySet()) {
			Value srcval = loader.map.get(fieldName);
			Value destval = loader2.map.get(fieldName);
			destval.setRawObject(srcval.getRawObject());
		}
		
		assertEquals("bob", p2.firstName.get());
		assertEquals(33, p2.age.getInt());
	}
	
	@Test
	public void test2() {
		Person p = buildPerson();
		Person p2 = new Person();
		
		FCConverter conv = new FCConverter();
		conv.convert(p, p2);
		
		assertEquals("bob", p2.firstName.get());
		assertEquals(33, p2.age.getInt());
	}

	private Person buildPerson() {
		Person p = new Person();
		p.firstName.set("bob");
		p.lastName.set("smith");
		p.age.set(33);
		return p;
	}
	

}
