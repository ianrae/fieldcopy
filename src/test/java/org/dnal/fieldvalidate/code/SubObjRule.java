package org.dnal.fieldvalidate.code;

public class SubObjRule extends ValidationRuleBase {
    private Validator subValidator;

    @Override
    public boolean canExecute(ValSpec spec) {
        return (spec.subBuilder != null);
    }

    @Override
    public void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx) {
        if (spec.subBuilder != null && subValidator == null) {
            subValidator = spec.subBuilder.build();
        }
        ValidationResults innerRes = subValidator.validate(fieldValue, ctx.target, null);

        if (!innerRes.hasNoErrors()) {
            res.errL.addAll(innerRes.errL);
        }
    }
}
