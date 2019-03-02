package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import org.dnal.fc.DefaultCopyFactory;
import org.dnal.fc.FieldCopier;
import org.dnal.fieldcopy.FieldCopyException;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.junit.Before;


public class BaseScopeTest {
	
	//--
	protected AllTypesEntity entity;
	protected AllTypesDTO dto;
	protected FieldCopier copier;
	
	protected void init() {
		reset();
		copier = createCopier();
		copier.getOptions().logEachCopy = true;
	}
	protected void reset() {
		entity = createEntity();
		dto = new AllTypesDTO();
	}
	protected AllTypesEntity createEntity() {
		AllTypesEntity entity = new AllTypesEntity();
		entity.primitiveBool = true;
		entity.bool1 = true;
		
		return entity;
	}
	protected void doCopy(String...fields) {
		copier.copy(entity, dto).autoCopy().include(fields).execute();
	}
	
	protected FieldCopier createCopier() {
		DefaultCopyFactory.setLogger(new SimpleConsoleLogger());
		return DefaultCopyFactory.Factory().createCopier();
	}
	
	protected void copySrcFieldTo(String srcField, String destField) {
		copySrcFieldTo(srcField, destField, true);
	}
	protected void copySrcFieldTo(String srcField, String destField, boolean doReset) {
		if (doReset) {
			reset();
		}
		copier.copy(entity, dto).field(srcField, destField).execute();
	}
	protected void copySrcFieldToFail(String srcField, String destField) {
		boolean failed = false;
		try {
			copySrcFieldTo(srcField, destField, true);
		} catch (FieldCopyException e) {
			failed = true;
			System.out.println(e.getMessage());
		}
		assertEquals(true, failed);
	}
	
}
