package org.dnal.fieldvalidate.code;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class InEnumRule extends ValidationRuleBase {
    private List<Object> enumValues;
    private String allValues;

    @Override
    public String getName() {
        return "inEnum";
    }

    @Override
    public boolean canExecute(ValSpec spec) {
        return (spec.enumClass != null);
    }

    @Override
    public void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx) {
        if (fieldValue == null) return;
        if (! isEnumValue(fieldValue) && !(fieldValue instanceof String)) {
            throwFieldException(spec, fieldValue, ctx, this);
        }

        if (enumValues == null) {
            extractValues(spec);
        }

        String targetStr = fieldValue.toString();
        for (Object eval : enumValues) {
            String s = eval.toString();
            if (targetStr.equals(s)) {
                return;
            }
        }

        //if we reach here then fieldValue not in enum
        String msg = String.format("inEnum(%s) failed. actual value: %s", allValues, fieldValue.toString());
        addValueError(res, spec, fieldValue, msg, ctx);
    }

    private void extractValues(ValSpec spec) {
        //alternatively
        try {
            Method method = spec.enumClass.getDeclaredMethod("values");
            Object obj = method.invoke(null);
            enumValues = Arrays.asList((Object[]) obj);
            allValues = Arrays.toString((Object[]) obj);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
