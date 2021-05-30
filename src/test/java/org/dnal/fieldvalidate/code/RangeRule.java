package org.dnal.fieldvalidate.code;

public class RangeRule extends ValidationRuleBase {
    @Override
    public boolean canExecute(ValSpec spec) {
        return (spec.minRangeObj != null && spec.maxRangeObj != null);
    }

    @Override
    public void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx) {
        if (compareValues(fieldValue, spec.minRangeObj, spec, ctx) < 0) {
            String msg = String.format("range(%s,%s) failed. actual value: %s", spec.minRangeObj.toString(), spec.maxRangeObj.toString(),
                    fieldValue.toString());
            addValueError(res, spec, fieldValue, msg, ctx);
        } else if (compareValues(fieldValue, spec.maxRangeObj, spec, ctx) > 0) {
            String msg = String.format("range(%s,%s) failed. actual value: %s", spec.minRangeObj.toString(), spec.maxRangeObj.toString(),
                    fieldValue.toString());
            addValueError(res, spec, fieldValue, msg, ctx);
        }
    }
}
