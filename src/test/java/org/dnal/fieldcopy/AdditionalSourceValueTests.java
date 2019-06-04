package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class AdditionalSourceValueTests extends BaseTest {
	public static class SomeEntity {
		private int id;
		private String name;
		
		public int id() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	public static class SomeDTO {
		private int id;
		private String name;
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
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
		SomeEntity src = new SomeEntity();
		src.setId(44);
		src.setName("sue");
		SomeDTO dto = new SomeDTO();
		
		FieldCopier copier = createCopier();
		enableLogging();
		copier.copy(src, dto).includeSourceValues("id", src.id()).autoCopy().execute();
		assertEquals("sue", dto.getName());
		assertEquals(44, dto.getId());
	}
	
	@Test
	public void testExecPlanCaching() {
		SomeEntity src = new SomeEntity();
		src.setId(44);
		src.setName("sue");
		SomeDTO dto = new SomeDTO();
		
		FieldCopier copier = createCopier();
		enableLogging();
		copier.copy(src, dto).includeSourceValues("id", src.id()).autoCopy().execute();
		assertEquals("sue", dto.getName());
		assertEquals(44, dto.getId());
		
		log("again..");
		src = new SomeEntity();
		src.setId(45);
		src.setName("suex");
		dto = new SomeDTO();
		copier.copy(src, dto).includeSourceValues("id", src.id()).autoCopy().execute();
		assertEquals("suex", dto.getName());
		assertEquals(45, dto.getId());
	}
	
}
