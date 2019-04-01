package org.dnal.fieldcopy.planner;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.dnal.fieldcopy.BaseTest;
import org.dnal.fieldcopy.FieldCopier;
import org.dnal.fieldcopy.DefaultCopyFactory;
import org.dnal.fieldcopy.FieldCopierTests.Source;
import org.dnal.fieldcopy.ListTests.Holder;
import org.dnal.fieldcopy.ListTests.HolderDest;
import org.dnal.fieldcopy.TransitiveTests.MyConverter1;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.dnal.fieldcopy.service.beanutils.BUClassPlan;
import org.dnal.fieldcopy.service.beanutils.BUCopyService;
import org.junit.Test;

/**
 * Rewrite based on the idea of creating a recursive execution plan.
 * For each field to be copied we create a FieldPlan that defines converters and other
 * conversion parameters.
 * A field that is a bean contains a sub-plan.
 * 
 * However, BeanUtil API is based on objects, not classes. As we inspect the fields to 
 * build the plan, if we encounter a null value for a bean field, we can't generate a 
 * sub-plan for it.  When this occurs we set the lazyGenerationNeeded flag to true, so 
 * that when the plan is executed we can generate the sub-plan then.
 *  * this may never occur if the field is always null
 *  * when we lazily create the plan, must do it in thread-safe way
 * 
 * plan backoff -- this is the concept that once we generate a full tree of plan and sub-plans,
 * we may notice that the leaf sub-plans don't have any converters or other FieldCopy features.
 * They could be copied using BeanUtils directly.  So we can set a directMode = true and 
 * set the sub-plan to null.  This can eventually be propagated upward to parent sub-plans.
 * It may end up that no sub-plans are needed at all -- a much faster performance.
 * 
 * @author Ian Rae
 *
 */
public class PlannerTests extends BaseTest {
	
	public static class A {
		private String name1;
		private String name2;
		private B bVal;
		
		public A(String name1, String name2) {
			super();
			this.name1 = name1;
			this.name2 = name2;
		}
		public String getName1() {
			return name1;
		}
		public void setName1(String name1) {
			this.name1 = name1;
		}
		public String getName2() {
			return name2;
		}
		public void setName2(String name2) {
			this.name2 = name2;
		}
		public B getbVal() {
			return bVal;
		}
		public void setbVal(B bVal) {
			this.bVal = bVal;
		}
	}
	public static class ADTO {
		private String name1;
		private String name2;
		private BDTO bVal;

		public String getName1() {
			return name1;
		}
		public void setName1(String name1) {
			this.name1 = name1;
		}
		public String getName2() {
			return name2;
		}
		public void setName2(String name2) {
			this.name2 = name2;
		}
		public BDTO getbVal() {
			return bVal;
		}
		public void setbVal(BDTO bVal) {
			this.bVal = bVal;
		}
	}
	
	public static class B {
		private String title;
		
		public B(String title) {
			super();
			this.title = title;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}
	public static class BDTO {
		private String title;
		
		public BDTO() {
		}
		public BDTO(String title) {
			super();
			this.title = title;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}
	
	//add class B and then C
	//C should have a date field. then test that builtIn converter for dates gets applied to C
	
	@Test
	public void testString() {
		A src = new A("bob", "smith");
		ADTO dest = new ADTO();
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).autoCopy().execute();
	
		assertEquals("bob", dest.getName1());
		assertEquals("smith", dest.getName2());
	}
	
	@Test
	public void testSubPlan() {
		A src = new A("bob", "smith");
		B bval = new B("toronto");
		src.setbVal(bval);
		ADTO dest = new ADTO();
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).autoCopy().execute();
	
		assertEquals("bob", dest.getName1());
		assertEquals("smith", dest.getName2());
		assertEquals("toronto", dest.getbVal().getTitle());
		
		log("again..");
		src = new A("bob", "smith");
		bval = new B("toronto");
		src.setbVal(bval);
		dest = new ADTO();
		
		copier.copy(src, dest).autoCopy().execute();
	
		assertEquals("bob", dest.getName1());
		assertEquals("smith", dest.getName2());
		assertEquals("toronto", dest.getbVal().getTitle());
	}
	
	@Test
	public void testSubPlanLazy() {
		A src = new A("bob", "smith");
		ADTO dest = new ADTO();
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).autoCopy().execute();
	
		assertEquals("bob", dest.getName1());
		assertEquals("smith", dest.getName2());
		assertEquals(null, dest.getbVal());
		
		log("again..");
		src = new A("bob", "smith");
		B bval = new B("toronto");
		src.setbVal(bval);
		dest = new ADTO();
		
		copier.copy(src, dest).autoCopy().execute();
	
		assertEquals("bob", dest.getName1());
		assertEquals("smith", dest.getName2());
		assertEquals("toronto", dest.getbVal().getTitle());
		
		log("again2..");
		src = new A("bob", "smith");
		bval = new B("toronto");
		src.setbVal(bval);
		dest = new ADTO();
		
		copier.copy(src, dest).autoCopy().execute();
	
		assertEquals("bob", dest.getName1());
		assertEquals("smith", dest.getName2());
		assertEquals("toronto", dest.getbVal().getTitle());
		
		BUCopyService plannerSvc = (BUCopyService) copier.getCopyService();
		assertEquals(1, plannerSvc.getPlanCacheSize());
	}
	
	@Test
	public void testConverter() {
		A src = new A("bob", "smith");
		B bval = new B("toronto");
		src.setbVal(bval);
		ADTO dest = new ADTO();
		MyConverter1 conv = new MyConverter1();
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).withConverters(conv).autoCopy().execute();
	
		assertEquals("BOB", dest.getName1());
		assertEquals("SMITH", dest.getName2());
		assertEquals("TORONTO", dest.getbVal().getTitle());
		
		log("again..");
		src = new A("BOB", "smith");
		bval = new B("toronto");
		src.setbVal(bval);
		dest = new ADTO();
		
		copier.copy(src, dest).withConverters(conv).autoCopy().execute();
	
		assertEquals("BOB", dest.getName1());
		assertEquals("SMITH", dest.getName2());
		assertEquals("TORONTO", dest.getbVal().getTitle());
	}
	
	@Test
	public void testList() {
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
		
		
		BUCopyService plannerSvc = (BUCopyService) copier.getCopyService();
		assertEquals(2, plannerSvc.getPlanCacheSize());
		BUClassPlan plan = plannerSvc.findPlan(Holder.class.getName());
		assertEquals(1, plan.converterL.size());
	}
	
	

	@Override
	protected FieldCopier createCopier() {
		DefaultCopyFactory.setLogger(new SimpleConsoleLogger());
		DefaultCopyFactory.Factory().createLogger().enableLogging(true);
		//for unit tests we want a fresh one each time
		DefaultCopyFactory.clearCopyService();
		return DefaultCopyFactory.Factory().createCopier();
	}
}
