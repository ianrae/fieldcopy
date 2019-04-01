package org.dnal.fieldcopy;

import static org.junit.Assert.*;

import org.junit.Test;


public class NestedPropertyTests extends BaseTest {
	public static class Person {
		private String name;
		private Address addr;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Address getAddr() {
			return addr;
		}
		public void setAddr(Address addr) {
			this.addr = addr;
		}
	}
	public static class Address {
		private String street;
		private String city;
		
		public String getStreet() {
			return street;
		}
		public void setStreet(String street) {
			this.street = street;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
	}
	public static class PersonOut {
		private String name;
		private String street;
		
		public String getStreet() {
			return street;
		}
		public void setStreet(String street) {
			this.street = street;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}
	
	@Test
	public void test() {
		Person src = new Person();
		src.setName("bob");
		Address addr = new Address();
		addr.setCity("toronto");
		addr.setStreet("main");
		src.setAddr(addr);
		
		PersonOut out = new PersonOut();
		FieldCopier copier = createCopier();
		enableLogging();
		copier.copy(src, out).autoCopy().execute();
		
		assertEquals("bob", out.getName());
		assertEquals(null, out.getStreet());
	}
	
	@Test
	public void test2() {
		Person src = new Person();
		src.setName("bob");
		Address addr = new Address();
		addr.setCity("toronto");
		addr.setStreet("main");
		src.setAddr(addr);
		
		PersonOut out = new PersonOut();
		FieldCopier copier = createCopier();
		enableLogging();
		//not supported
		//copier.copy(src, out).autoCopy().field("addr.street", "street").execute();

		copier.copy(src, out).autoCopy().execute();
		copier.copy(src.getAddr(), out).autoCopy().execute();
		
		assertEquals("bob", out.getName());
		assertEquals("main", out.getStreet());
	}
	
	@Test
	public void testReverse() {
		Person dest = new Person();
		Address addr = new Address();
		dest.setAddr(addr);
		
		PersonOut input = new PersonOut();
		input.setName("bob");
		input.setStreet("main");
		FieldCopier copier = createCopier();
		enableLogging();
		//not supported
		//copier.copy(src, out).autoCopy().field("street", "addr.street").execute();

		copier.copy(input, dest).autoCopy().execute();
		copier.copy(input, dest.getAddr()).autoCopy().execute();
		
		assertEquals("bob", dest.getName());
		assertEquals("main", dest.getAddr().getStreet());
	}
}
