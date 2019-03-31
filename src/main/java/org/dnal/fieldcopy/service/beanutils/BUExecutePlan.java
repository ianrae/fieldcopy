package org.dnal.fieldcopy.service.beanutils;

import org.dnal.fieldcopy.core.CopySpec;

public class BUExecutePlan {
	public Object srcObject;
	public Object destObj;
	public BUClassPlan classPlan;
	public boolean inConverter; //used to make better error messages
	public String currentFieldName;
	public CopySpec copySpec;
}