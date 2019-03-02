package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.dnal.fc.DefaultCopyFactory;
import org.dnal.fc.FieldCopier;
import org.dnal.fieldcopy.FieldCopyException;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.junit.Before;
import org.junit.Test;


public class BooleanTests {
	
	public static enum Colour {
		RED,
		BLUE,
		YELLOW,
		GREEN
	}
	
	public static class AllTypesEntity {
		private boolean primitiveBool;
		private Boolean bool1;
		
		private int primitiveInt;
		private Integer int1;
		private long primitiveLong;
		private Long long1;
		private double primitiveDouble;
		private Double double1;
		
		private Date date1;
		private String string1;
		
		private Colour colour1;

		public boolean isPrimitiveBool() {
			return primitiveBool;
		}

		public void setPrimitiveBool(boolean primitiveBool) {
			this.primitiveBool = primitiveBool;
		}

		public Boolean getBool1() {
			return bool1;
		}

		public void setBool1(Boolean bool1) {
			this.bool1 = bool1;
		}

		public int getPrimitiveInt() {
			return primitiveInt;
		}

		public void setPrimitiveInt(int primitiveInt) {
			this.primitiveInt = primitiveInt;
		}

		public Integer getInt1() {
			return int1;
		}

		public void setInt1(Integer int1) {
			this.int1 = int1;
		}

		public long getPrimitiveLong() {
			return primitiveLong;
		}

		public void setPrimitiveLong(long primitiveLong) {
			this.primitiveLong = primitiveLong;
		}

		public Long getLong1() {
			return long1;
		}

		public void setLong1(Long long1) {
			this.long1 = long1;
		}

		public double getPrimitiveDouble() {
			return primitiveDouble;
		}

		public void setPrimitiveDouble(double primitiveDouble) {
			this.primitiveDouble = primitiveDouble;
		}

		public Double getDouble1() {
			return double1;
		}

		public void setDouble1(Double double1) {
			this.double1 = double1;
		}

		public Date getDate1() {
			return date1;
		}

		public void setDate1(Date date1) {
			this.date1 = date1;
		}

		public String getString1() {
			return string1;
		}

		public void setString1(String string1) {
			this.string1 = string1;
		}

		public Colour getColour1() {
			return colour1;
		}

		public void setColour1(Colour colour1) {
			this.colour1 = colour1;
		}
	}
	
	public static class AllTypesDTO {
		private boolean primitiveBool;
		private Boolean bool1;
		
		private int primitiveInt;
		private Integer int1;
		private long primitiveLong;
		private Long long1;
		private double primitiveDouble;
		private Double double1;
		
		private Date date1;
		private String string1;
		
		private Colour colour1;

		public boolean isPrimitiveBool() {
			return primitiveBool;
		}

		public void setPrimitiveBool(boolean primitiveBool) {
			this.primitiveBool = primitiveBool;
		}

		public Boolean getBool1() {
			return bool1;
		}

		public void setBool1(Boolean bool1) {
			this.bool1 = bool1;
		}

		public int getPrimitiveInt() {
			return primitiveInt;
		}

		public void setPrimitiveInt(int primitiveInt) {
			this.primitiveInt = primitiveInt;
		}

		public Integer getInt1() {
			return int1;
		}

		public void setInt1(Integer int1) {
			this.int1 = int1;
		}

		public long getPrimitiveLong() {
			return primitiveLong;
		}

		public void setPrimitiveLong(long primitiveLong) {
			this.primitiveLong = primitiveLong;
		}

		public Long getLong1() {
			return long1;
		}

		public void setLong1(Long long1) {
			this.long1 = long1;
		}

		public double getPrimitiveDouble() {
			return primitiveDouble;
		}

		public void setPrimitiveDouble(double primitiveDouble) {
			this.primitiveDouble = primitiveDouble;
		}

		public Double getDouble1() {
			return double1;
		}

		public void setDouble1(Double double1) {
			this.double1 = double1;
		}

		public Date getDate1() {
			return date1;
		}

		public void setDate1(Date date1) {
			this.date1 = date1;
		}

		public String getString1() {
			return string1;
		}

		public void setString1(String string1) {
			this.string1 = string1;
		}

		public Colour getColour1() {
			return colour1;
		}

		public void setColour1(Colour colour1) {
			this.colour1 = colour1;
		}
	}
	
	@Test
	public void test() {
		doCopy("primitiveBool","bool1");
		chkValue(true, true);
		
		reset();
		entity.setPrimitiveBool(false);
		entity.setBool1(false);
		doCopy("primitiveBool","bool1");
		chkValue(false, false);
	}
	
	@Test
	public void testNull() {
		entity.setBool1(null);
		doCopy("bool1");
		assertEquals(null, dto.getBool1());
	}
	
	//----------- primitive ------------
	@Test
	public void testPrimitiveToBoolean() {
		String srcField = "primitiveBool";
		copySrcFieldTo(srcField, "bool1");
		assertEquals(true, dto.getBool1().booleanValue());
	}
	@Test
	public void testPrimitiveToInt() {
		String srcField = "primitiveBool";
		copySrcFieldTo(srcField, "primitiveInt");
		assertEquals(1, dto.getPrimitiveInt());
		
		copySrcFieldTo(srcField, "int1");
		assertEquals(1, dto.getInt1().intValue());
	}
	@Test
	public void testPrimitiveToLong() {
		String srcField = "primitiveBool";
		copySrcFieldTo(srcField, "primitiveLong");
		assertEquals(1L, dto.getPrimitiveLong());
		
		copySrcFieldTo(srcField, "long1");
		assertEquals(1L, dto.getLong1().longValue());
	}
	@Test
	public void testPrimitiveToDouble() {
		String srcField = "primitiveBool";
		copySrcFieldTo(srcField, "primitiveDouble");
		assertEquals(1.0, dto.getPrimitiveDouble(), 0.001);
		
		copySrcFieldTo(srcField, "double1");
		assertEquals(1.0, dto.getDouble1(), 0.001);
	}
	@Test
	public void testPrimitiveToString() {
		String srcField = "primitiveBool";
		copySrcFieldTo(srcField, "string1");
		assertEquals("true", dto.getString1());
		
		reset();
		entity.setPrimitiveBool(false);
		copySrcFieldTo(srcField, "string1", false);
		assertEquals("false", dto.getString1());
	}
	@Test
	public void testPrimitiveToDate() {
		String srcField = "primitiveBool";
		copySrcFieldToFail(srcField, "date1");
		assertEquals(null, dto.getDate1());
	}
	@Test
	public void testPrimitiveToEnum() {
		String srcField = "primitiveBool";
		copySrcFieldToFail(srcField, "colour1");
		assertEquals(null, dto.getColour1());
	}
	
	//----------- Boolean ------------
	@Test
	public void testToBoolean() {
		String srcField = "bool1";
		copySrcFieldTo(srcField, "primitiveBool");
		assertEquals(true, dto.isPrimitiveBool());
	}
	@Test
	public void testToInt() {
		String srcField = "bool1";
		copySrcFieldTo(srcField, "primitiveInt");
		assertEquals(1, dto.getPrimitiveInt());
		
		copySrcFieldTo(srcField, "int1");
		assertEquals(1, dto.getInt1().intValue());
	}
	@Test
	public void testToLong() {
		String srcField = "bool1";
		copySrcFieldTo(srcField, "primitiveLong");
		assertEquals(1L, dto.getPrimitiveLong());
		
		copySrcFieldTo(srcField, "long1");
		assertEquals(1L, dto.getLong1().longValue());
	}
	@Test
	public void testToDouble() {
		String srcField = "bool1";
		copySrcFieldTo(srcField, "primitiveDouble");
		assertEquals(1.0, dto.getPrimitiveDouble(), 0.001);
		
		copySrcFieldTo(srcField, "double1");
		assertEquals(1.0, dto.getDouble1(), 0.001);
	}
	@Test
	public void testToString() {
		String srcField = "bool1";
		copySrcFieldTo(srcField, "string1");
		assertEquals("true", dto.getString1());
		
		reset();
		entity.setBool1(false);
		copySrcFieldTo(srcField, "string1", false);
		assertEquals("false", dto.getString1());
	}
	@Test
	public void testToDate() {
		String srcField = "bool1";
		copySrcFieldToFail(srcField, "date1");
		assertEquals(null, dto.getDate1());
	}
	@Test
	public void testToEnum() {
		String srcField = "bool1";
		copySrcFieldToFail(srcField, "colour1");
		assertEquals(null, dto.getColour1());
	}
	
	//--
	private AllTypesEntity entity;
	private AllTypesDTO dto;
	private FieldCopier copier;
	
	@Before
	public void init() {
		reset();
		copier = createCopier();
		copier.getOptions().logEachCopy = true;
	}
	private void reset() {
		entity = createEntity();
		dto = new AllTypesDTO();
	}
	private AllTypesEntity createEntity() {
		AllTypesEntity entity = new AllTypesEntity();
		entity.primitiveBool = true;
		entity.bool1 = true;
		
		return entity;
	}
	private void doCopy(String...fields) {
		copier.copy(entity, dto).autoCopy().include(fields).execute();
	}
	
	private FieldCopier createCopier() {
		DefaultCopyFactory.setLogger(new SimpleConsoleLogger());
		return DefaultCopyFactory.Factory().createCopier();
	}
	
	private void chkValue(boolean bPrim, boolean b2) {
		assertEquals(bPrim, dto.isPrimitiveBool());
		assertEquals(b2, dto.getBool1().booleanValue());
		assertEquals(null, dto.getString1());
	}
	private void copySrcFieldTo(String srcField, String destField) {
		copySrcFieldTo(srcField, destField, true);
	}
	private void copySrcFieldTo(String srcField, String destField, boolean doReset) {
		if (doReset) {
			reset();
		}
		copier.copy(entity, dto).field(srcField, destField).execute();
	}
	private void copySrcFieldToFail(String srcField, String destField) {
		boolean failed = false;
		try {
			copySrcFieldTo(srcField, destField, true);
		} catch (FieldCopyException e) {
			failed = true;
			System.out.println(e.getMessage());
		}
		assertEquals(true, failed);
	}
	
}
