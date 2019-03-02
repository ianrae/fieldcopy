package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.dnal.fc.DefaultCopyFactory;
import org.dnal.fc.FieldCopier;
import org.dnal.fc.core.ListElementTransformer;
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
	public void test2() {
		Source src = new Source("bob", 33);
		Holder holder = new Holder();
		holder.setWidth(55);
		
		List<Source> list = new ArrayList<>();
		list.add(src);
		holder.setListSource1(list);
		
		HolderDest holder2 = new HolderDest();
		FieldCopier copier = createCopier();
		ListElementTransformer transformer = new ListElementTransformer("listSource1", Dest.class);
		
		copier.copy(holder, holder2).withTransformers(transformer).autoCopy().execute();
		assertEquals(55, holder2.getWidth());
		assertEquals(1, holder2.getListSource1().size());
	}
	
	@Test
	public void test3() {
		Source src = new Source("bob", 33);
		Holder holder = new Holder();
		holder.setWidth(55);
		
		List<Source> list = new ArrayList<>();
		list.add(src);
		holder.setListSource1(list);
		
		HolderDest holder2 = new HolderDest();
		FieldCopier copier = createCopier();
		
		//OPTIONAL declare listHint for each field where you are converting list element types
		//If the source and destination list elements are the same type, listHint is not needed.
		copier.copy(holder, holder2).listHint("listSource1", Dest.class).autoCopy().execute();
		assertEquals(55, holder2.getWidth());
		assertEquals(1, holder2.getListSource1().size());
	}
	
	@Test
	public void test3a() {
		Source src = new Source("bob", 33);
		Holder holder = new Holder();
		holder.setWidth(55);
		
		List<Source> list = new ArrayList<>();
		list.add(src);
		holder.setListSource1(list);
		
		HolderDest holder2 = new HolderDest();
		FieldCopier copier = createCopier();
		
		//will automatically create transformer
		copier.copy(holder, holder2).autoCopy().execute();
		assertEquals(55, holder2.getWidth());
		assertEquals(1, holder2.getListSource1().size());
	}
	
	@Test
	public void testDetectElementClass() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Source src = new Source("bob", 33);
		Holder holder = new Holder();
		holder.setWidth(55);
		
		List<Source> list = new ArrayList<>();
		list.add(src);
		holder.setListSource1(list);
		
		Class<?> clazz = holder.getClass();
		Field field = clazz.getDeclaredField("listSource1");
		
		
		Type typ = field.getGenericType();
		if (typ != null) {
			if (typ instanceof ParameterizedType) {
				ParameterizedType paramType = (ParameterizedType) typ;
				Type[] argTypes = paramType.getActualTypeArguments();
			//	log(">>" + argTypes[0].getTypeName());
			}		
//		Type z = clazz.getComponentType();
		}
		
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
