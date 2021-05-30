package org.dnal.fieldvalidate.code;

import java.util.ArrayList;
import java.util.List;

public class ValSpec {
    public String fieldName;
    public boolean isNotNull;
    public Object minObj;
    public Object maxObj;
    public Object minRangeObj;
    public Object maxRangeObj;
    public Val1 elementsVal;
    public ValidateBuilder subBuilder;
    public ValidateBuilder mapBuilder;
    public List<Number> inList;
    public ArrayList<String> inStrList;
    public Integer strMaxLen;
    public RuleLambda evalRule;
    public ValidationRule runner; //set by Validator
    public Class<? extends Enum> enumClass;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(fieldName);
        sb.append(":");
        sb.append(isNotNull ? "notNull" : "");
        return sb.toString();
    }
}
