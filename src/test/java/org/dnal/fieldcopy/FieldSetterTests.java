package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.service.beanutils.BUFieldSetterService;
import org.junit.Test;

public class FieldSetterTests extends BaseTest {
	public static class Dest2 {
		private String name;
		private String title;
		private int port;
		
		public Dest2() {
		}
		public Dest2(String name, String title) {
			this.name = name;
			this.title = title;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public int getPort() {
			return port;
		}
		public void setPort(int port) {
			this.port = port;
		}
	}
	
	public static class MyPrivateFields {
		private String name;
		private String title;
		private int port;
		
		public MyPrivateFields(FieldCopier copier, Dest2 srcObj) {
//			copier.copy(srcObj, this).field("name").field("title").field("port").execute();
			copier.copy(srcObj, this).autoCopy().execute();
		}
		
		public String getName() {
			return name;
		}
		public String getTitle() {
			return title;
		}
		public int getPort() {
			return port;
		}
	}
	
	public static class Pojo1 {
		private String name;
		private String title;
		private int port;
		
		public String calcName() {
			return name;
		}
	}
	
	@Test
	public void test() {
		Dest2 src = new Dest2("bill", "abc");
		src.setPort(3000);
		
		FieldCopier copier = createCopier();
		MyPrivateFields dest = new MyPrivateFields(copier, src);
		assertEquals("bill", dest.getName());
		assertEquals("abc", dest.getTitle());
		assertEquals(3000, dest.getPort());
	}
	
	@Test
	public void test2() {
		BUFieldSetterService svc = new BUFieldSetterService(FieldCopy.getLogger());
		Pojo1 pojo = new Pojo1();
		svc.setField(pojo, "name", "bob");
		assertEquals("bob", pojo.calcName());
	}	
	//--
}
