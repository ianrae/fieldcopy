package org.dnal.fieldvalidate.code;

import java.util.stream.Collectors;

public class InNumericRule extends ValidationRuleBase {
    @Override
    public boolean canExecute(ValSpec spec) {
        return (spec.inList != null);
    }

    @Override
    public void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx) {
        boolean found = false;
        for (Number el : spec.inList) {
            if (compareValues(fieldValue, el, spec, ctx, this) == 0) {
                found = true;
                break;
            }
        }
        if (!found) {
            String elStr = spec.inList.stream().map(Object::toString)
                    .collect(Collectors.joining(","));
            String msg = String.format("in(%s) failed. actual value: %s", elStr, fieldValue.toString());
            addValueError(res, spec, fieldValue, msg, ctx);
        }
    }
}
