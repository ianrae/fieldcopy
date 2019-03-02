package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.dnal.fc.DefaultCopyFactory;
import org.dnal.fc.FieldCopier;
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
		Dest dest = holder2.getListSource1().get(0);
	}
	
	
	//--
	private FieldCopier createCopier() {
		DefaultCopyFactory.setLogger(new SimpleConsoleLogger());
		return DefaultCopyFactory.Factory().createCopier();
	}
}
