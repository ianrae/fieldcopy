package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.dnal.fieldcopy.log.SimpleLogger;
import org.junit.Test;

public class ValueTests {


	/**
	 * Not using enum so that field types are extensbile.
	 * @author ian
	 *
	 */
	public static class FieldTypes {
		public static final int BOOLEAN = 1;
		public static final int INTEGER = 2;
		public static final int LONG = 3;
		public static final int DOUBLE = 4;
		public static final int STRING = 5;
		public static final int DATE = 6;
		public static final int ENUM = 7;
		public static final int LIST = 8;
		public static final int STRUCT = 9;
	}

	/*
	xINTEGER,
	xLONG,
	xNUMBER,
	xBOOLEAN,
	xSTRING,
	xDATE,
	xLIST,
	MAP,
	STRUCT,
	xENUM,
	ANY
	 * 
	 */

	public interface Value {
		int getFieldType();
		String name();
		void setNameInternal(String name);
		Object getRawObject();
		void setRawObject(Object val);
		boolean isNull();
	}
	public abstract static class BaseValue implements Value {
		protected String name;
		protected Object rawObject;

		@Override
		public String name() {
			return name;
		}
		@Override
		public void setNameInternal(String name) {
			this.name = name;
		}

		@Override
		public Object getRawObject() {
			return rawObject;
		}

		@Override
		public void setRawObject(Object val) {
			rawObject = val;
		}

		@Override
		public boolean isNull() {
			return rawObject == null;
		}
		@Override
		public String toString() {
			return rawObject.toString();
		}
	}
	public static class IntegerValue extends BaseValue {
		public Integer get() {
			Integer n = (Integer) rawObject;
			return n;
		}
		public void set(Integer val) {
			rawObject = val;
		}
		
		public int getInt() {
			Integer n = (Integer) rawObject;
			return n;
		}
		@Override
		public int getFieldType() {
			return FieldTypes.INTEGER;
		}
	}
	public static class LongValue extends BaseValue {
		public Long get() {
			Long n = (Long) rawObject;
			return n;
		}
		public void set(Long val) {
			rawObject = val;
		}
		
		public long getLong() {
			Long n = (Long) rawObject;
			return n;
		}
		@Override
		public int getFieldType() {
			return FieldTypes.LONG;
		}
	}
	public static class DoubleValue extends BaseValue {
		public Double get() {
			Double n = (Double) rawObject;
			return n;
		}
		public void set(Double val) {
			rawObject = val;
		}
		
		public double getDouble() {
			Double n = (Double) rawObject;
			return n;
		}
		@Override
		public int getFieldType() {
			return FieldTypes.DOUBLE;
		}
	}
	public static class BooleanValue extends BaseValue {
		public Boolean get() {
			Boolean n = (Boolean) rawObject;
			return n;
		}
		public void set(Boolean val) {
			rawObject = val;
		}
		@Override
		public int getFieldType() {
			return FieldTypes.BOOLEAN;
		}
	}
	public static class StringValue extends BaseValue {
		public String get() {
			return rawObject.toString();
		}
		public void set(String val) {
			rawObject = val;
		}
		@Override
		public int getFieldType() {
			return FieldTypes.STRING;
		}
	}
	public static class DateValue extends BaseValue {
		public Date get() {
			Date n = (Date) rawObject;
			return n;
		}
		public void set(Date val) {
			rawObject = val;
		}
		
		@Override
		public int getFieldType() {
			return FieldTypes.DATE;
		}
	}
	public static class EnumValue<T extends Enum<?>> extends BaseValue {
		private Class<T> enumClass;

		public EnumValue(Class<T> enumClass) {
			this.enumClass = enumClass;
			if (enumClass == null) {
				throw new FieldCopyException("NULL passed to EnumValue constructor");
			}
			if (! enumClass.isEnum()) {
				String error = String.format("'%s' is not an enum", enumClass.getName());
				throw new FieldCopyException(error);
			}
		}
		public T get() {
			@SuppressWarnings("unchecked")
			T t = (T) rawObject;
			return t;
		}
		public void set(T val) {
			rawObject = val;
		}
		public Class<T> getEnumClass() {
			return enumClass;
		}
		
		@Override
		public int getFieldType() {
			return FieldTypes.ENUM;
		}
	}
//	public static class ListValue extends BaseValue {
//		private int elementFieldType;
//
//		public ListValue(int elementFieldType) {
//			this.elementFieldType = elementFieldType;
//		}
//		public List<Value> get() {
//			@SuppressWarnings("unchecked")
//			List<Value> t = (List<Value>) rawObject;
//			return t;
//		}
//		public void set(List<Value> val) {
//			rawObject = val;
//		}
//		public int getElementFieldType() {
//			return elementFieldType;
//		}
//		@Override
//		public int getFieldType() {
//			return FieldTypes.LIST;
//		}
//	}
	public static class ListValue<T extends Value> extends BaseValue {
		private Class<T> elementClass;

		public ListValue(Class<T> elementClass) {
			this.elementClass = elementClass;
		}
		public List<T> get() {
			@SuppressWarnings("unchecked")
			List<T> t = (List<T>) rawObject;
			return t;
		}
		public void set(List<T> val) {
			rawObject = val;
		}
		public Class<T> getElementClass() {
			return elementClass;
		}
		@Override
		public int getFieldType() {
			return FieldTypes.LIST;
		}
	}
	public static class YListValue<T> extends BaseValue {
		private Class<T> elementClass;
		public YListValue(Class<T> elementClass) {
			this.elementClass = elementClass;
			this.rawObject = new ArrayList<>();
		}
		
		public Value createEmptyElement(ValueFactory factory) {
			if (elementClass.isEnum()) {
				return factory.createEnumValue((Class<? extends Enum<?>>) elementClass);
			} else {
				return factory.createYValue(elementClass);
			}
		}
		
		@SuppressWarnings("unchecked")
		public T get(int index) {
			List<Value> list = getValueList();
			Value val = list.get(index);
			return (T) val.getRawObject();
		}
		public void add(T obj) {
			List<Value> list = getValueList();
			Value val = new StringValue();
			val.setRawObject(obj);
			list.add(val);
		}
		public List<T> getList() {
			List<Value> list = getValueList();
			List<T> outlist = new ArrayList<>();
			for(Value val: list) {
				Object obj = val.getRawObject();
				outlist.add((T) obj);
			}
			return outlist;
		}
		public List<Value> getValueList() {
			@SuppressWarnings("unchecked")
			List<Value> list = (List<Value>) rawObject;
			return list;
		}
		public Class<T> getElementClass() {
			return elementClass;
		}
		@Override
		public int getFieldType() {
			return FieldTypes.LIST;
		}
	}
	
	public static class StringListValue extends ListValue<StringValue> {
		public StringListValue() {
			super(StringValue.class);
		}
		public List<String> getList() {
			@SuppressWarnings("unchecked")
			List<StringValue> t = (List<StringValue>) rawObject;
			List<String> list = new ArrayList<>();
			for(StringValue val: t) {
				list.add(val.get());
			}
			return list;
		}
		public void setList(List<String> list) {
			List<StringValue> vallist = new ArrayList<>();
			for(String s: list) {
				StringValue sval = new StringValue();
				sval.set(s);
				vallist.add(sval);
			}
			rawObject = vallist;
		}
	}
	//TODO define bool, int, etc list
	
	public static class EnumListValue<T extends Enum<T>> extends ListValue<EnumValue> {
		private Class<T> enumClass;
		
		public EnumListValue(Class<T> enumClass) {
			super(EnumValue.class);
			this.enumClass = enumClass;
		}
		public List<T> getList() {
			@SuppressWarnings("unchecked")
			List<EnumValue<T>> vallist = (List<EnumValue<T>>) rawObject;
			List<T> list = new ArrayList<>();
			for(Value val: vallist) {
				@SuppressWarnings("unchecked")
				T obj = (T) val.getRawObject();
				list.add(obj);
			}
			return list;
		}
		public void setList(List<T> list) {
			List<Value> vallist = new ArrayList<>();
			for(T s: list) {
				EnumValue<T> sval = new EnumValue<>(enumClass);
				sval.set(s);
				vallist.add(sval);
			}
			rawObject = vallist;
		}
		public Class<T> getEnumClass() {
			return enumClass;
		}
	}
	
	public static class StructValue<T> extends BaseValue {
		private Class<T> structClass;

		public StructValue(Class<T> structClass) {
			this.structClass = structClass;
			if (structClass == null) {
				throw new FieldCopyException("NULL passed to StructValue constructor");
			}
		}
		public T get() {
			@SuppressWarnings("unchecked")
			T t = (T) rawObject;
			return t;
		}
		public void set(T val) {
			rawObject = val;
		}
		public Class<T> getStructClass() {
			return structClass;
		}
		
		@Override
		public int getFieldType() {
			return FieldTypes.STRUCT;
		}
	}

	public interface ValueFactory {
		Value createValue(int fieldType);
		Value createEnumValue(Class<? extends Enum<?>> enumClass);
		Value createXValue(Class<?>clazz);
		Value createYValue(Class<?>clazz);
	}
	
	public static class DefaultValueFactory implements ValueFactory {
		@Override
		public Value createValue(int fieldType) {
			switch(fieldType) {
			case FieldTypes.BOOLEAN:
				return new BooleanValue();
			case FieldTypes.INTEGER:
				return new IntegerValue();
			case FieldTypes.LONG:
				return new LongValue();
			case FieldTypes.DOUBLE:
				return new DoubleValue();
			case FieldTypes.DATE:
				return new DateValue();
			case FieldTypes.STRING:
				return new StringValue();
			default:
				throw new FieldCopyException(String.format("unknown fieldtype %d", fieldType));
			}
		}
		@Override
		public Value createEnumValue(Class<? extends Enum<?>> enumClass) {
			return new EnumValue<>(enumClass); 
		}
		@Override
		public Value createXValue(Class<?>clazz) {
			if (clazz.equals(BooleanValue.class)) {
				return new BooleanValue();
			} else if (clazz.equals(IntegerValue.class)) {
				return new IntegerValue();
			} else if (clazz.equals(LongValue.class)) {
				return new LongValue();
			} else if (clazz.equals(DoubleValue.class)) {
				return new DoubleValue();
			} else if (clazz.equals(DateValue.class)) {
				return new DateValue();
			} else if (clazz.equals(StringValue.class)) {
				return new StringValue();
			} else {
				throw new FieldCopyException(String.format("unknown class: %s", clazz.getName()));
			}
		}
		@Override
		public Value createYValue(Class<?>clazz) {
			if (clazz.equals(Boolean.class)) {
				return new BooleanValue();
			} else if (clazz.equals(Integer.class)) {
				return new IntegerValue();
			} else if (clazz.equals(Long.class)) {
				return new LongValue();
			} else if (clazz.equals(Double.class)) {
				return new DoubleValue();
			} else if (clazz.equals(Date.class)) {
				return new DateValue();
			} else if (clazz.equals(String.class)) {
				return new StringValue();
			} else {
				throw new FieldCopyException(String.format("unknown class: %s", clazz.getName()));
			}
		}
	}
	
	public static class FieldCopyException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public FieldCopyException(String msg) {
			super(msg);
		}
	}
	public static class FieldOptions {
		public boolean printStackTrace = false;
		public boolean logEachCopy = false;
	}

	public static class FieldCopyUtils {
		public static String className(Class<?> clazz) {
			return clazz.getSimpleName();
		}
		public static String classNameForObj(Object obj) {
			return obj.getClass().getSimpleName();
		}
		public static String objToString(Object obj) {
			String tmp = (obj == null) ? "(null)" : obj.toString();
			return tmp;
		}
		
		//uses reflection
		public static Object createEnumObject(String name, Class<? extends Enum<?>> clazz) {
			Object value = null;
			try {
			    Method valueOf = clazz.getMethod("valueOf", String.class);
			    value = valueOf.invoke(null, name);
			} catch ( ReflectiveOperationException e) {
				throw new FieldCopyException("enum copy failed!: " + e.getMessage());
			}				
			return value;
		}
	}


	public static class ClassFieldInfo {
		private Class<?> targetClass;
		private Map<String,Field> fieldMap = new HashMap<>();
		private SimpleLogger logger;
		private FieldOptions options;

		public ClassFieldInfo(FieldOptions options, SimpleLogger logger) {
			this.options = options;
			this.logger = logger;
		}
		public Value getValueField(Object obj, String fieldName) {
			if (obj == null) {
				String error = String.format("NULL passed to getValueField for class %s, fieldName: %s", FieldCopyUtils.className(targetClass), fieldName);
				failAndThrow(null, error);
			}

			Field fld = fieldMap.get(fieldName);
			Value value = null;
			try {
				value = (Value) fld.get(obj);
			} catch (IllegalArgumentException e) {
				String error = String.format("IllegalArgumentException in getValueField: Class '%s' (expected '%s'), fieldName: %s", FieldCopyUtils.classNameForObj(obj), FieldCopyUtils.className(targetClass), fieldName);
				failAndThrow(e, error);
			} catch (IllegalAccessException e) {
				String error = String.format("IllegalAccessException in getValueField: Class '%s' (expected '%s'), fieldName: %s", FieldCopyUtils.classNameForObj(obj), FieldCopyUtils.className(targetClass), fieldName);
				failAndThrow(e, error);
			}
			return value;
		}

		private void failAndThrow(Exception e, String errMsg) {
			logger.log("ERROR: %s", errMsg);
			if (e != null && options.printStackTrace) {
				e.printStackTrace();
			}
			throw new FieldCopyException(errMsg);
		}
	}

	public static class FieldRegistry {
		private Map<Class<?>, ClassFieldInfo> map = new HashMap<>();
		private SimpleLogger logger;
		private FieldOptions options = new FieldOptions();

		public FieldRegistry(SimpleLogger logger) {
			this.logger = logger;
		}
		public void register(Class<?> clazz, ClassFieldInfo info) {
			map.put(clazz, info);
		}
		public boolean isRegistered(Class<?> clazz) {
			return map.containsKey(clazz);
		}
		public ClassFieldInfo find(Class<?> clazz) {
			return map.get(clazz);
		}

		public void registerIfNeeded(Object obj) {
			Class<?> clazz = obj.getClass();
			if (isRegistered(clazz)) {
				return;
			}

			ClassFieldInfo info = new ClassFieldInfo(options, logger);
			info.targetClass = clazz; //for logging
			Field[] fields = clazz.getFields();
			for(Field field: fields) {
				String fieldName = field.getName();
				Class<?> clazzField = field.getType();
				if (Value.class.isAssignableFrom(clazzField)) {
					info.fieldMap.put(fieldName, field);
					setValueName(obj, fieldName, info);
				}
			}
			logger.log("FieldRegister: register '%s': %d fields",FieldCopyUtils.className(clazz), info.fieldMap.size());
			map.put(clazz, info);
		}
		private void setValueName(Object obj, String fieldName, ClassFieldInfo info) {
			Value value = info.getValueField(obj, fieldName); 
			if (value != null) {
				value.setNameInternal(fieldName);
			}
		}
		public void setValueNameIfNeeded(Object obj, Value value) {
			if (value != null && value.name() == null) {
				ClassFieldInfo info = find(obj.getClass());
				Field[] fields = obj.getClass().getFields();
				for(Field field: fields) {
					String fieldName = field.getName();
					if (info.fieldMap.containsKey(fieldName)) {
						setValueName(obj, fieldName, info);
					}
				}
			}
		}
		
		/**
		 * We lazily register classes.
		 * We lazily set value.name in each object that is a source or destination of a copy
		 * @param obj
		 */
		public void prepareObj(Object obj) {
			if (obj == null) {
				return;
			}
			registerIfNeeded(obj);
			ClassFieldInfo info1 = find(obj.getClass());
			for(String fieldName: info1.fieldMap.keySet()) {
				Value val1 = info1.getValueField(obj, fieldName);
				if (val1 == null) {
					continue;
				}
				if (val1.name() == null) {
					val1.setNameInternal(fieldName);
				} else {
					break; //assume that if one value's name is set, then all are.
				}
			}
		}
	}
	
	public static class CopyHandlerContext {
		public SimpleLogger logger;
		public ValueCopier copier;
	}
	public interface ObjectCopyHandler {
		void copyTo(Value src, Value dest, CopyHandlerContext ctx);
	}
	public static abstract class BaseObjectCopyHandler implements ObjectCopyHandler {
		protected void throwError(Object src, int fieldType,  CopyHandlerContext ctx) {
			String error = String.format("Incompatible field copy: %d", fieldType);
			throw new FieldCopyException(error);
		}
		
		@Override
		public void copyTo(Value src, Value dest,  CopyHandlerContext ctx) {
			if (src.getClass().equals(dest.getClass())) { 
				dest.setRawObject(src.getRawObject());
				return;
			}
			
			Object obj = copyToRawObject(src.getRawObject(), dest.getFieldType(), ctx);
			dest.setRawObject(obj);
		}		
		protected abstract Object copyToRawObject(Object src, int fieldType,  CopyHandlerContext ctx);
		
	}
	public static class BooleanCopyHandler extends BaseObjectCopyHandler {
		@Override
		protected Object copyToRawObject(Object src, int fieldType,  CopyHandlerContext ctx) {
			Boolean n = (Boolean) src;
			switch(fieldType) {
			case FieldTypes.STRING:
				return n.toString();
			default:
				break;
			}
			throwError(src, fieldType, ctx);
			return null;
		}
	}
	public static class IntegerCopyHandler extends BaseObjectCopyHandler {
		@Override
		protected Object copyToRawObject(Object src, int fieldType,  CopyHandlerContext ctx) {
			Integer n = (Integer) src;
			switch(fieldType) {
			case FieldTypes.LONG:
				return n.longValue();
			case FieldTypes.DOUBLE:
				return n.doubleValue();
			case FieldTypes.STRING:
				return n.toString();
			default:
				break;
			}
			throwError(src, fieldType, ctx);
			return null;
		}
	}
	public static class LongCopyHandler extends BaseObjectCopyHandler {
		@Override
		protected Object copyToRawObject(Object src, int fieldType,  CopyHandlerContext ctx) {
			Long n = (Long) src;
			switch(fieldType) {
			case FieldTypes.INTEGER:
			{
				if (n >= Integer.MIN_VALUE && n <= Integer.MAX_VALUE) {
					return n.intValue();
				}
			}
			case FieldTypes.DOUBLE:
				return n.doubleValue();
			case FieldTypes.DATE:
				return new Date(n);
			case FieldTypes.STRING:
				return n.toString();
			default:
				break;
			}
			throwError(src, fieldType, ctx);
			return null;
		}
	}
	public static class DoubleCopyHandler extends BaseObjectCopyHandler {
		@Override
		protected Object copyToRawObject(Object src, int fieldType,  CopyHandlerContext ctx) {
			Double n = (Double) src;
			switch(fieldType) {
			case FieldTypes.INTEGER:
			{
				long iPart = (long) n.doubleValue();
				double fPart = n - iPart;
				if (fPart == 0.0D) {
					if (iPart >= Integer.MIN_VALUE && iPart <= Integer.MAX_VALUE) {
						return (int)iPart;
					}
				}
			}
			case FieldTypes.LONG:
			{
				long iPart = (long) n.doubleValue();
				double fPart = n - iPart;
				if (fPart == 0.0D) {
					return iPart;
				}
			}
			case FieldTypes.STRING:
				return n.toString();
			default:
				break;
			}
			throwError(src, fieldType, ctx);
			return null;
		}
	}
	public static class DateCopyHandler extends BaseObjectCopyHandler {
		@Override
		protected Object copyToRawObject(Object src, int fieldType,  CopyHandlerContext ctx) {
			Date dt = (Date) src;
			switch(fieldType) {
			case FieldTypes.LONG:
				return dt.getTime();
			case FieldTypes.STRING:
				return dt.toString(); //TODO: add formatter
			default:
				break;
			}
			throwError(src, fieldType, ctx);
			return null;
		}
	}
	public static class StringCopyHandler extends BaseObjectCopyHandler {
		@Override
		public void copyTo(Value src, Value dest,  CopyHandlerContext ctx) {
			if (src.getClass().equals(dest.getClass())) { 
				dest.setRawObject(src.getRawObject());
				return;
			}
			
			if (EnumValue.class.isAssignableFrom(dest.getClass())) {
				EnumValue<?> evalue2 = (EnumValue<?>) dest;
			
				//use reflection
				String name = src.getRawObject().toString();
				Class<? extends Enum<?>> clazz = evalue2.getEnumClass();
				Object value = FieldCopyUtils.createEnumObject(name, clazz);
				dest.setRawObject(value);
			} else {
				Object obj = copyToRawObject(src.getRawObject(), dest.getFieldType(), ctx);
				dest.setRawObject(obj);
			}
		}		
		
		
		@Override
		protected Object copyToRawObject(Object src, int fieldType,  CopyHandlerContext ctx) {
			String s = (String) src;
			switch(fieldType) {
			case FieldTypes.BOOLEAN:
				if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false")) {
					return Boolean.parseBoolean(s);
				}
				break;
			case FieldTypes.INTEGER:
			{
				Integer n = 0;
				try {
					n = Integer.parseInt(s);
				} catch (NumberFormatException e) {
				}
				return n;
			}
			case FieldTypes.LONG:
			{
				Long n = 0L;
				try {
					n = Long.parseLong(s);
				} catch (NumberFormatException e) {
				}
				return n;
			}
			case FieldTypes.DOUBLE:
			{
				Double n = 0.0;
				try {
					n = Double.parseDouble(s);
				} catch (NumberFormatException e) {
				}
				return n;
			}
			default:
				break;
			}
			throwError(src, fieldType, ctx);
			return null;
		}
	}
	public static class EnumCopyHandler extends BaseObjectCopyHandler {
		
		@Override
		public void copyTo(Value src, Value dest,  CopyHandlerContext ctx) {
			if (EnumValue.class.isAssignableFrom(dest.getClass())) {
				EnumValue<?> evalue1 = (EnumValue<?>) src;
				EnumValue<?> evalue2 = (EnumValue<?>) dest;
			
				if (evalue1.getEnumClass().equals(evalue2.getEnumClass())) {
					dest.setRawObject(src.getRawObject());
					return;
				}
				
				//use reflection
				String name = evalue1.getRawObject().toString();
				Class<? extends Enum<?>> clazz = evalue2.getEnumClass();
				Object value = FieldCopyUtils.createEnumObject(name, clazz);
				dest.setRawObject(value);
			} else {
				Object obj = copyToRawObject(src.getRawObject(), dest.getFieldType(), ctx);
				dest.setRawObject(obj);
			}
		}		
		
		
		@Override
		protected Object copyToRawObject(Object src, int fieldType,  CopyHandlerContext ctx) {
			switch(fieldType) {
			case FieldTypes.STRING:
				return src.toString();
			default:
				break;
			}
			throwError(src, fieldType, ctx);
			return null;
		}
	}
	public static class ListCopyHandler extends BaseObjectCopyHandler {
		
		private ValueFactory factory;

		public ListCopyHandler(ValueFactory factory) {
			this.factory = factory;
		}
		@Override
		public void copyTo(Value src, Value dest,  CopyHandlerContext ctx) {
			if (YListValue.class.isAssignableFrom(dest.getClass())) {
				YListValue<?> list1 = (YListValue<?>) src;
				YListValue<?> list2 = (YListValue<?>) dest;
			
				if (list1.getElementClass().equals(list2.getElementClass())) {
					//TODO: not copying the list may cause problems in app logic. fix
					List<Value> shallowCopy = new ArrayList<>(list1.getValueList());
					dest.setRawObject(shallowCopy);
					return;
				}

				List<Value> newlist = new ArrayList<>();
				for(Value val: list1.getValueList()) {
					Value val2 = list2.createEmptyElement(factory);
					ctx.copier.copy(val, val2);
					newlist.add(val2);
				}
				list2.setRawObject(newlist);
			} else if (ListValue.class.isAssignableFrom(dest.getClass())) {
				ListValue<?> list1 = (ListValue<?>) src;
				@SuppressWarnings("unchecked")
				ListValue<? extends Value> list2 = (ListValue<? extends Value>) dest;
			
				if (list1.getElementClass().equals(list2.getElementClass())) {
					//TODO: not copying the list may cause problems in app logic. fix
					@SuppressWarnings("unchecked")
					List<Value> shallowCopy = new ArrayList<>(list1.get());
					dest.setRawObject(shallowCopy);
					return;
				}

				List newlist = new ArrayList<>();
				for(Value val: list1.get()) {
					Value val2 = createXValue(list2, list2.getElementClass());
					ctx.copier.copy(val, val2);
					newlist.add(val2);
				}
				list2.set(newlist);
			} else {
				throwError(src, dest.getFieldType(), ctx);
			}
		}		
		@Override
		protected Object copyToRawObject(Object src, int fieldType,  CopyHandlerContext ctx) {
			throwError(src, fieldType, ctx);
			return null;
		}

//		private Value createValue(ListValue list, int fieldType) {
//			//only way to support enum value creation is to use EnumListValue
//			if (list instanceof EnumListValue<?>) {
//				EnumListValue<?> evlist = (EnumListValue<?>) list;
//				return factory.createEnumValue(fieldType, evlist.getEnumClass());
//			}
//			
//			return factory.createValue(fieldType); 
//		}
		private Value createXValue(ListValue<?> list, Class<? extends Value> elementClass) {
			//only way to support enum value creation is to use EnumListValue
			if (elementClass.equals(EnumValue.class)) {
				EnumListValue listxx = (EnumListValue) list;
				return factory.createEnumValue(listxx.getEnumClass());
			}
			
			return factory.createXValue(elementClass); 
		}
	}
	
	public static class StructCopyHandler extends BaseObjectCopyHandler {
		
		private FieldCopyBuilder builder;
		public StructCopyHandler(FieldCopyBuilder builder) {
			this.builder = builder;
		}
		@Override
		public void copyTo(Value src, Value dest,  CopyHandlerContext ctx) {
			if (StructValue.class.isAssignableFrom(dest.getClass())) {
				StructValue<?> struct1 = (StructValue<?>) src;
				StructValue<?> struct2 = (StructValue<?>) dest;
			
				Object obj1 = struct1.getRawObject();
				Object obj2 = struct2.getRawObject();
				if (obj2 == null) {
					obj2 = createObj(struct2.getStructClass());
					struct2.setRawObject(obj2);
				}
				builder.copy(obj1, obj2).autoCopy().execute();
			} else {
				throwError(src, dest.getFieldType(), ctx);
			}
		}		
		private Object createObj(Class<?> structClass) {
			Object obj = null;
			//structClass must have a default constructor
			try {
				obj = structClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				//e.printStackTrace();
				String error = String.format("createObj failed: %s", e.getMessage());
				throw new FieldCopyException(error);
			}
			
			return obj;
		}
		@Override
		protected Object copyToRawObject(Object src, int fieldType,  CopyHandlerContext ctx) {
			throwError(src, fieldType, ctx);
			return null;
		}
	}
	

	public static class ValueCopier {
		private Map<Integer,ObjectCopyHandler> handlerMap = new HashMap<>();
		private SimpleLogger logger;
		
		public ValueCopier(SimpleLogger logger, FieldRegistry registry) {
			this(logger, registry, new DefaultValueFactory());
		}
		public ValueCopier(SimpleLogger logger, FieldRegistry registry, ValueFactory factory) {
			this.logger = logger;
			
			handlerMap.put(FieldTypes.BOOLEAN, new BooleanCopyHandler());
			handlerMap.put(FieldTypes.INTEGER, new IntegerCopyHandler());
			handlerMap.put(FieldTypes.LONG, new LongCopyHandler());
			handlerMap.put(FieldTypes.DOUBLE, new DoubleCopyHandler());
			handlerMap.put(FieldTypes.STRING, new StringCopyHandler());
			handlerMap.put(FieldTypes.DATE, new DateCopyHandler());
			handlerMap.put(FieldTypes.ENUM, new EnumCopyHandler());
			
			//list 
			handlerMap.put(FieldTypes.LIST, new ListCopyHandler(factory));
			
			//struct handler
			FieldCopyBuilder builder = new FieldCopyBuilder(registry, this, logger);
			handlerMap.put(FieldTypes.STRUCT, new StructCopyHandler(builder));
		}
		public void copy(Value val1, Value val2) {
			if (val1.isNull()) {
				val2.setRawObject(null);
			} else {
				ObjectCopyHandler handler = handlerMap.get(val1.getFieldType());
				if (handler == null) {
					String error = String.format("Incompatible field copy: %s (no handler)", FieldCopyUtils.classNameForObj(val1));
					throw new FieldCopyException(error);
				} else {
					CopyHandlerContext ctx = new CopyHandlerContext();
					ctx.logger = logger;
					ctx.copier = this;
					handler.copyTo(val1, val2, ctx);
				}
			}
		}
	}

	public static class FieldCopier {
		private SimpleLogger logger;
		private FieldRegistry registry;
		private ValueCopier copier;

		public FieldCopier(FieldRegistry registry, ValueCopier copier, SimpleLogger logger) {
			this.logger = logger;
			this.registry = registry;
			this.copier = copier;
		}

		public void copyField(Object sourceObj, Object destObj, Value sourceVal, Value destVal) {
			if (sourceObj == null) {
				String error = String.format("copyField. NULL passed to sourceObj");
				throw new FieldCopyException(error);
			}
			if (destObj == null) {
				String error = String.format("copyField. NULL passed to destObj");
				throw new FieldCopyException(error);
			}
			//ensure value.name set by registering if not already registered
			registry.prepareObj(sourceObj);
			registry.prepareObj(destObj);

			doCopyField(sourceObj, destObj, sourceVal.name(), destVal.name());
		}

		public void copyField(Object sourceObj, Object destObj, String sourceFieldName, String destFieldName) {
			if (sourceObj == null) {
				String error = String.format("copyField. NULL passed to sourceObj. fieldName: %s", sourceFieldName);
				throw new FieldCopyException(error);
			}
			if (destObj == null) {
				String error = String.format("copyField. NULL passed to destObj. fieldName: %s", sourceFieldName);
				throw new FieldCopyException(error);
			}
			registry.prepareObj(sourceObj);
			registry.prepareObj(destObj);
			doCopyField(sourceObj, destObj, sourceFieldName, destFieldName);
		}
		
		private void doCopyField(Object sourceObj, Object destObj, String sourceFieldName, String destFieldName) {
			ClassFieldInfo info1 = registry.find(sourceObj.getClass());
			ClassFieldInfo info2 = registry.find(destObj.getClass());

			Value val1 = info1.getValueField(sourceObj, sourceFieldName);
			Value val2 = info2.getValueField(destObj, destFieldName);
			
			if (registry.options.logEachCopy) {
				String tmp = FieldCopyUtils.objToString(val1.getRawObject());
				logger.log("%s -> %s = %s", val1.name(), val2.name(), tmp);
			}
			copier.copy(val1, val2);
		}
		
		public void copyFields(Object sourceObj, Object destObj, List<String> sourceFields, List<String> destFields) {
			if (sourceObj == null) {
				String error = String.format("copyFields. NULL passed to sourceObj");
				throw new FieldCopyException(error);
			}
			if (destObj == null) {
				String error = String.format("copyFields. NULL passed to destObj.");
				throw new FieldCopyException(error);
			}
			registry.prepareObj(sourceObj);
			registry.prepareObj(destObj);

			ClassFieldInfo info1 = registry.find(sourceObj.getClass());
			ClassFieldInfo info2 = registry.find(destObj.getClass());
			
			if (sourceFields.size() != destFields.size()) {
				String error = String.format("copyFields. sourceFields and destFields sizes don't match: %d %d", sourceFields.size(), destFields.size());
				throw new FieldCopyException(error);
			}
			
			for(int i = 0; i < sourceFields.size(); i++) {
				String sourceFieldName = sourceFields.get(i);
				String destFieldName = destFields.get(i);
				Value val1 = info1.getValueField(sourceObj, sourceFieldName);
				Value val2 = info2.getValueField(destObj, destFieldName);
					
				if (registry.options.logEachCopy) {
					String tmp = FieldCopyUtils.objToString(val1.getRawObject());
					logger.log("%s -> %s = %s", val1.name(), val2.name(), tmp);
				}
				copier.copy(val1, val2);
			}
		}
		
		public void dumpFields(Object sourceObj) {
			registry.prepareObj(sourceObj);
			ClassFieldInfo info1 = registry.find(sourceObj.getClass());
			for(String fieldName: info1.fieldMap.keySet()) {
				Value val1 = info1.getValueField(sourceObj, fieldName);
				String tmp = FieldCopyUtils.objToString(val1.getRawObject());
				logger.log("%s = %s", val1.name(), tmp);
			}
		}
		
		public Map<String,Object> convertToMap(Object sourceObj) {
			registry.prepareObj(sourceObj);
			Map<String,Object> map = new HashMap<>();
			ClassFieldInfo info1 = registry.find(sourceObj.getClass());
			for(String fieldName: info1.fieldMap.keySet()) {
				Value val1 = info1.getValueField(sourceObj, fieldName);
				map.put(fieldName, val1.getRawObject());
			}
			return map;
		}
	}
	
	//fluent api
	public static class FCB2 {
		private FieldCopyBuilder root;
		private List<String> srcList = new ArrayList<>();
		private List<String> destList = new ArrayList<>();

		public FCB2(FieldCopyBuilder fieldCopierBuilder, String srcField, String destField) {
			this.root = fieldCopierBuilder;
			srcList.add(srcField);
			destList.add(destField);
		}
		
		public FCB2 copyField(String srcFieldName, String destFieldName) {
			srcList.add(srcFieldName);
			destList.add(destFieldName);
			return this;
		}
		public FCB2 copyField(Value srcField, Value destField) {
			root.registry.prepareObj(root.sourceObj);
			root.registry.prepareObj(root.destObj);
			return copyField(srcField.name(), destField.name());
		}
		
		public void execute() {
			FieldCopier fieldCopier = root.createFieldCopier();
			fieldCopier.copyFields(root.sourceObj, root.destObj, srcList, destList);
		}
	}
	public static class FCB1 {

		private FieldCopyBuilder root;
		private List<String> excludeList = new ArrayList<>();
		private boolean doAutoCopy;

		public FCB1(FieldCopyBuilder fieldCopierBuilder) {
			this.root = fieldCopierBuilder;
		}
		
		public FCB1 exclude(String...fieldNames) {
			this.excludeList = Arrays.asList(fieldNames);
			return this;
		}
		public FCB1 exclude(Value...fields) {
			root.registry.prepareObj(root.sourceObj);
			root.registry.prepareObj(root.destObj);

			List<String> list = new ArrayList<>();
			for(Value val: fields) {
				list.add(val.name());
			}
			this.excludeList = list;
			return this;
		}
		
//		x.copy(s,t).autoCopy().execute();
//		x.copy(s,t).include(...).execute();
		
		public FCB1 autoCopy() {
			this.doAutoCopy = true;
			return this;
		}
		
		public void execute() {
			if (this.doAutoCopy) {
				root.registry.prepareObj(root.sourceObj);
				root.registry.prepareObj(root.destObj);
				ClassFieldInfo info1 = root.registry.find(root.sourceObj.getClass());
				ClassFieldInfo info2 = root.registry.find(root.destObj.getClass());
				
				List<String> fieldsToCopy = new ArrayList<>();
				for(String fieldName: info1.fieldMap.keySet()) {
					if (excludeList.contains(fieldName)) {
						continue;
					}
					
					//TODO perhaps case-insensitive compare
					if (info2.fieldMap.containsKey(fieldName)) {
						fieldsToCopy.add(fieldName);
					}
				}
				
				FieldCopier fieldCopier = root.createFieldCopier();
				fieldCopier.copyFields(root.sourceObj, root.destObj, fieldsToCopy, fieldsToCopy);
			}
		}
		
		public FCB2 copyField(String srcFieldName, String destFieldName) {
			return new FCB2(root, srcFieldName, destFieldName);
		}
		public FCB2 copyField(Value srcField, Value destField) {
			root.registry.prepareObj(root.sourceObj);
			root.registry.prepareObj(root.destObj);
			return new FCB2(root, srcField.name(), destField.name());
		}
	}


	public static class FieldCopyBuilder {
		private static FieldCopyBuilder singleton;
		
		private SimpleLogger logger;
		private FieldRegistry registry;
		private ValueCopier copier;
		Object sourceObj;
		Object destObj;

		public FieldCopyBuilder(FieldRegistry registry, ValueCopier copier, SimpleLogger logger) {
			this.logger = logger;
			this.registry = registry;
			this.copier = copier;
		}
		
		//TODO: make thread-safe
		public static FieldCopyBuilder builder() {
			if (singleton == null) {
				SimpleLogger logger = new SimpleConsoleLogger();
				FieldRegistry reg = new FieldRegistry(logger);
				ValueCopier copier = new ValueCopier(logger, reg, new DefaultValueFactory());
				singleton = new FieldCopyBuilder(reg, copier, logger);
			}
			return singleton;
		}
		
		public FCB1 copy(Object sourceObj, Object destObj) {
			this.sourceObj = sourceObj;
			this.destObj = destObj;
			return new FCB1(this);
		}
		
		FieldCopier createFieldCopier() {
			registry.options.logEachCopy = true;
			//ValueCopier copier = new ValueCopier(logger, registry);
			FieldCopier fieldCopier = new FieldCopier(registry, copier, registry.logger);
			return fieldCopier;
		}
	}	
	
	public static class Truck {
		public final IntegerValue WIDTH = new IntegerValue();
		public final IntegerValue HEIGHT = new IntegerValue();

		public Integer getWIDTH() {
			return WIDTH.get();
		}
		public Integer getHEIGHT() {
			return HEIGHT.get();
		}
	}
	
	public static enum Enum1 {
		ALPHA,
		BETA,
		GAMMA,
		DELTA
	}
	public static enum Enum2 {
		ALPHA,
		BETA,
		GAMMA
	}
	public static class SampleClass1 {
		public final IntegerValue WIDTH = new IntegerValue();
		public final BooleanValue FLAG = new BooleanValue();
		public final StringValue TITLE = new StringValue();
		public final DoubleValue WEIGHT = new DoubleValue();
		public final LongValue SIZE = new LongValue();
		public final EnumValue<Enum1> RANK = new EnumValue<>(Enum1.class);
		public final EnumValue<Enum2> RANK2 = new EnumValue<>(Enum2.class);
		public final ListValue<StringValue> ROLES = new ListValue<>(StringValue.class);
		public final ListValue<IntegerValue> PERMS = new ListValue<>(IntegerValue.class);
		
		public final StringListValue SROLES = new StringListValue();
		public final EnumListValue<Enum1> EROLES = new EnumListValue<>(Enum1.class);
		
		public final StructValue<Person> PERSON = new StructValue<>(Person.class);
		public final StructValue<Customer> CUSTOMER = new StructValue<>(Customer.class);
		
		public final YListValue<Integer> XIROLES = new YListValue(Integer.class);
		public final YListValue<String> XSROLES = new YListValue(String.class);
	}
	
	public static class Person {
		public final StringValue FIRSTNAME = new StringValue();
		public final StringValue LASTNAME = new StringValue();
	}
	public static class Customer {
		public final StringValue FIRSTNAME = new StringValue();
		public final StringValue LASTNAME = new StringValue();
	}
	
	public static class FieldCopy {
		public static void initFields(Object obj) {
			for(Field field: obj.getClass().getFields()) {
				log(field.getName());
				Class<?> clazzField = field.getType();
				if (Value.class.isAssignableFrom(clazzField)) {
					Object fieldValue = getFieldValue(field, obj);
					
					if (fieldValue == null) {
						fieldValue = createInstance(field, clazzField);
						setFieldValue(field, obj, fieldValue);
					}
					
					Type typ = field.getGenericType();
					if (typ != null) {
						if (typ instanceof ParameterizedType) {
							ParameterizedType paramType = (ParameterizedType) typ;
							Type[] argTypes = paramType.getActualTypeArguments();
							log(">>" + argTypes[0].getTypeName());
						} else {
							log(typ.getTypeName());
						}
					}
				}
			}
		}
		private static Object getFieldValue(Field field, Object obj) {
			Object fieldValue = null;
			try {
				fieldValue = field.get(obj);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			return fieldValue;
		}
		private static void setFieldValue(Field field, Object obj, Object fieldValue) {
			try {
				field.set(obj, fieldValue);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		private static Object createInstance(Field field, Class<?> clazzField) {
			if (clazzField.equals(EnumValue.class)) {
				Class<?>[] args = { Class.class };				
				Constructor<?> cons = getConstructor(clazzField, args); 
				
				Object[] params = new Object[1];
				params[0] = getGenericArg(field, clazzField);
				
				Object obj = null;
				try {
					obj = cons.newInstance(params);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
				return obj;
			} else {
				return doCreateInstance(clazzField);
			}
		}
		
		private static Class<?> getGenericArg(Field field, Class<?> clazzField) {
			Type typ = field.getGenericType();
			if (typ != null) {
				if (typ instanceof ParameterizedType) {
					ParameterizedType paramType = (ParameterizedType) typ;
					Type[] argTypes = paramType.getActualTypeArguments();
					log(">>" + argTypes[0].getTypeName());
					Type arg = argTypes[0];
					if (arg instanceof Class) {
						return (Class<?>) arg;
					}
				}
			}
			return null;
			
		}
		private static Constructor<?> getConstructor(Class<?> clazzField, Class[] args) {
			Constructor<?> cons = null;
			try {
				cons = clazzField .getConstructor(args);
			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return cons;
		}
		private static Object doCreateInstance(Class<?> clazzField) {
			Object obj = null;
			//clazzField must have a default constructor
			try {
				obj = clazzField.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				String error = String.format("createObj failed: %s", e.getMessage());
				throw new FieldCopyException(error);
			}
			
			return obj;
		}
		private static void log(String s) {
			System.out.println(s);
		}
	}
	public static class Person2 {
		public StringValue FIRSTNAME;
		public StringValue LASTNAME;
		public EnumValue<Enum1> RANK;
		
		public Person2() {
			FieldCopy.initFields(this);
		}
	}

	@Test
	public void test() throws Exception {
		IntegerValue val = new IntegerValue();
		val.setNameInternal("WIDTH");
		val.set(44);
		int n = val.get();
		assertEquals(44, n);
	}

	@Test
	public void test3() throws Exception {
		Truck truck = new Truck();
		truck.WIDTH.set(10);
		truck.HEIGHT.set(20);
		FieldRegistry reg = initReg(truck);

		log("use...");
		ClassFieldInfo info = reg.find(truck.getClass());
		Value vv = info.getValueField(truck, "WIDTH");
		assertEquals("WIDTH", vv.name());
		Object raw = vv.getRawObject();
		log("raw:" + raw.toString());
	}

	@Test(expected=FieldCopyException.class)
	public void testFail() throws Exception {
		Truck truck = new Truck();
		FieldRegistry reg = initReg(truck);

		log("use...");
		ClassFieldInfo info = reg.find(truck.getClass());
		Integer other = 10;
		Value vv = info.getValueField(other, "WIDTH");
	}
	@Test(expected=FieldCopyException.class)
	public void testFailNull() throws Exception {
		Truck truck = new Truck();
		FieldRegistry reg = initReg(truck);

		log("use...");
		ClassFieldInfo info = reg.find(truck.getClass());
		Value vv = info.getValueField(null, "WIDTH");
	}

	@Test
	public void testFieldCopy() throws Exception {
		IntegerValue val = new IntegerValue();
		val.setNameInternal("WIDTH");
		val.set(44);

		IntegerValue val2 = new IntegerValue();
		val2.setNameInternal("WIDTH");

		SimpleLogger logger = new SimpleConsoleLogger();
		ValueCopier copier = new ValueCopier(logger, null, new DefaultValueFactory());
		copier.copy(val, val2);

		int n = val2.get();
		assertEquals(44, n);
	}

	@Test(expected=FieldCopyException.class)
	public void testFieldCopyFail() throws Exception {
		IntegerValue val = new IntegerValue();
		val.setNameInternal("WIDTH");
		val.set(44);
		BooleanValue val2 = new BooleanValue();
		val2.setNameInternal("WIDTH");

		SimpleLogger logger = new SimpleConsoleLogger();
		ValueCopier copier = new ValueCopier(logger, null, new DefaultValueFactory());
		copier.copy(val, val2);
	}

	@Test
	public void testCopy1() throws Exception {
		Truck truck = new Truck();
		truck.WIDTH.set(10);
		truck.HEIGHT.set(20);
		FieldRegistry reg = initReg(truck);
		Truck truck2 = new Truck();

		FieldCopier fieldCopier = createFieldCopier(reg);
		fieldCopier.copyField(truck, truck2, "WIDTH", "WIDTH");
		assertEquals(10, truck2.WIDTH.get().intValue());
		assertEquals(null, truck2.HEIGHT.get());

		truck.WIDTH.set(400);
		fieldCopier.copyField(truck, truck2, "WIDTH", "HEIGHT");
		assertEquals(400, truck2.HEIGHT.get().intValue());
	}

	@Test
	public void testCopy2() throws Exception {
		Truck truck = new Truck();
		truck.WIDTH.set(10);
		truck.HEIGHT.set(20);
		FieldRegistry reg = initReg(truck);
		Truck truck2 = new Truck();

		FieldCopier fieldCopier = createFieldCopier(reg);
		fieldCopier.copyField(truck, truck2, truck.WIDTH, truck2.WIDTH);
		assertEquals(10, truck2.WIDTH.get().intValue());
		assertEquals(null, truck2.HEIGHT.get());

		truck.WIDTH.set(400);
		fieldCopier.copyField(truck, truck2, truck.WIDTH, truck2.HEIGHT);
		assertEquals(400, truck2.HEIGHT.get().intValue());
		
		fieldCopier.dumpFields(truck2);
		
		Map<String,Object> map = fieldCopier.convertToMap(truck2);
		assertEquals(2, map.size());
		for(String fieldName: map.keySet()) {
			Object val = map.get(fieldName);
			String tmp = FieldCopyUtils.objToString(val);
			log(String.format("map: %s->%s", fieldName, tmp));
		}
	}
	
	@Test
	public void testCopy3() throws Exception {
		Truck truck = new Truck();
		truck.WIDTH.set(10);
		truck.HEIGHT.set(20);
		FieldRegistry reg = initReg(truck);
		Truck truck2 = new Truck();
		FieldCopier fieldCopier = createFieldCopier(reg); 

		List<String> fields = Arrays.asList("WIDTH", "HEIGHT");
		fieldCopier.copyFields(truck, truck2, fields, fields);
		assertEquals(10, truck2.WIDTH.getInt());
		assertEquals(20, truck2.HEIGHT.getInt());
	}
	
	@Test
	public void testDumpMap() throws Exception {
		Truck truck = new Truck();
		truck.WIDTH.set(10);
		truck.HEIGHT.set(20);
		FieldRegistry reg = initReg(truck);
		FieldCopier fieldCopier = createFieldCopier(reg);
		fieldCopier.dumpFields(truck);

		Map<String,Object> map = fieldCopier.convertToMap(truck);
		assertEquals(2, map.size());
		for(String fieldName: map.keySet()) {
			Object val = map.get(fieldName);
			String tmp = (val == null) ? "(null)" : val.toString();
			log(String.format("map: %s->%s", fieldName, tmp));
		}
	}
	
	@Test
	public void testDumpEmptyMap() throws Exception {
		Truck truck = new Truck();
		FieldRegistry reg = initReg(truck);
		FieldCopier fieldCopier = createFieldCopier(reg);
		
		fieldCopier.dumpFields(truck);

		Map<String,Object> map = fieldCopier.convertToMap(truck);
		assertEquals(2, map.size());
		for(String fieldName: map.keySet()) {
			Object val = map.get(fieldName);
			String tmp = (val == null) ? "(null)" : val.toString();
			log(String.format("map: %s->%s", fieldName, tmp));
		}
	}
	
	@Test
	public void testCopyBuilder1() throws Exception {
		Truck truck = new Truck();
		truck.WIDTH.set(10);
		truck.HEIGHT.set(20);
		FieldRegistry reg = initReg(truck);
		Truck truck2 = new Truck();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(truck, truck2).autoCopy().execute();
		assertEquals(10, truck2.WIDTH.getInt());
		assertEquals(20, truck2.HEIGHT.getInt());
	}
	
	@Test
	public void testCopyBuilder2() throws Exception {
		Truck truck = new Truck();
		truck.WIDTH.set(10);
		truck.HEIGHT.set(20);
		FieldRegistry reg = initReg(truck);
		Truck truck2 = new Truck();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(truck, truck2).exclude("WIDTH").autoCopy().execute();
		assertEquals(true, truck2.WIDTH.isNull());
		assertEquals(20, truck2.HEIGHT.getInt());
	}
	@Test
	public void testCopyBuilder2a() throws Exception {
		Truck truck = new Truck();
		truck.WIDTH.set(10);
		truck.HEIGHT.set(20);
		FieldRegistry reg = initReg(truck);
		Truck truck2 = new Truck();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(truck, truck2).exclude(truck.WIDTH).autoCopy().execute();
		assertEquals(true, truck2.WIDTH.isNull());
		assertEquals(20, truck2.HEIGHT.getInt());
	}
	@Test
	public void testCopyBuilderNone() throws Exception {
		Truck truck = new Truck();
		truck.WIDTH.set(10);
		truck.HEIGHT.set(20);
		FieldRegistry reg = initReg(truck);
		Truck truck2 = new Truck();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(truck, truck2).exclude("WIDTH", "HEIGHT").autoCopy().execute();
		assertEquals(true, truck2.WIDTH.isNull());
		assertEquals(true, truck2.HEIGHT.isNull());
	}
	
	@Test
	public void testCopyBuilder4() throws Exception {
		Truck truck = new Truck();
		truck.WIDTH.set(10);
		truck.HEIGHT.set(20);
		FieldRegistry reg = initReg(truck);
		Truck truck2 = new Truck();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(truck, truck2).copyField("WIDTH", "WIDTH").execute();
		assertEquals(10, truck2.WIDTH.getInt());
		assertEquals(true, truck2.HEIGHT.isNull());
	}
	@Test
	public void testCopyBuilder4a() throws Exception {
		Truck truck = new Truck();
		truck.WIDTH.set(10);
		truck.HEIGHT.set(20);
		FieldRegistry reg = initReg(truck);
		Truck truck2 = new Truck();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(truck, truck2)
		.copyField(truck.WIDTH, truck2.WIDTH)
		.copyField(truck.HEIGHT, truck2.HEIGHT)
		.execute();
		assertEquals(10, truck2.WIDTH.getInt());
		assertEquals(20, truck2.HEIGHT.getInt());
	}
	
	@Test
	public void testCopyBuilder5() throws Exception {
		Truck truck = new Truck();
		truck.WIDTH.set(10);
		truck.HEIGHT.set(20);
		Truck truck2 = new Truck();

		FieldCopyBuilder.builder().copy(truck, truck2).exclude(truck.WIDTH).autoCopy().execute();
		assertEquals(true, truck2.WIDTH.isNull());
		assertEquals(20, truck2.HEIGHT.getInt());
	}
	
	@Test(expected=FieldCopyException.class)
	public void testObjCopier1() throws Exception {
		SampleClass1 obj = new SampleClass1();
		obj.FLAG.set(true);
		obj.WIDTH.set(10);

		FieldRegistry reg = initReg(obj);
		SampleClass1 obj2 = new SampleClass1();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(obj, obj2).copyField(obj.WIDTH, obj.FLAG).execute();
	}
	
	@Test
	public void testObjCopier2() throws Exception {
		SampleClass1 obj = new SampleClass1();
		obj.WIDTH.set(10);

		FieldRegistry reg = initReg(obj);
		SampleClass1 obj2 = new SampleClass1();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(obj, obj2).copyField(obj.WIDTH, obj2.TITLE).execute();
		assertEquals("10", obj2.TITLE.get());
	}
	@Test
	public void testObjCopier3() throws Exception {
		SampleClass1 obj = new SampleClass1();
		obj.TITLE.set("122");

		FieldRegistry reg = initReg(obj);
		SampleClass1 obj2 = new SampleClass1();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(obj, obj2).copyField(obj.TITLE, obj2.WIDTH).execute();
		assertEquals(122, obj2.WIDTH.getInt());
		
		obj.SIZE.set(45890L);
		builder.copy(obj, obj2).copyField(obj.SIZE, obj2.WIDTH).execute();
		assertEquals(45890, obj2.WIDTH.getInt());
		
		obj.WEIGHT.set(12.45);
		builder.copy(obj, obj2).copyField(obj.WEIGHT, obj2.TITLE).execute();
		assertEquals("12.45", obj2.TITLE.get());
	}
	@Test
	public void testObjCopier4() throws Exception {
		SampleClass1 obj = new SampleClass1();
		obj.RANK.set(Enum1.ALPHA);
		obj.RANK2.set(Enum2.BETA);

		Enum1 e1 = obj.RANK.get();
		
		FieldRegistry reg = initReg(obj);
		SampleClass1 obj2 = new SampleClass1();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(obj, obj2).copyField(obj.RANK, obj2.RANK).execute();
		assertEquals(Enum1.ALPHA, obj2.RANK.get());
		
		builder.copy(obj, obj2).copyField(obj.RANK, obj2.RANK2).execute();
		assertEquals(Enum2.ALPHA, obj2.RANK2.get());
	}
	@Test(expected=FieldCopyException.class)
	public void testObjCopier4EnumFail() throws Exception {
		SampleClass1 obj = new SampleClass1();
		obj.RANK.set(Enum1.DELTA);
		obj.RANK2.set(Enum2.BETA);

		FieldRegistry reg = initReg(obj);
		SampleClass1 obj2 = new SampleClass1();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(obj, obj2).copyField(obj.RANK, obj2.RANK2).execute();
		assertEquals(Enum1.ALPHA, obj2.RANK2.get());
	}
	
	@Test
	public void testObjCopierList1() throws Exception {
		SampleClass1 obj = new SampleClass1();
		List<String> strlist = Arrays.asList("abc", "def");
		List<StringValue> list = new ArrayList<>();
		for(String s: strlist) {
			StringValue sv = new StringValue();
			sv.set(s);
			list.add(sv);
		}
		obj.ROLES.set(list);

		FieldRegistry reg = initReg(obj);
		SampleClass1 obj2 = new SampleClass1();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(obj, obj2).copyField(obj.ROLES, obj2.ROLES).execute();
		
		List<StringValue> outlist = obj2.ROLES.get();
		assertEquals(2, outlist.size());
		assertEquals("abc", outlist.get(0).getRawObject().toString());
	}
	
	@Test
	public void testObjCopierList2() throws Exception {
		SampleClass1 obj = new SampleClass1();
		List<Integer> strlist = Arrays.asList(33, 44);
		List<IntegerValue> list = new ArrayList<>();
		for(Integer n: strlist) {
			IntegerValue sv = new IntegerValue();
			sv.set(n);
			list.add(sv);
		}
		obj.PERMS.set(list);

		FieldRegistry reg = initReg(obj);
		SampleClass1 obj2 = new SampleClass1();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(obj, obj2).copyField(obj.PERMS, obj2.ROLES).execute();
		
		List<StringValue> outlist = obj2.ROLES.get();
		assertEquals(2, outlist.size());
		StringValue vv = (StringValue) outlist.get(0);
		assertEquals("33", vv.get());
	}
	
	@Test
	public void testObjCopierList3() throws Exception {
		SampleClass1 obj = new SampleClass1();
		List<String> strlist = Arrays.asList("abc", "def");
		obj.SROLES.setList(strlist);

		FieldRegistry reg = initReg(obj);
		SampleClass1 obj2 = new SampleClass1();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(obj, obj2).copyField(obj.SROLES, obj2.SROLES).execute();
		
		List<String> outlist = obj2.SROLES.getList();
		assertEquals(2, outlist.size());
		assertEquals("abc", outlist.get(0));
	}
	@Test
	public void testObjCopierList3a() throws Exception {
		SampleClass1 obj = new SampleClass1();
		obj.XSROLES.add("abc");
		obj.XSROLES.add("def");

		FieldRegistry reg = initReg(obj);
		SampleClass1 obj2 = new SampleClass1();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(obj, obj2).copyField(obj.XSROLES, obj2.XSROLES).execute();

		List<Value> outlist = obj2.XSROLES.getValueList();
		assertEquals(2, outlist.size());
		assertEquals("abc", outlist.get(0).getRawObject());
	}
	@Test
	public void testObjCopierList3b() throws Exception {
		SampleClass1 obj = new SampleClass1();
		obj.XSROLES.add("123");
		obj.XSROLES.add("456");

		FieldRegistry reg = initReg(obj);
		SampleClass1 obj2 = new SampleClass1();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(obj, obj2).copyField(obj.XSROLES, obj2.XIROLES).execute();

		List<Value> outlist = obj2.XIROLES.getValueList();
		assertEquals(2, outlist.size());
		IntegerValue v = (IntegerValue) outlist.get(0);
		assertEquals(123, v.getInt());
		v = (IntegerValue) outlist.get(1);
		assertEquals(456, v.getInt());
		assertEquals(123, obj2.XIROLES.getList().get(0).intValue());
	}
	
	
	@Test
	public void testObjCopierList4() throws Exception {
		SampleClass1 obj = new SampleClass1();
		List<Enum1> strlist = Arrays.asList(Enum1.ALPHA, Enum1.BETA);
		obj.EROLES.setList(strlist);

		FieldRegistry reg = initReg(obj);
		SampleClass1 obj2 = new SampleClass1();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(obj, obj2).copyField(obj.EROLES, obj2.EROLES).execute();
		
		List<Enum1> outlist = obj2.EROLES.getList();
		assertEquals(2, outlist.size());
		assertEquals(Enum1.ALPHA, outlist.get(0));
	}
	
	@Test
	public void testObjCopierList5() throws Exception {
		SampleClass1 obj = new SampleClass1();
		List<Enum1> strlist = Arrays.asList(Enum1.ALPHA, Enum1.BETA);
		obj.EROLES.setList(strlist);

		FieldRegistry reg = initReg(obj);
		SampleClass1 obj2 = new SampleClass1();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(obj, obj2).copyField(obj.EROLES, obj2.ROLES).execute();
		
		List<StringValue> outlist = obj2.ROLES.get();
		assertEquals(2, outlist.size());
		StringValue val = outlist.get(0);
		assertEquals("ALPHA", val.get());
	}
	@Test
	public void testObjCopierList5a() throws Exception {
		SampleClass1 obj = new SampleClass1();
		List<String> strlist = Arrays.asList("ALPHA", "BETA");
		obj.SROLES.setList(strlist);

		FieldRegistry reg = initReg(obj);
		SampleClass1 obj2 = new SampleClass1();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(obj, obj2).copyField(obj.SROLES, obj2.EROLES).execute();
		
		List<EnumValue> outlist = obj2.EROLES.get();
		assertEquals(2, outlist.size());
		EnumValue<Enum1> val = outlist.get(0);
		assertEquals(Enum1.ALPHA, val.get());
	}
	@Test
	public void testObjCopierStruct6() throws Exception {
		SampleClass1 obj = new SampleClass1();
		Person person = new Person();
		person.FIRSTNAME.set("bob");
		person.LASTNAME.set("smith");
		obj.PERSON.set(person);

		FieldRegistry reg = initReg(obj);
		SampleClass1 obj2 = new SampleClass1();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(obj, obj2).copyField(obj.PERSON, obj2.PERSON).execute();
		
		Person p2 = obj2.PERSON.get();
		assertEquals("bob", p2.FIRSTNAME.get());
		assertEquals("smith", p2.LASTNAME.get());
	}
	@Test
	public void testObjCopierStruct6a() throws Exception {
		SampleClass1 obj = new SampleClass1();
		Person person = new Person();
		person.FIRSTNAME.set("bob");
		person.LASTNAME.set("smith");
		obj.PERSON.set(person);

		FieldRegistry reg = initReg(obj);
		SampleClass1 obj2 = new SampleClass1();

		FieldCopyBuilder builder = createFieldCopierBuilder(reg); 
		builder.copy(obj, obj2).copyField(obj.PERSON, obj2.CUSTOMER).execute();
		
		Customer p2 = obj2.CUSTOMER.get();
		assertEquals("bob", p2.FIRSTNAME.get());
		assertEquals("smith", p2.LASTNAME.get());
	}
	
	@Test
	public void testAutoCreateValues() throws Exception {
		SampleClass1 obj = new SampleClass1();
		for(Field field: obj.getClass().getFields()) {
			log(field.getName());
			Type typ = field.getGenericType();
			if (typ != null) {
				if (typ instanceof ParameterizedType) {
					ParameterizedType paramType = (ParameterizedType) typ;
					Type[] argTypes = paramType.getActualTypeArguments();
					log(">>" + argTypes[0].getTypeName());
				} else {
					log(typ.getTypeName());
				}
			}
			//https://stackoverflow.com/questions/38761897/convert-java-lang-reflect-type-to-classt-clazz
			
		}
	}
	
	@Test
	public void testAutoInit() throws Exception {
		Person2 p = new Person2();
		p.FIRSTNAME.set("a");
		p.LASTNAME.set("bb");
		p.RANK.set(Enum1.BETA);
		
		assertEquals("a", p.FIRSTNAME.get());
	}

	//--
	private void log(String s) {
		System.out.println(s);
	}

	private FieldRegistry initReg(Object obj) {
		SimpleLogger logger = new SimpleConsoleLogger();
		FieldRegistry reg = new FieldRegistry(logger);
		reg.registerIfNeeded(obj);
		return reg;
	}
	private FieldCopier createFieldCopier(FieldRegistry reg) {
		reg.options.logEachCopy = true;
		ValueCopier copier = new ValueCopier(reg.logger, reg);
		FieldCopier fieldCopier = new FieldCopier(reg, copier, reg.logger);
		return fieldCopier;
	}
	private FieldCopyBuilder createFieldCopierBuilder(FieldRegistry reg) {
		reg.options.logEachCopy = true;
//		ValueCopier copier = new ValueCopier(reg.logger, reg, new ValueFactory());
		ValueCopier copier = new ValueCopier(reg.logger, reg);
		FieldCopyBuilder builder = new FieldCopyBuilder(reg, copier, reg.logger);
		return builder;
	}
}
