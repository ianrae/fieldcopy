package org.dnal.fieldcopy.service.beanutils;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.converters.DateConverter;
import org.dnal.fieldcopy.CopyOptions;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldFilter;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.core.FieldRegistry;
import org.dnal.fieldcopy.core.TargetPair;
import org.dnal.fieldcopy.log.SimpleLogger;

public abstract class BUCopyServiceBase implements FieldCopyService {
	
	protected SimpleLogger logger;
	protected FieldRegistry registry;
	protected BeanUtilsBean beanUtil;
	protected PropertyUtilsBean propertyUtils;
	protected FieldFilter fieldFilter;
	protected BUBeanDetectorService beanDetectorSvc;
	protected BUHelperService helperSvc;
	
	public BUCopyServiceBase(SimpleLogger logger, FieldRegistry registry, FieldFilter fieldFilter) {
		this.logger = logger;
		this.registry = registry;
		this.beanUtil =  BeanUtilsBean.getInstance();
		this.propertyUtils =  new PropertyUtilsBean();
		this.fieldFilter = fieldFilter;
		this.beanDetectorSvc = new BUBeanDetectorService();
		this.helperSvc = new BUHelperService(logger);
		
		//customize Date converter.  There is no good defaut for date conversions
		//so well just do yyyy-MM-dd
		DateConverter dateConverter = new DateConverter();
		dateConverter.setPattern("yyyy-MM-dd");
		ConvertUtils.register(dateConverter, Date.class);
	}
	

	@Override
	public List<FieldPair> buildAutoCopyPairs(TargetPair targetPair, CopyOptions options) {
        List<FieldPair> fieldPairs = registry.findAutoCopyInfo(targetPair);
		if (fieldPairs != null) {
			return fieldPairs;
		}
		
        fieldPairs = buildAutoCopyPairsNoRegister(targetPair.getSrcClass(), targetPair.getDestClass(), options);
		registry.registerAutoCopyInfo(targetPair, fieldPairs);
        return fieldPairs;
	}
	
	public List<FieldPair> buildAutoCopyPairsNoRegister(Class<? extends Object> class1, Class<? extends Object> class2, CopyOptions options) {
        final PropertyDescriptor[] arSrc = propertyUtils.getPropertyDescriptors(class1);
        final PropertyDescriptor[] arDest = propertyUtils.getPropertyDescriptors(class2);
		
        List<FieldPair> fieldPairs = new ArrayList<>();
        for (int i = 0; i < arSrc.length; i++) {
        	PropertyDescriptor pd = arSrc[i];
        	if (! fieldFilter.shouldProcess(class1, pd.getName())) {
        		continue; // No point in trying to set an object's class
            }

        	PropertyDescriptor targetPd = findMatchingField(arDest, pd.getName(), options);
        	if (targetPd != null) {
        		FieldPair pair = new FieldPair();
        		pair.srcProp = new BeanUtilsFieldDescriptor(pd);
        		pair.destFieldName = (targetPd == null) ? null : targetPd.getName();
        		pair.destProp = new BeanUtilsFieldDescriptor(targetPd);
        		fieldPairs.add(pair);
        	}
        }
        return fieldPairs;
	}
	
	private PropertyDescriptor findMatchingField(PropertyDescriptor[] arDest, String name, CopyOptions options) {
		for (int i = 0; i < arDest.length; i++) {
			
			PropertyDescriptor pd = arDest[i];
			if (options.autoCopyCaseSensitiveMatch) {
				if (pd.getName().equals(name)) {
					return pd;
				}
			} else {
				if (pd.getName().equalsIgnoreCase(name)) {
					return pd;
				}
			}
		}
		return null;
	}

	
	@Override
	public void dumpFields(Object sourceObj) {
		helperSvc.dumpFields(sourceObj);
	}

	@Override
	public SimpleLogger getLogger() {
		return logger;
	}

	@Override
	public FieldRegistry getRegistry() {
		return registry;
	}

	@Override
	public String generateExecutionPlanCacheKey(CopySpec spec) {
		return helperSvc.generateExecutionPlanCacheKey(spec);
	}
	
	protected Object getLoggableString(Object value) {
		return helperSvc.getLoggableString(value);
	}
}