package org.dnal.fieldcopy.proploader;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.dnal.fieldcopy.BaseTest;
import org.dnal.fieldcopy.DefaultValueTests.Dest;
import org.dnal.fieldcopy.FieldCopier;
import org.dnal.fieldcopy.TransitiveTests.MyConverter1;
import org.dnal.fieldcopy.core.FieldDescriptor;
import org.dnal.fieldcopy.propertyloader.PropertyCopy;
import org.dnal.fieldcopy.propertyloader.PropertyLoader;
import org.junit.Test;

public class PropLoaderTests extends BaseTest {
	
	public static class ConfigFieldDescriptor implements FieldDescriptor {
		private String name;
		
		public ConfigFieldDescriptor(String name) {
			this.name = name;
		}
		
		@Override
		public String getName() {
			return name;
		}
	}
	
	public static class MyLoader implements PropertyLoader {

		@Override
		public String load(String propertyName) {
			switch(propertyName) {
			case "name":
				return "bob";
			case "title":
				return "Mr";
			case "app.port":
				return "3000";
			default:
				return null;
			}
		}
	}
	
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
		
		public MyPrivateFields(MyLoader loader, FieldCopier copier) {
			copier.copy(loader, this).field("name").field("title").field("app.port", "port").execute();
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
	

	@Test
	public void test() {
		MyLoader loader = new MyLoader();
		Dest dest = new Dest(null, null);
		
		FieldCopier copier = createConfigCopier();
		copier.copy(loader, dest).field("name", "name").execute();
		assertEquals("bob", dest.getName());
		assertEquals(null, dest.getTitle());
	}
	@Test
	public void test2() {
		MyLoader loader = new MyLoader();
		Dest dest = new Dest(null, null);
		
		FieldCopier copier = createConfigCopier();
		copier.copy(loader, dest).field("name", "name").field("title").execute();
		assertEquals("bob", dest.getName());
		assertEquals("Mr", dest.getTitle());
	}
	@Test
	public void test2a() {
		MyLoader loader = new MyLoader();
		Dest dest = new Dest(null, null);
		
		FieldCopier copier = createConfigCopier();
		copier.copy(loader, dest).field("app.port", "name").execute();
		assertEquals("3000", dest.getName());
		assertEquals(null, dest.getTitle());
	}
	@Test
	public void test3() {
		MyLoader loader = new MyLoader();
		Dest2 dest = new Dest2(null, null);
		
		FieldCopier copier = createConfigCopier();
		copier.copy(loader, dest).field("nosuchname", "port").defaultValue(3000).execute();
		assertEquals(3000, dest.getPort());
	}
	@Test
	public void test4() {
		MyLoader loader = new MyLoader();
		Dest2 dest = new Dest2(null, null);
		
		MyConverter1 conv = new MyConverter1();
		
		FieldCopier copier = createConfigCopier();
		copier.copy(loader, dest).withConverters(conv).field("name").execute();
		assertEquals("BOB", dest.getName());
	}
	@Test
	public void testFieldWrite() {
		Dest destx = new Dest();
		try {
			FieldUtils.writeField(destx, "name", "bill", true);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		assertEquals("bill", destx.getName());
	}
	@Test
	public void test5() {
		MyLoader loader = new MyLoader();

		FieldCopier copier = createConfigCopier();
		MyPrivateFields dest = new MyPrivateFields(loader, copier);
		assertEquals("bob", dest.getName());
		assertEquals("Mr", dest.getTitle());
		assertEquals(3000, dest.getPort());
	}
	
	
	private FieldCopier createConfigCopier() {
		return PropertyCopy.createFactory().createCopier();
	}
}
