package org.dnal.fieldvalidate.code;

public class MaxLenRule extends ValidationRuleBase {
    @Override
    public boolean canExecute(ValSpec spec) {
        return (spec.strMaxLen != null);
    }

    @Override
    public void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx) {
        int len = fieldValue.toString().length();
        if (len > spec.strMaxLen.intValue()) {
            String msg = String.format("maxlen(%d) failed. actual value: %s", spec.strMaxLen, fieldValue.toString());
            addValueError(res, spec, fieldValue, msg, ctx);
        }
    }
}
