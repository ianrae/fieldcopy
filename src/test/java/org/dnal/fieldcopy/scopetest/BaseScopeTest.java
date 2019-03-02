package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.dnal.fc.DefaultCopyFactory;
import org.dnal.fc.FieldCopier;
import org.dnal.fieldcopy.FieldCopyException;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;

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
		copySrcFieldToFail(srcField, destField, true);
	}
	protected void copySrcFieldToFail(String srcField, String destField, boolean doReset) {
		if (doReset) {
			reset();
		}
		boolean failed = false;
		try {
			copySrcFieldTo(srcField, destField, true);
		} catch (FieldCopyException e) {
			failed = true;
			System.out.println(e.getMessage());
		}
		assertEquals(true, failed);
	}
	
	protected Date createDate(int year, int mon, int day) {
	    Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.YEAR, year);
	    cal.set(Calendar.MONTH, mon - 1);
	    cal.set(Calendar.DATE, day);
	    cal.set(Calendar.HOUR_OF_DAY, 7);
	    cal.set(Calendar.MINUTE, 30);
	    cal.set(Calendar.SECOND, 41);
	    cal.set(Calendar.MILLISECOND, 0);
	    Date dt = cal.getTime();
	    return dt;
	}
		

	
}
