package org.dnal.fieldcopy.map;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.dnal.fieldcopy.BaseTest;
import org.dnal.fieldcopy.FieldCopier;
import org.dnal.fieldcopy.converter.ConverterContext;
import org.dnal.fieldcopy.converter.FieldInfo;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.CopySpec;
import org.junit.Test;


public class MapConverterTests extends BaseTest {
	
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
	public static class InnerDTO {
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
		private Map<String,InnerDTO> mapInner = new HashMap<>();

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
		public Map<String, InnerDTO> getMapInner() {
			return mapInner;
		}
		public void setMapInner(Map<String, InnerDTO> mapInner) {
			this.mapInner = mapInner;
		}
	}
	
	public static class MyMapValueConverter implements ValueConverter {
		@Override
		public boolean canConvert(FieldInfo source, FieldInfo dest) {
			return source.fieldName.equals("mapInner");
		}
		@Override
		public Object convertValue(Object srcBean, Object value, ConverterContext ctx) {
			Map<String,Inner> srcMap = (Map<String, Inner>) value;
			Map<String,InnerDTO> destMap = new HashMap<String, InnerDTO>();
			
			for(String key: srcMap.keySet()) {
				Inner inner = srcMap.get(key);
				InnerDTO dto = new InnerDTO();
				dto.setName(inner.getName());
				dto.setPoints(inner.getPoints());
				destMap.put(key, dto);
			}
			return destMap;
		}
	}
	public static class MyOtherMapValueConverter implements ValueConverter {
		@Override
		public boolean canConvert(FieldInfo source, FieldInfo dest) {
			return source.fieldName.equals("mapInner");
		}
		@Override
		public Object convertValue(Object srcBean, Object value, ConverterContext ctx) {
			Map<String,Inner> srcMap = (Map<String, Inner>) value;
			Map<String,InnerDTO> destMap = new HashMap<String, InnerDTO>();
			
			//use FieldCopy to copy Inner to InnerDTO objects
			CopySpec spec = ctx.createCopySpec(Inner.class, InnerDTO.class);
			
			for(String key: srcMap.keySet()) {
				Inner inner = srcMap.get(key);
				InnerDTO dto = new InnerDTO();
				
				spec.sourceObj = inner;
				spec.destObj = dto;
				ctx.copySvc.copyFields(spec);
				
				destMap.put(key, dto);
			}
			return destMap;
		}
	}
	
	@Test
	public void testInnerWithConverter() {
		User user = new User();
		user.setName("bill");
		Inner inner = createInner();
		user.mapInner.put("B", inner);
		UserDTO dto = new UserDTO();
		
		FieldCopier copier = createCopier();
		copier.copy(user, dto).withConverters(new MyMapValueConverter()).autoCopy().execute();
		assertEquals("bill", dto.getName());
		assertEquals(1, dto.mapInner.size());
		
		InnerDTO inner2 = dto.mapInner.get("B");
		assertEquals("rob", inner2.getName());
		assertEquals(10, inner2.getPoints().intValue());
	}
	
	@Test
	public void testInnerWithOtherConverter() {
		User user = new User();
		user.setName("bill");
		Inner inner = createInner();
		user.mapInner.put("B", inner);
		UserDTO dto = new UserDTO();
		
		FieldCopier copier = createCopier();
		copier.copy(user, dto).withConverters(new MyOtherMapValueConverter()).autoCopy().execute();
		assertEquals("bill", dto.getName());
		assertEquals(1, dto.mapInner.size());
		
		InnerDTO inner2 = dto.mapInner.get("B");
		assertEquals("rob", inner2.getName());
		assertEquals(10, inner2.getPoints().intValue());
		
		//do it again
		log("and again...");
		user = new User();
		user.setName("billy");
		inner = createInner();
		user.mapInner.put("B", inner);
		dto = new UserDTO();
		
		copier.copy(user, dto).withConverters(new MyOtherMapValueConverter()).autoCopy().execute();
		assertEquals("billy", dto.getName());
		assertEquals(1, dto.mapInner.size());
		
		inner2 = dto.mapInner.get("B");
		assertEquals("rob", inner2.getName());
		assertEquals(10, inner2.getPoints().intValue());
	}
	
	//--
	private Inner createInner() {
		Inner inner = new Inner();
		inner.setName("rob");
		inner.setPoints(10);
		return inner;
	}
}
