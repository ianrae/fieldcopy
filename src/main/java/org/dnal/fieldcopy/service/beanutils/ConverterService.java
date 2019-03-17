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
	
	public ConverterService(SimpleLogger logger) {
		this.logger = logger;
		this.converterFactory = new ListElementConverterFactory();
	}

	public void addListConverterIfNeeded(FieldPair pair, CopySpec copySpec, Object destObj) {
		BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp;
		BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) pair.destProp;
		
		Class<?> srcFieldClass = fd1.pd.getPropertyType();
		Class<?> destFieldClass = fd2.pd.getPropertyType();
		if (Collection.class.isAssignableFrom(srcFieldClass) && 
				Collection.class.isAssignableFrom(destFieldClass)) {
			
			if (copySpec.converterL == null) {
				copySpec.converterL = new ArrayList<>();
			}
			
			ListSpec listSpec1 = ReflectionUtil.buildListSpec(copySpec.sourceObj, fd1);
			ListSpec listSpec2 = ReflectionUtil.buildListSpec(destObj, fd2);
			
			if (listSpec1.depth != listSpec2.depth) {
				String error = String.format("copyFields. field '%s' has list depth %d, but field '%s' has different depth %d",
						fd1.getName(), listSpec1.depth, fd2.getName(), listSpec2.depth);
				throw new FieldCopyException(error);
			}

			Class<?> srcElementClass = listSpec1.elementClass;
			Class<?> destElementClass = listSpec2.elementClass;
			FieldInfo sourceField = new FieldInfo();
			sourceField.fieldName = pair.srcProp.getName();
			sourceField.fieldClass = srcElementClass;
			sourceField.beanClass = copySpec.sourceObj.getClass();
			
			FieldInfo destField = new FieldInfo();
			destField.fieldName = pair.destProp.getName();
			destField.fieldClass = destElementClass;
			destField.beanClass = copySpec.destObj.getClass();
			
			for(ValueConverter converter: copySpec.converterL) {
				//a special use of converter. normally we pass field name and its class (and the dest class).
				//Here we are passing the fieldName (which is a list) and source and destination *element* classes
				if (converter.canConvert(sourceField, destField)) {
					//if already is a converter, nothing more to do
					return;
				}
			}
			
			//add one
			String name = pair.srcProp.getName();
			ListElementConverter converter = converterFactory.createListConverter(name, srcElementClass, destElementClass);
			if (converter == null) {
				String error = String.format("Copying list<%s> to list<%s> is not supported.", srcElementClass.getName(), destElementClass.getName());
				throw new FieldCopyException(error);
			}
			converter.setDepth(listSpec1.depth);
			copySpec.converterL.add(converter);
		}
	}
	
	public void addArrayConverterIfNeeded(FieldPair pair, CopySpec copySpec, Object destObj) {
		BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp;
		BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) pair.destProp;
		
		Class<?> srcFieldClass = fd1.pd.getPropertyType();
		Class<?> destFieldClass = fd2.pd.getPropertyType();
		if (srcFieldClass.isArray() && destFieldClass.isArray()) { 
			
			if (copySpec.converterL == null) {
				copySpec.converterL = new ArrayList<>();
			}
			
			ListSpec listSpec1 = ReflectionUtil.buildArraySpec(copySpec.sourceObj, fd1);
			ListSpec listSpec2 = ReflectionUtil.buildArraySpec(destObj, fd2);
			
			if (listSpec1.depth != listSpec2.depth) {
				String error = String.format("copyFields. field '%s' has array depth %d, but field '%s' has different depth %d",
						fd1.getName(), listSpec1.depth, fd2.getName(), listSpec2.depth);
				throw new FieldCopyException(error);

			}

			Class<?> srcElementClass = listSpec1.elementClass;
			Class<?> destElementClass = listSpec2.elementClass;
			FieldInfo sourceField = new FieldInfo();
			sourceField.fieldName = pair.srcProp.getName();
			sourceField.fieldClass = srcElementClass;
			sourceField.beanClass = copySpec.sourceObj.getClass();
			sourceField.isArray = true;
			
			FieldInfo destField = new FieldInfo();
			destField.fieldName = pair.destProp.getName();
			destField.fieldClass = destElementClass;
			destField.beanClass = copySpec.destObj.getClass();
			destField.isArray = true;
			
			for(ValueConverter converter: copySpec.converterL) {
				//a special use of converter. normally we pass field name and its class (and the dest class).
				//Here we are passing the fieldName (which is a list) and source and destination *element* classes
				if (converter.canConvert(sourceField, destField)) {
					//if already is a converter, nothing more to do
					return;
				}
			}
			
			//add one
			String name = pair.srcProp.getName();
			ArrayElementConverter converter = converterFactory.createArrayConverter(name, srcElementClass, destElementClass);
			if (converter == null) {
				String error = String.format("Copying array<%s> to array<%s> is not supported.", srcElementClass.getName(), destElementClass.getName());
				throw new FieldCopyException(error);
			}
			converter.setDepth(listSpec1.depth);
			copySpec.converterL.add(converter);
		}
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
			destField.beanClass = copySpec.destObj.getClass();
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