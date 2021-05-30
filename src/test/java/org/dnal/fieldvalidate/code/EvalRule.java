package org.dnal.fieldvalidate.code;

public class EvalRule extends ValidationRuleBase {
    private RuleLambda evalRule;

    @Override
    public String getName() {
        if (evalRule instanceof RuleCondition) {
            return ((RuleCondition) evalRule).getName();
        }
        return super.getName();
    }

    @Override
    public boolean canExecute(ValSpec spec) {
        return (spec.evalRule != null);
    }

    @Override
    public void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx) {
        this.evalRule = spec.evalRule; //for getName

        String errStr = spec.evalRule.eval(fieldValue, ctx);
        if (errStr != null) {
            this.addValueError(res, spec, fieldValue, errStr, ctx);
        }
    }
}
