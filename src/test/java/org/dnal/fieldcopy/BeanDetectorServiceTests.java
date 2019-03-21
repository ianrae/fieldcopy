package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.dnal.fieldcopy.FieldCopierTests.Dest;
import org.dnal.fieldcopy.FieldCopierTests.Source;
import org.junit.Test;

public class BeanDetectorServiceTests extends BaseTest {
	
	public interface BeanDetectorService {
		boolean isBeanClass(Class<?> clazz);
	}
	
	public static class BeanUtilsBeanDetectorService implements BeanDetectorService {
		private Map<Class<?>, String> knownTypes = new HashMap<>();
		
		public BeanUtilsBeanDetectorService() {
			//primitive types
			add(Boolean.TYPE);
			add(Byte.TYPE);
			add(Character.TYPE);
			add(Double.TYPE);
			add(Float.TYPE);
			add(Integer.TYPE);
			add(Long.TYPE);
			add(Short.TYPE);
			
			//standard types
	        add(BigDecimal.class);
	        add(BigInteger.class);
	        add(Boolean.class);
	        add(Byte.class);
	        add(Character.class);
	        add(Double.class);
	        add(Float.class);
	        add(Integer.class);
	        add(Long.class);
	        add(Short.class);
	        add(String.class);
	        
	        //others
	        add(Class.class);
	        add(java.util.Date.class);
	        add(Calendar.class);
	        add(File.class);
	        add(java.sql.Date.class);
	        add(java.sql.Time.class);
	        add(Timestamp.class);
	        add(URL.class);
			
		}

		private void add(Class<?> type) {
			knownTypes.put(type, "");
		}

		/**
		 * If it's not one of our known types, it must be a bean.
		 */
		@Override
		public boolean isBeanClass(Class<?> clazz) {
			return !knownTypes.containsKey(clazz);
		}
		
	}
	
	@Test
	public void test() {
		this.svc = new BeanUtilsBeanDetectorService();
		chkFalse(String.class);
		chkFalse(Integer.class);
		chkFalse(Boolean.class);
		
		chkTrue(Source.class);
		chkTrue(Dest.class);
	}
	
	//--
	private BeanUtilsBeanDetectorService svc;
	
	private void chkFalse(Class<?> clazz) {
		assertEquals(false, svc.isBeanClass(clazz));
	}
	private void chkTrue(Class<?> clazz) {
		assertEquals(true, svc.isBeanClass(clazz));
	}
}
