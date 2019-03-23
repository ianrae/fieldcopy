package org.dnal.fieldcopy.service.beanutils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.dnal.fieldcopy.converter.ArrayElementConverter;
import org.dnal.fieldcopy.converter.FieldInfo;
import org.dnal.fieldcopy.converter.ListElementConverter;
import org.dnal.fieldcopy.converter.ListElementConverterFactory;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.log.SimpleLogger;

public class ConverterService {
	private SimpleLogger logger;
	private ListElementConverterFactory converterFactory;
	
	public ConverterService(SimpleLogger logger, BeanUtilsBeanDetectorService beanDetectorSvc) {
		this.logger = logger;
		this.converterFactory = new ListElementConverterFactory();
		this.converterFactory.setBeanDetectorSvc(beanDetectorSvc);
	}

	// List -> List
	public void addListConverterIfNeeded(FieldPair pair, CopySpec copySpec, Class<?> destClass) {
		BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp;
		BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) pair.destProp;
		
		Class<?> srcFieldClass = fd1.pd.getPropertyType();
		Class<?> destFieldClass = fd2.pd.getPropertyType();
		if (Collection.class.isAssignableFrom(srcFieldClass) && 
				Collection.class.isAssignableFrom(destFieldClass)) {
			
			if (copySpec.converterL == null) {
				copySpec.converterL = new ArrayList<>();
			}
			
			ListSpec listSpec1 = ReflectionUtil.buildListSpec(copySpec.sourceObj.getClass(), fd1);
			ListSpec listSpec2 = ReflectionUtil.buildListSpec(destClass, fd2);
			
			if (listSpec1.depth != listSpec2.depth) {
				throwDepthError("list", fd1, listSpec1, fd2, listSpec2);
			}

			Class<?> srcElementClass = listSpec1.elementClass;
			Class<?> destElementClass = listSpec2.elementClass;
			if (matchingConverterAlreadyExists(copySpec, pair, srcElementClass, destElementClass)) {
				return;
			}
			
			//add one
			String name = pair.srcProp.getName();
			ListElementConverter converter = converterFactory.createListConverter(copySpec.sourceObj.getClass(), name, srcElementClass, destElementClass);
			if (converter == null) {
				String error = String.format("Copying list<%s> to list<%s> is not supported.", srcElementClass.getName(), destElementClass.getName());
				throw new FieldCopyException(error);
			}
			converter.setDepth(listSpec1.depth);
			copySpec.converterL.add(converter);
		}
	}
	
	// Array -> List
	public void addArrayListConverterIfNeeded(FieldPair pair, CopySpec copySpec, Class<?> destClass) {
		BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp;
		BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) pair.destProp;
		
		Class<?> srcFieldClass = fd1.pd.getPropertyType();
		Class<?> destFieldClass = fd2.pd.getPropertyType();
		if (srcFieldClass.isArray() && Collection.class.isAssignableFrom(destFieldClass)) {
			
			if (copySpec.converterL == null) {
				copySpec.converterL = new ArrayList<>();
			}
			
			ListSpec listSpec1 = ReflectionUtil.buildArraySpec(copySpec.sourceObj.getClass(), fd1);
			ListSpec listSpec2 = ReflectionUtil.buildListSpec(destClass, fd2);
			
			if (listSpec1.depth != listSpec2.depth) {
				throwDepthError("list", fd1, listSpec1, fd2, listSpec2);
			}

			Class<?> srcElementClass = listSpec1.elementClass;
			Class<?> destElementClass = listSpec2.elementClass;
			if (matchingConverterAlreadyExists(copySpec, pair, srcElementClass, destElementClass)) {
				return;
			}
			
			//add one
			String name = pair.srcProp.getName();
			ListElementConverter converter = converterFactory.createListConverter(copySpec.sourceObj.getClass(), name, srcElementClass, destElementClass);
			if (converter == null) {
				String error = String.format("Copying list<%s> to list<%s> is not supported.", srcElementClass.getName(), destElementClass.getName());
				throw new FieldCopyException(error);
			}
			converter.setDepth(listSpec1.depth);
			converter.setSourceIsArray(true);
			copySpec.converterL.add(converter);
		}
	}
	
	// Array -> Array
	public void addArrayConverterIfNeeded(FieldPair pair, CopySpec copySpec, Class<?> destClass) {
		BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp;
		BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) pair.destProp;
		
		Class<?> srcFieldClass = fd1.pd.getPropertyType();
		Class<?> destFieldClass = fd2.pd.getPropertyType();
		if (srcFieldClass.isArray() && destFieldClass.isArray()) { 
			
			if (copySpec.converterL == null) {
				copySpec.converterL = new ArrayList<>();
			}
			
			ListSpec listSpec1 = ReflectionUtil.buildArraySpec(copySpec.sourceObj.getClass(), fd1);
			ListSpec listSpec2 = ReflectionUtil.buildArraySpec(destClass, fd2);
			
			if (listSpec1.depth != listSpec2.depth) {
				throwDepthError("array", fd1, listSpec1, fd2, listSpec2);
			}

			Class<?> srcElementClass = listSpec1.elementClass;
			Class<?> destElementClass = listSpec2.elementClass;
			if (matchingConverterAlreadyExists(copySpec, pair, srcElementClass, destElementClass)) {
				return;
			}
			
			//add one
			String name = pair.srcProp.getName();
			ArrayElementConverter converter = converterFactory.createArrayConverter(copySpec.sourceObj.getClass(), name, srcElementClass, destElementClass);
			if (converter == null) {
				String error = String.format("Copying array<%s> to array<%s> is not supported.", srcElementClass.getName(), destElementClass.getName());
				throw new FieldCopyException(error);
			}
			converter.setDepth(listSpec1.depth);
			copySpec.converterL.add(converter);
		}
	}

	// List -> Array
	public void addListArrayConverterIfNeeded(FieldPair pair, CopySpec copySpec, Class<?> destClass) {
		BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp;
		BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) pair.destProp;
		
		Class<?> srcFieldClass = fd1.pd.getPropertyType();
		Class<?> destFieldClass = fd2.pd.getPropertyType();
		if (Collection.class.isAssignableFrom(srcFieldClass) && destFieldClass.isArray()) { 
			
			if (copySpec.converterL == null) {
				copySpec.converterL = new ArrayList<>();
			}
			
			ListSpec listSpec1 = ReflectionUtil.buildListSpec(copySpec.sourceObj.getClass(), fd1);
			ListSpec listSpec2 = ReflectionUtil.buildArraySpec(destClass, fd2);
			
			if (listSpec1.depth != listSpec2.depth) {
				throwDepthError("array", fd1, listSpec1, fd2, listSpec2);
			}

			Class<?> srcElementClass = listSpec1.elementClass;
			Class<?> destElementClass = listSpec2.elementClass;
			if (matchingConverterAlreadyExists(copySpec, pair, srcElementClass, destElementClass)) {
				return;
			}
			
			//add one
			String name = pair.srcProp.getName();
			ArrayElementConverter converter = converterFactory.createArrayConverter(copySpec.sourceObj.getClass(), name, srcElementClass, destElementClass);
			if (converter == null) {
				String error = String.format("Copying array<%s> to array<%s> is not supported.", srcElementClass.getName(), destElementClass.getName());
				throw new FieldCopyException(error);
			}
			converter.setDepth(listSpec1.depth);
			converter.setSourceIsList(true);
			copySpec.converterL.add(converter);
		}
	}
	
	private boolean matchingConverterAlreadyExists(CopySpec copySpec, FieldPair pair, Class<?> srcElementClass, Class<?> destElementClass) {
		BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp;
		BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) pair.destProp;
	
		Class<?> srcFieldClass = fd1.pd.getPropertyType();
		Class<?> destFieldClass = fd2.pd.getPropertyType();
		
		boolean isArray = srcFieldClass.isArray();
		FieldInfo sourceField = buildSourceFieldInfo(pair, srcElementClass, copySpec, isArray);
		
		isArray = destFieldClass.isArray();
		FieldInfo destField = buildDestFieldInfo(pair, destElementClass, copySpec, isArray);
		
		for(ValueConverter converter: copySpec.converterL) {
			//a special use of converter. normally we pass field name and its class (and the dest class).
			//Here we are passing the fieldName (which is a list) and source and destination *element* classes
			if (converter.canConvert(sourceField, destField)) {
				//if already is a converter, nothing more to do
				return true;
			}
		}
		return false;
	}

	private void throwDepthError(String title, BeanUtilsFieldDescriptor fd1, ListSpec listSpec1, BeanUtilsFieldDescriptor fd2,
			ListSpec listSpec2) {
		String error = String.format("copyFields. field '%s' has %s depth %d, but field '%s' has different depth %d",
				fd1.getName(), title, listSpec1.depth, fd2.getName(), listSpec2.depth);
		throw new FieldCopyException(error);
	}

	private FieldInfo buildSourceFieldInfo(FieldPair pair, Class<?> srcElementClass,
			CopySpec copySpec, boolean isArray) {
		FieldInfo sourceField = new FieldInfo();
		sourceField.fieldName = pair.srcProp.getName();
		sourceField.fieldClass = srcElementClass;
		sourceField.beanClass = copySpec.sourceObj.getClass();
		sourceField.isArray = isArray;
		return sourceField;
	}
	private FieldInfo buildDestFieldInfo(FieldPair pair, Class<?> destElementClass, CopySpec copySpec, boolean isArray) {
		FieldInfo destField = new FieldInfo();
		destField.fieldName = pair.destProp.getName();
		destField.fieldClass = destElementClass;
		destField.beanClass = copySpec.destObj.getClass();
		destField.isArray = isArray;
		return destField;
	}


	public ValueConverter useConverterIfPresent(CopySpec copySpec, FieldPair pair, Object orig, List<ValueConverter> converterL) {
		if (CollectionUtils.isNotEmpty(converterL)) {
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
			for(ValueConverter converter: converterL) {
				//TODO: fix value null issue
				
				if (converter.canConvert(sourceField, destField)) {
					return converter;
				}
			}
		}
		return null;
	}
	
}