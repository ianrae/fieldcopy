package org.dnal.fieldcopy.planner;

import org.dnal.fieldcopy.core.CopySpec;

public class ZExecPlan {
	public Object srcObject;
	public Object destObj;
	public ZClassPlan classPlan;
	public boolean inConverter; //used to make better error messages
	public String currentFieldName;
	public CopySpec copySpec;
}