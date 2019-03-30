package org.dnal.fieldcopy.planner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.dnal.fieldcopy.converter.ArrayElementConverter;
import org.dnal.fieldcopy.converter.FieldInfo;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.log.SimpleLogger;
import org.dnal.fieldcopy.planner.PlannerTests.ZFieldPlan;
import org.dnal.fieldcopy.service.beanutils.BeanUtilsBeanDetectorService;
import org.dnal.fieldcopy.service.beanutils.BeanUtilsFieldDescriptor;
import org.dnal.fieldcopy.service.beanutils.ListSpec;
import org.dnal.fieldcopy.service.beanutils.ReflectionUtil;

public class ZConverterService {
	private SimpleLogger logger;
	private ZListElementConverterFactory converterFactory;
	private List<ValueConverter> builtInConverterL = new ArrayList<>();
	private FieldCopyService outerSvc;
	
	public ZConverterService(SimpleLogger logger, BeanUtilsBeanDetectorService beanDetectorSvc, FieldCopyService outerSvc) {
		this.logger = logger;
		this.converterFactory = new ZListElementConverterFactory(outerSvc);
		this.converterFactory.setBeanDetectorSvc(beanDetectorSvc);
		this.outerSvc = outerSvc;
	}

	// List -> List
	public ValueConverter addListConverterIfNeeded(ZFieldPlan fieldPlan, FieldPair pair, ZClassPlan classPlan, Class<?> destClass) {
		BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp;
		BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) pair.destProp;
		
		Class<?> srcFieldClass = fd1.pd.getPropertyType();
		Class<?> destFieldClass = fd2.pd.getPropertyType();
		if (Collection.class.isAssignableFrom(srcFieldClass) && 
				Collection.class.isAssignableFrom(destFieldClass)) {
			
			ListSpec listSpec1 = ReflectionUtil.buildListSpec(classPlan.srcClass, fd1);
			ListSpec listSpec2 = ReflectionUtil.buildListSpec(destClass, fd2);
			
			if (listSpec1.depth != listSpec2.depth) {
				throwDepthError("list", fd1, listSpec1, fd2, listSpec2);
			}

			Class<?> srcElementClass = listSpec1.elementClass;
			Class<?> destElementClass = listSpec2.elementClass;
			ValueConverter conv = matchingConverterAlreadyExists(classPlan, pair, srcElementClass, destElementClass);
			if (conv != null) {
				return conv;
			}
			
			//add one
			String name = pair.srcProp.getName();
			ZListElementConverter converter = converterFactory.createListConverter(classPlan.srcClass, name, srcElementClass, destElementClass);
			if (converter == null) {
				String error = String.format("Copying list<%s> to list<%s> is not supported.", srcElementClass.getName(), destElementClass.getName());
				throw new FieldCopyException(error);
			}
			converter.setDepth(listSpec1.depth);
			classPlan.converterL.add(converter);
			return converter;
		}
		return null;
	}
	
	// Array -> List
	public ValueConverter addArrayListConverterIfNeeded(ZFieldPlan fieldPlan, FieldPair pair, ZClassPlan classPlan, Class<?> destClass) {
		BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp;
		BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) pair.destProp;
		
		Class<?> srcFieldClass = fd1.pd.getPropertyType();
		Class<?> destFieldClass = fd2.pd.getPropertyType();
		if (srcFieldClass.isArray() && Collection.class.isAssignableFrom(destFieldClass)) {
			
			ListSpec listSpec1 = ReflectionUtil.buildArraySpec(classPlan.srcClass, fd1);
			ListSpec listSpec2 = ReflectionUtil.buildListSpec(destClass, fd2);
			
			if (listSpec1.depth != listSpec2.depth) {
				throwDepthError("list", fd1, listSpec1, fd2, listSpec2);
			}

			Class<?> srcElementClass = listSpec1.elementClass;
			Class<?> destElementClass = listSpec2.elementClass;
			ValueConverter conv = matchingConverterAlreadyExists(classPlan, pair, srcElementClass, destElementClass);
			if (conv != null) {
				return conv;
			}
			
			//add one
			String name = pair.srcProp.getName();
			ZListElementConverter converter = converterFactory.createListConverter(classPlan.srcClass, name, srcElementClass, destElementClass);
			if (converter == null) {
				String error = String.format("Copying list<%s> to list<%s> is not supported.", srcElementClass.getName(), destElementClass.getName());
				throw new FieldCopyException(error);
			}
			converter.setDepth(listSpec1.depth);
			converter.setSourceIsArray(true);
			classPlan.converterL.add(converter);
			return converter;
		}
		return null;
	}
	
	// Array -> Array
	public ValueConverter addArrayConverterIfNeeded(ZFieldPlan fieldPlan, FieldPair pair, ZClassPlan classPlan, Class<?> destClass) {
		BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp;
		BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) pair.destProp;
		
		Class<?> srcFieldClass = fd1.pd.getPropertyType();
		Class<?> destFieldClass = fd2.pd.getPropertyType();
		if (srcFieldClass.isArray() && destFieldClass.isArray()) { 
			
			ListSpec listSpec1 = ReflectionUtil.buildArraySpec(classPlan.srcClass, fd1);
			ListSpec listSpec2 = ReflectionUtil.buildArraySpec(destClass, fd2);
			
			if (listSpec1.depth != listSpec2.depth) {
				throwDepthError("array", fd1, listSpec1, fd2, listSpec2);
			}

			Class<?> srcElementClass = listSpec1.elementClass;
			Class<?> destElementClass = listSpec2.elementClass;
			ValueConverter conv = matchingConverterAlreadyExists(classPlan, pair, srcElementClass, destElementClass);
			if (conv != null) {
				return conv;
			}
			
			//add one
			String name = pair.srcProp.getName();
			ZArrayElementConverter converter = converterFactory.createArrayConverter(classPlan.srcClass, name, srcElementClass, destElementClass);
			if (converter == null) {
				String error = String.format("Copying array<%s> to array<%s> is not supported.", srcElementClass.getName(), destElementClass.getName());
				throw new FieldCopyException(error);
			}
			converter.setDepth(listSpec1.depth);
			classPlan.converterL.add(converter);
			return converter;
		}
		return null;
	}

	// List -> Array
	public ValueConverter addListArrayConverterIfNeeded(ZFieldPlan fieldPlan, FieldPair pair, ZClassPlan classPlan, Class<?> destClass) {
		BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp;
		BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) pair.destProp;
		
		Class<?> srcFieldClass = fd1.pd.getPropertyType();
		Class<?> destFieldClass = fd2.pd.getPropertyType();
		if (Collection.class.isAssignableFrom(srcFieldClass) && destFieldClass.isArray()) { 
			
			ListSpec listSpec1 = ReflectionUtil.buildListSpec(classPlan.srcClass, fd1);
			ListSpec listSpec2 = ReflectionUtil.buildArraySpec(destClass, fd2);
			
			if (listSpec1.depth != listSpec2.depth) {
				throwDepthError("array", fd1, listSpec1, fd2, listSpec2);
			}

			Class<?> srcElementClass = listSpec1.elementClass;
			Class<?> destElementClass = listSpec2.elementClass;
			ValueConverter conv = matchingConverterAlreadyExists(classPlan, pair, srcElementClass, destElementClass);
			if (conv != null) {
				return conv;
			}
			
			//add one
			String name = pair.srcProp.getName();
			ZArrayElementConverter converter = converterFactory.createArrayConverter(classPlan.srcClass, name, srcElementClass, destElementClass);
			if (converter == null) {
				String error = String.format("Copying array<%s> to array<%s> is not supported.", srcElementClass.getName(), destElementClass.getName());
				throw new FieldCopyException(error);
			}
			converter.setDepth(listSpec1.depth);
			converter.setSourceIsList(true);
			classPlan.converterL.add(converter);
			return converter;
		}
		return null;
	}
	
	private ValueConverter matchingConverterAlreadyExists(ZClassPlan classPlan, FieldPair pair, Class<?> srcElementClass, Class<?> destElementClass) {
		BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp;
		BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) pair.destProp;
	
		Class<?> srcFieldClass = fd1.pd.getPropertyType();
		Class<?> destFieldClass = fd2.pd.getPropertyType();
		
		boolean isArray = srcFieldClass.isArray();
		FieldInfo sourceField = buildSourceFieldInfo(pair, srcElementClass, classPlan, isArray);
		
		isArray = destFieldClass.isArray();
		FieldInfo destField = buildDestFieldInfo(pair, destElementClass, classPlan, isArray);
		
		for(ValueConverter converter: classPlan.converterL) {
			//a special use of converter. normally we pass field name and its class (and the dest class).
			//Here we are passing the fieldName (which is a list) and source and destination *element* classes
			if (converter.canConvert(sourceField, destField)) {
				//if already is a converter, nothing more to do
				return converter;
			}
		}

		//now try builtin converters
		for(ValueConverter converter: builtInConverterL) {
			//a special use of converter. normally we pass field name and its class (and the dest class).
			//Here we are passing the fieldName (which is a list) and source and destination *element* classes
			if (converter.canConvert(sourceField, destField)) {
				//if already is a converter, nothing more to do
				return converter;
			}
		}
		
		return null;
	}

	private void throwDepthError(String title, BeanUtilsFieldDescriptor fd1, ListSpec listSpec1, BeanUtilsFieldDescriptor fd2,
			ListSpec listSpec2) {
		String error = String.format("copyFields. field '%s' has %s depth %d, but field '%s' has different depth %d",
				fd1.getName(), title, listSpec1.depth, fd2.getName(), listSpec2.depth);
		throw new FieldCopyException(error);
	}

	private FieldInfo buildSourceFieldInfo(FieldPair pair, Class<?> srcElementClass,
			ZClassPlan classPlan, boolean isArray) {
		FieldInfo sourceField = new FieldInfo();
		sourceField.fieldName = pair.srcProp.getName();
		sourceField.fieldClass = srcElementClass;
		sourceField.beanClass = classPlan.srcClass;
		sourceField.isArray = isArray;
		return sourceField;
	}
	private FieldInfo buildDestFieldInfo(FieldPair pair, Class<?> destElementClass, ZClassPlan classPlan, boolean isArray) {
		FieldInfo destField = new FieldInfo();
		destField.fieldName = pair.destProp.getName();
		destField.fieldClass = destElementClass;
		destField.beanClass = classPlan.destClass;
		destField.isArray = isArray;
		return destField;
	}


	public ValueConverter findConverter(CopySpec copySpec, FieldPair pair, Object orig, List<ValueConverter> converterL) {
		if (CollectionUtils.isNotEmpty(converterL) || CollectionUtils.isNotEmpty(builtInConverterL)) {
			BeanUtilsFieldDescriptor desc = (BeanUtilsFieldDescriptor) pair.destProp;
			Class<?> destClass = desc.pd.getPropertyType();
			
			BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp;
			Class<?> srcClass = fd1.pd.getPropertyType();

			//TODO: can we make this faster with a map??
			FieldInfo sourceField = new FieldInfo();
			sourceField.fieldName = pair.srcProp.getName();
			sourceField.fieldClass = srcClass;
			sourceField.beanClass = copySpec.sourceObj.getClass();
			
			FieldInfo destField = new FieldInfo();
			destField.fieldName = pair.destProp.getName();
			destField.fieldClass = destClass;
			//NOTE. destObj is sometimes null. Document this TODO
			destField.beanClass = copySpec.destObj == null ? null : copySpec.destObj.getClass();

			if (CollectionUtils.isNotEmpty(converterL)) {
				for(ValueConverter converter: converterL) {
					//TODO: fix value null issue
					
					if (converter.canConvert(sourceField, destField)) {
						return converter;
					}
				}
			}
			
			//now try builtin converters
			for(ValueConverter converter: builtInConverterL) {
				//a special use of converter. normally we pass field name and its class (and the dest class).
				//Here we are passing the fieldName (which is a list) and source and destination *element* classes
				if (converter.canConvert(sourceField, destField)) {
					//if already is a converter, nothing more to do
					return converter;
				}
			}
		}
		return null;
	}

	public List<ValueConverter> getBuiltInConverterL() {
		return builtInConverterL;
	}
	
}