package org.dnal.fieldcopy.orika;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.dnal.fieldcopy.FieldCopierTests;
import org.dnal.fieldcopy.ListTests;
import org.dnal.fieldcopy.FieldCopierTests.Dest;
import org.dnal.fieldcopy.FieldCopierTests.Source;
import org.dnal.fieldcopy.ListTests.Holder;
import org.dnal.fieldcopy.ListTests.HolderDest;
import org.junit.Before;
import org.junit.Test;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

//https://www.baeldung.com/orika-mapping
public class OrikaListTests {

	@Test
	public void test() {
		Source src = new Source("bob", 33);
		Holder holder = new Holder();
		holder.setWidth(55);
		
		List<Source> list = new ArrayList<>();
		list.add(src);
		holder.setListSource1(list);
		
		mapperFactory.classMap(Holder.class, HolderDest.class);
		MapperFacade mapper = mapperFactory.getMapperFacade();
		HolderDest dest = mapper.map(holder, HolderDest.class);

		assertEquals(55, dest.getWidth());
		assertEquals(1, dest.getListSource1().size());
		Dest x = dest.getListSource1().get(0);
		assertEquals("bob", x.getName());
	}

	//--
	private MapperFactory mapperFactory;

	@Before
	public void init() {
		mapperFactory = new DefaultMapperFactory.Builder().build();

	}
}
