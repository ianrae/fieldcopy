package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DefaultValueTests extends BaseTest {
	
	public static class Source {
		private String name;
		private String title;

		public Source() {
		}
		public Source(String name, String title) {
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
	}
	public static class Dest {
		private String name;
		private String title;

		public Dest() {
		}
		public Dest(String name, String title) {
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
	}
	
	
	@Test
	public void test() {
		Source src = new Source(null, null);
		Dest dest = new Dest(null, null);
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).cacheKey("key1").field("name", "name").field("title", "title").execute();
		assertEquals(null, dest.getName());
		assertEquals(null, dest.getTitle());
		
		dest = new Dest(null, null);
		copier.copy(src, dest).cacheKey("key2").field("name", "name").defaultValue("sam").field("title", "title").defaultValue("t2").execute();
		assertEquals("sam", dest.getName());
		assertEquals("t2", dest.getTitle());
	}
	@Test
	public void testWhenNotUsed() {
		Source src = new Source("bob", "t1");
		Dest dest = new Dest(null, null);
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).cacheKey("key1").field("name", "name").field("title", "title").execute();
		assertEquals("bob", dest.getName());
		assertEquals("t1", dest.getTitle());
		
		dest = new Dest(null, null);
		copier.copy(src, dest).cacheKey("key2").field("name", "name").defaultValue("sam").field("title", "title").defaultValue("t2").execute();
		assertEquals("bob", dest.getName());
		assertEquals("t1", dest.getTitle());
	}
	
	//--
}
