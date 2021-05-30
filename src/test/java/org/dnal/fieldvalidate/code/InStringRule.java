package org.dnal.fieldvalidate.code;

import java.util.stream.Collectors;

public class InStringRule extends ValidationRuleBase {
    @Override
    public String getName() {
        return "in";
    }

    @Override
    public boolean canExecute(ValSpec spec) {
        return (spec.inStrList != null);
    }

    @Override
    public void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx) {
        if (!(fieldValue instanceof String)) {
            throwFieldException(spec, fieldValue, ctx, this);
        }

        boolean found = false;
        for (String el : spec.inStrList) {
            if (compareStrValues(fieldValue, el) == 0) {
                found = true;
                break;
            }
        }
        if (!found) {
            String elStr = spec.inStrList.stream().map(Object::toString)
                    .collect(Collectors.joining(","));
            String msg = String.format("in(%s) failed. actual value: %s", elStr, fieldValue.toString());
            addValueError(res, spec, fieldValue, msg, ctx);
        }
    }
}
