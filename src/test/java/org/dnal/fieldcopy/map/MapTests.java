package org.dnal.fieldcopy.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.Map;

import org.dnal.fieldcopy.DefaultCopyFactory;
import org.dnal.fieldcopy.FieldCopier;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.junit.Test;

/**
 * Maps are key-value pairs.  FieldCopy simply copies the key-value pairs of a source map to a
 * destination map.  In fact, BeanUtils doesn't even copy; it simply assigns dest.someMapField = source.someMapField.
 * 
 * This can lead to several problems:
 * - if the destination map is of different type, we have a problem. For example a source Map<String,String>
 *  and a destination Map<String,Double> will encounter problems since bean utils simply assigns
 *  the source map object to the destination map. This will lead to run-time errors.
 *  
 * - keys and values in the map are not converted using any of the registered FieldCopy converters or mappings.
 * 
 * If the objects you are copying contain maps, and you want conversion done on the keys and/or values
 * in the maps, use a converter.
 * 
 * @author Ian Rae
 *
 */
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
		String value = "abc";
		user.map.put("A", value);
		UserDTO dto = new UserDTO();
		
		FieldCopier copier = createCopier();
		copier.copy(user, dto).autoCopy().execute();
		assertEquals("bill", dto.getName());
		assertEquals(1, dto.map.size());
		assertEquals("abc", dto.map.get("A"));
		String value2 = dto.map.get("A");
		assertSame(value2, value);
		
		//yikes. beanutils simply copies the entire map!
		assertSame(user.map, dto.map);
	}
	
	@Test
	public void testNull() {
		User user = new User();
		user.setName("bill");
		String value = "abc";
		user.map.put("A", value);
		UserDTO dto = new UserDTO();
		dto.map = null; //it will be created
		//but be careful. beanutils simply copies values
		//if the dest map is of a different type, this is not honoured.
		//get source is Map<String,String> and dest is Map<String,Double>
		
		FieldCopier copier = createCopier();
		copier.copy(user, dto).autoCopy().execute();
		assertEquals("bill", dto.getName());
		assertEquals(1, dto.map.size());
		assertEquals("abc", dto.map.get("A"));
		String value2 = dto.map.get("A");
		assertSame(value2, value);
		
		assertSame(user.map, dto.map);
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
	
	//--
	private FieldCopier createCopier() {
		DefaultCopyFactory.setLogger(new SimpleConsoleLogger());
		return DefaultCopyFactory.Factory().createCopier();
	}

	private Inner createInner() {
		Inner inner = new Inner();
		inner.setName("rob");
		inner.setPoints(10);
		return inner;
	}
}
