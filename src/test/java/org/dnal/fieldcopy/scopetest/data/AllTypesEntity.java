package org.dnal.fieldcopy.scopetest.data;

import java.util.Date;
import java.util.List;

public class AllTypesEntity {
	boolean primitiveBool;
	Boolean bool1;
	
	private int primitiveInt;
	private Integer int1;
	private long primitiveLong;
	private Long long1;
	private double primitiveDouble;
	private Double double1;
	
	private Date date1;
	private String string1;
	
	private Colour colour1;
	private Province province1;

	//lists of all types
	private List<Integer> listInt1;
	private List<String> listString1;
	private List<Date> listDate1;
	private List<Long> listLong1;
	private List<Colour> listColour1;
	private List<Province> listProvince1;

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

	public Province getProvince1() {
		return province1;
	}

	public void setProvince1(Province province1) {
		this.province1 = province1;
	}

	public List<String> getListString1() {
		return listString1;
	}

	public void setListString1(List<String> listString1) {
		this.listString1 = listString1;
	}

	public List<Integer> getListInt1() {
		return listInt1;
	}

	public void setListInt1(List<Integer> listInt1) {
		this.listInt1 = listInt1;
	}

	public List<Date> getListDate1() {
		return listDate1;
	}

	public void setListDate1(List<Date> listDate1) {
		this.listDate1 = listDate1;
	}

	public List<Long> getListLong1() {
		return listLong1;
	}

	public void setListLong1(List<Long> listLong1) {
		this.listLong1 = listLong1;
	}

	public List<Colour> getListColour1() {
		return listColour1;
	}

	public void setListColour1(List<Colour> listColour1) {
		this.listColour1 = listColour1;
	}

	public List<Province> getListProvince1() {
		return listProvince1;
	}

	public void setListProvince1(List<Province> listProvince1) {
		this.listProvince1 = listProvince1;
	}
}