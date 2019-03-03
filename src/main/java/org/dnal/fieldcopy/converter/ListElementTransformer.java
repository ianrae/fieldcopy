package org.dnal.fieldcopy.converter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.dnal.fieldcopy.CopyOptions;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldPair;

public class ListElementTransformer implements ValueTransformer {
	private String srcFieldName;
	private Class<?> destElClass;
	private FieldCopyService copySvc;
	
	public ListElementTransformer(String srcFieldName, Class<?> destElementClass) {
		this.srcFieldName = srcFieldName;
		this.destElClass = destElementClass;
	}

	@Override
	public boolean canHandle(String srcFieldName, Class<?>srcClass, Class<?> destClass) {
		return this.srcFieldName.equals(srcFieldName);
	}
	
	@Override
	public Object transformValue(Object srcBean, Object value, ConverterContext ctx) {
		@SuppressWarnings("unchecked")
		List<?> list = (List<?>) value;
		
		Class<?> srcElClass = this.detectSrcElementClass(srcBean);
		List<FieldPair> fieldPairs = copySvc.buildAutoCopyPairs(srcElClass, destElClass);

		CopySpec spec = new CopySpec();
		spec.fieldPairs = fieldPairs;
		spec.options = new CopyOptions(); //TODO: should be propogated
		spec.mappingL = null;
		spec.transformerL = null;

		List<Object> list2 = new ArrayList<>();
		for(Object el: list) {
			spec.sourceObj = el;
			spec.destObj = createObject(destElClass);
			copySvc.copyFields(spec);
			
			list2.add(spec.destObj);
		}
		return list2;
	}
	private Object createObject(Class<?> clazzDest) {
		Object obj = null;
		try {
			obj = clazzDest.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

	private Class<?> detectSrcElementClass(Object bean) {
		Class<?> clazz = null;
		try {
			clazz = doDetectSrcElementClass(bean);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clazz;
	}
	
	private Class<?> doDetectSrcElementClass(Object bean) throws Exception {
		
		//TODO: fix. getField only works with public fields, but getDeclaredField won't handle inheritance
		Field field = bean.getClass().getDeclaredField(srcFieldName);

		//determine type of list element
		System.out.println(field.getName());
		Class<?> c2 = List.class;
		if (c2.isAssignableFrom(field.getType())) {
			System.out.println("sdf");
			field.setAccessible(true);
			Collection<?> col = (Collection<?>) field.get(bean);

			if (col.isEmpty()) {
				System.out.println("empty");
			} else {
				Iterator<?> it = col.iterator();

				while(it.hasNext()) {
					Object element = it.next();
					if (element == null) {
						continue;
					}
					Class<?> elclazz = element.getClass();
					System.out.println("!!! " + elclazz);
					return elclazz;
				}
			}
		}
		return null;
	}


	public FieldCopyService getCopySvc() {
		return copySvc;
	}


	@Override
	public void setCopySvc(FieldCopyService copySvc) {
		this.copySvc = copySvc;
	}
}