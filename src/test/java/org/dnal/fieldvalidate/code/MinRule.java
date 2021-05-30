package org.dnal.fieldvalidate.code;

public class MinRule extends ValidationRuleBase {
    @Override
    public boolean canExecute(ValSpec spec) {
        return spec.minObj != null;
    }

    @Override
    public void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx) {
        if (compareValues(fieldValue, spec.minObj, spec, ctx, this) < 0) {
            String msg = String.format("min(%s) failed. actual value: %s", spec.minObj.toString(), fieldValue.toString());
            addValueError(res, spec, fieldValue, msg, ctx);
        }
    }
}
