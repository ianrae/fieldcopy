package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.DefaultValueTests.Source;
import org.junit.Test;

public class AutoCopyTests extends BaseTest {
	
	public static class Destination {
		private String name;
		private String title;
		private String title2;

		public Destination() {
		}
		public Destination(String name, String title) {
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
		public String getTitle2() {
			return title2;
		}
		public void setTitle2(String title2) {
			this.title2 = title2;
		}
	}
	public static class Destination2 {
		private String naMe;
		private String tiTle;

		public Destination2() {
		}
		public Destination2(String name, String title) {
			this.naMe = name;
			this.tiTle = title;
		}
		public String getNaMe() {
			return naMe;
		}
		public void setNaMe(String naMe) {
			this.naMe = naMe;
		}
		public String getTiTle() {
			return tiTle;
		}
		public void setTiTle(String tiTle) {
			this.tiTle = tiTle;
		}
	}
	
	
	@Test
	public void test() {
		Source src = new Source("abc", "def");
		Destination dest = new Destination(null, null);
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).autoCopy().execute();
		assertEquals("abc", dest.getName());
		assertEquals("def", dest.getTitle());
		assertEquals(2, copier.mostRecentCopySpec.fieldPairs.size());
	}
	@Test
	public void test2() {
		Source src = new Source("abc", "def");
		Destination dest = new Destination(null, null);
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).autoCopy().field("title","title2").execute();
		assertEquals("abc", dest.getName());
		assertEquals(null, dest.getTitle());
		assertEquals("def", dest.getTitle2());
		assertEquals(2, copier.mostRecentCopySpec.fieldPairs.size());
	}
	
	@Test
	public void testCaseSensitive() {
		Source src = new Source("abc", "def");
		Destination2 dest = new Destination2(null, null);
		
		FieldCopier copier = createCopier();
		copier.options.autoCopyCaseSensitiveMatch = true;
		copier.copy(src, dest).autoCopy().execute();
		//no match name != naMe
		assertEquals(null, dest.getNaMe());
		assertEquals(null, dest.getTiTle());
		assertEquals(0, copier.mostRecentCopySpec.fieldPairs.size());
	}
	
	@Test
	public void testCaseInsensitive() {
		Source src = new Source("abc", "def");
		Destination2 dest = new Destination2(null, null);
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).autoCopy().execute();
		assertEquals("abc", dest.getNaMe());
		assertEquals("def", dest.getTiTle());
		assertEquals(2, copier.mostRecentCopySpec.fieldPairs.size());
	}
	
//	@Test
//	public void test() {
//		Source src = new Source(null, null);
//		Dest dest = new Dest(null, null);
//		
//		FieldCopier copier = createCopier();
//		copier.copy(src, dest).cacheKey("key1").field("name", "name").field("title", "title").execute();
//		assertEquals(null, dest.getName());
//		assertEquals(null, dest.getTitle());
//		
//		dest = new Dest(null, null);
//		copier.copy(src, dest).cacheKey("key2").field("name", "name").defaultValue("sam").field("title", "title").defaultValue("t2").execute();
//		assertEquals("sam", dest.getName());
//		assertEquals("t2", dest.getTitle());
//	}
	
	//--
}
