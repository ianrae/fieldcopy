package org.dnal.fieldcopy.map;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.dnal.fieldcopy.DefaultCopyFactory;
import org.dnal.fieldcopy.FieldCopier;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.junit.Test;


public class MapTests {
	
	public static class Inner {
		private String name;
		private Integer points;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getPoints() {
			return points;
		}
		public void setPoints(Integer points) {
			this.points = points;
		}
	}
	
	public static class User {
		private String name;
		private Map<String,String> map = new HashMap<>();
		private Map<String,Inner> mapInner = new HashMap<>();
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Map<String, String> getMap() {
			return map;
		}
		public void setMap(Map<String, String> map) {
			this.map = map;
		}
		public Map<String, Inner> getMapInner() {
			return mapInner;
		}
		public void setMapInner(Map<String, Inner> mapInner) {
			this.mapInner = mapInner;
		}
	}
	public static class UserDTO {
		private String name;
		private Map<String,String> map = new HashMap<>();
		private Map<String,Inner> mapInner = new HashMap<>();

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Map<String, String> getMap() {
			return map;
		}
		public void setMap(Map<String, String> map) {
			this.map = map;
		}
		public Map<String, Inner> getMapInner() {
			return mapInner;
		}
		public void setMapInner(Map<String, Inner> mapInner) {
			this.mapInner = mapInner;
		}
	}
	
	@Test
	public void test() {
		User user = new User();
		user.setName("bill");
		user.map.put("A", "abc");
		UserDTO dto = new UserDTO();
		
		FieldCopier copier = createCopier();
		copier.copy(user, dto).autoCopy().execute();
		assertEquals("bill", dto.getName());
		assertEquals(1, dto.map.size());
		assertEquals("abc", dto.map.get("A"));
	}
	@Test
	public void testInner() {
		User user = new User();
		user.setName("bill");
		Inner inner = createInner();
		user.mapInner.put("B", inner);
		UserDTO dto = new UserDTO();
		
		FieldCopier copier = createCopier();
		copier.copy(user, dto).autoCopy().execute();
		assertEquals("bill", dto.getName());
		assertEquals(1, dto.mapInner.size());
		
		//hmm. so beanutils simply copied the map values. no cloning or mapping done
		Inner inner2 = dto.mapInner.get("B");
		assertSame(inner2, inner);
	}
	

	private Inner createInner() {
		Inner inner = new Inner();
		inner.setName("rob");
		inner.setPoints(10);
		return inner;
	}
	//--
	private FieldCopier createCopier() {
		DefaultCopyFactory.setLogger(new SimpleConsoleLogger());
		return DefaultCopyFactory.Factory().createCopier();
	}
}
