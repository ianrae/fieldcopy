package org.dnal.fieldcopy.service.beanutils.old;

import java.util.ArrayList;
import java.util.List;

public class ExecuteCopyPlan {
	public List<FieldPlan> fieldL = new ArrayList<>();
	
	public boolean inConverter; //used to make better error messages
	public String currentFieldName;
}