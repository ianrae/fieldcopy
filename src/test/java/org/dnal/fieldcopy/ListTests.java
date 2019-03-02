package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.dnal.fc.CopyOptions;
import org.dnal.fc.DefaultCopyFactory;
import org.dnal.fc.FieldCopier;
import org.dnal.fc.FieldCopyMapping;
import org.dnal.fc.core.CopySpec;
import org.dnal.fc.core.FieldCopyService;
import org.dnal.fc.core.FieldPair;
import org.dnal.fc.core.ValueTransformer;
import org.dnal.fieldcopy.BeanUtilTests.Dest;
import org.dnal.fieldcopy.BeanUtilTests.Source;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.junit.Test;


public class ListTests {
	public static class Holder {
		private int width;
		private List<Source> listSource1;
		
		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public List<Source> getListSource1() {
			return listSource1;
		}

		public void setListSource1(List<Source> listSource1) {
			this.listSource1 = listSource1;
		}
	}
	public static class HolderDest {
		private int width;
		private List<Dest> listSource1;
		
		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public List<Dest> getListSource1() {
			return listSource1;
		}

		public void setListSource1(List<Dest> listSource1) {
			this.listSource1 = listSource1;
		}
	}
	
	public static class XBaseListTransformer implements ValueTransformer {
		private String srcFieldName;
		private Class<?> destElClass;
		private FieldCopyService copySvc;
		
		public XBaseListTransformer(String srcFieldName, Class<?> destElementClass) {
			this.srcFieldName = srcFieldName;
			this.destElClass = destElementClass;
		}
		

		@Override
		public boolean canHandle(String srcFieldName, Object value, Class<?> destClass) {
			return this.srcFieldName.equals(srcFieldName);
		}
		
		@Override
		public Object transformValue(String srcFieldName, Object bean, Object value, Class<?> destClass) {
			@SuppressWarnings("unchecked")
			List<?> list = (List<?>) value;
			
			Class<?> srcElClass = this.detectSrcElementClass(bean);
			List<FieldPair> fieldPairs = copySvc.buildAutoCopyPairs(srcElClass, destElClass);
			
			List<Object> list2 = new ArrayList<>();
			for(Object el: list) {
				CopySpec spec = new CopySpec();
				spec.sourceObj = el;
				spec.destObj = createObject(destElClass);
				spec.fieldPairs = fieldPairs;
				spec.options = new CopyOptions(); //TODO: should be propogated
				spec.mappingL = null;
				spec.transformerL = null;
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

		protected Object copyElement(Object el) {
			return null;
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
	
	
	@Test
	public void test() {
		Source src = new Source("bob", 33);
		Holder holder = new Holder();
		holder.setWidth(55);
		
		List<Source> list = new ArrayList<>();
		list.add(src);
		holder.setListSource1(list);
		
		HolderDest holder2 = new HolderDest();
		
		FieldCopier copier = createCopier();
		copier.copy(holder, holder2).autoCopy().execute();
		assertEquals(55, holder2.getWidth());
		assertEquals(1, holder2.getListSource1().size());
		
		//TODO: fix class cast exception. we need a way to run mapper
//		Des?St dest = holder2.getListSource1().get(0);
	}
	
	@Test
	public void test22() {
		Source src = new Source("bob", 33);
		Holder holder = new Holder();
		holder.setWidth(55);
		
		List<Source> list = new ArrayList<>();
		list.add(src);
		holder.setListSource1(list);
		
		HolderDest holder2 = new HolderDest();
		
		FieldCopier copier = createCopier();
		XBaseListTransformer transformer = new XBaseListTransformer("listSource1", Dest.class);
		
		copier.copy(holder, holder2).withTransformers(transformer).autoCopy().execute();
		assertEquals(55, holder2.getWidth());
		assertEquals(1, holder2.getListSource1().size());
		
		//TODO: fix class cast exception. we need a way to run mapper
//		Des?St dest = holder2.getListSource1().get(0);
	}
	
	@Test
	public void test2() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Source src = new Source("bob", 33);
		Holder holder = new Holder();
		holder.setWidth(55);
		
		List<Source> list = new ArrayList<>();
		list.add(src);
		holder.setListSource1(list);
		
		Class<?> clazz = holder.getClass();
		Field field = clazz.getDeclaredField("listSource1");
		
		//determine type of list element
		System.out.println(field.getName());
		Class<?> c2 = List.class;
		if (c2.isAssignableFrom(field.getType())) {
			System.out.println("sdf");
			field.setAccessible(true);
			Collection<?> col = (Collection<?>) field.get(holder);
			
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
					break;
				}
			}
			
		}
	}
	
	//--
	private FieldCopier createCopier() {
		DefaultCopyFactory.setLogger(new SimpleConsoleLogger());
		return DefaultCopyFactory.Factory().createCopier();
	}
}
