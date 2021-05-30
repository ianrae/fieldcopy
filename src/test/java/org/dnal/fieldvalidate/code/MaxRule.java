package org.dnal.fieldvalidate.code;

public class MaxRule extends ValidationRuleBase {
    @Override
    public boolean canExecute(ValSpec spec) {
        return spec.maxObj != null;
    }

    @Override
    public void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx) {
        if (compareValues(fieldValue, spec.maxObj, spec, ctx, this) > 0) {
            String msg = String.format("max(%s) failed. actual value: %s", spec.maxObj.toString(), fieldValue.toString());
            addValueError(res, spec, fieldValue, msg, ctx);
        }
    }
}
