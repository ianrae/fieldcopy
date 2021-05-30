package org.dnal.fieldvalidate.code;

import org.apache.commons.lang3.StringUtils;

public abstract class ValidationRuleBase implements ValidationRule {

    @Override
    public String getName() {
        String name = this.getClass().getSimpleName();
        if (name.endsWith("Rule")) {
            name = StringUtils.substringBeforeLast(name, "Rule");
        }
        return name.toLowerCase();
    }

    @Override
    public abstract boolean canExecute(ValSpec spec);

    @Override
    public abstract void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx);

    protected int compareStrValues(Object fieldValue, String el) {
        return fieldValue.toString().compareTo(el);
    }

    protected void addValueError(ValidationResults res, ValSpec spec, Object fieldValue, String message, RuleContext ctx) {
        FieldError err = new FieldError(ctx.target.getClass().getSimpleName(), spec.fieldName, ctx.index, fieldValue, ErrorType.VALUE);
        err.fullTargetPath = FieldError.buildTargetPath(ctx.root, ctx.target, spec.fieldName, ctx.index);
        err.errMsg = String.format("%s: %s", err.fullTargetPath, message);
        res.errL.add(err);
    }
    protected FieldError buildUnexpectedError(ValSpec spec, Object fieldValue, String message, RuleContext ctx) {
        FieldError err = new FieldError(ctx.target.getClass().getSimpleName(), spec.fieldName, ctx.index, fieldValue, ErrorType.VALUE);
        err.fullTargetPath = FieldError.buildTargetPath(ctx.root, ctx.target, spec.fieldName, ctx.index);
        err.errMsg = String.format("%s: %s", err.fullTargetPath, message);
        return err;
    }

    protected int compareValues(Object fieldValue, Object minObj, ValSpec spec, RuleContext ctx, ValidationRule self) {
        if (fieldValue instanceof Integer) {
            Integer min = NumberUtils.asInt(minObj);
            return ((Integer) fieldValue).compareTo(min);
        }
        if (fieldValue instanceof Long) {
            Long min = NumberUtils.asLong(minObj);
            return ((Long) fieldValue).compareTo(min);
        }
        if (fieldValue instanceof Float) {
            Float min = NumberUtils.asFloat(minObj);
            return ((Float) fieldValue).compareTo(min);
        }
        if (fieldValue instanceof Double) {
            Double min = NumberUtils.asDouble(minObj);
            return ((Double) fieldValue).compareTo(min);
        }

        throwFieldException(spec, fieldValue, ctx, self);
        return 0; //never executes
    }

    protected void throwFieldException(ValSpec spec, Object fieldValue, RuleContext ctx, ValidationRule self) {
        String typeStr = fieldValue == null ? "null" : fieldValue.getClass().getSimpleName();
        String valueStr = fieldValue == null ? "null" : fieldValue.toString(); //TODO: limit to 200 chars...
        String ruleStr = self.getName();
        String msg = String.format("'%s' rule failed. unsupported type '%s' for value %s", ruleStr, typeStr, valueStr);
        FieldError err = buildUnexpectedError(spec, fieldValue, msg, ctx);
        throw new FieldValidateException(err);
    }

    protected boolean isEnumValue(Object fieldValue) {
        if (fieldValue == null) return false;
        return fieldValue.getClass().isEnum();
    }

}
