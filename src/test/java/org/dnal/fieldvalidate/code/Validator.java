package org.dnal.fieldvalidate.code;

import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Validator {
    public final List<ValSpec> specList;
    private Object target;
    private List<ValidationRule> ruleList = new ArrayList<>();
    private ErrorMessageBuilder errorMessageBuilder;

    public Validator(List<ValSpec> specList) {
        this.specList = specList;
        this.ruleList.add(new MinRule());
        this.ruleList.add(new MaxRule());
        this.ruleList.add(new RangeRule());
        this.ruleList.add(new InNumericRule());
        this.ruleList.add(new InStringRule());
        this.ruleList.add(new MaxLenRule());
        this.ruleList.add(new EvalRule());
        this.ruleList.add(new SubObjRule());
        this.ruleList.add(new ElementsRule());
        this.ruleList.add(new InEnumRule());

        //set spec.runner
        for (ValSpec spec : specList) {
            if (spec.runner != null) {
                continue;
            }
            for (ValidationRule rule : ruleList) {
                if (rule.canExecute(spec)) {
                    spec.runner = createNewInstance(rule); //each spec needs its own rule instance
                    break;
                }
            }
            //Note. if spec only has isNotNull then runner will be null. which is ok.
        }
    }

    private ValidationRule createNewInstance(ValidationRule rule) {
        ValidationRule copy = null;
        try {
            copy = rule.getClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return copy;
    }

    public ValidationResults validate(Object target) {
        return validate(target, null, null);
    }

    public ValidationResults validate(Object target, Object rootTarget, Integer index) {
        ValidationResults res = new ValidationResults();
        for (ValSpec spec : specList) {
            try {
                doValidate(target, spec, res, rootTarget, index);
            } catch (FieldValidateException e) {
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (errorMessageBuilder != null) {
            for (FieldError err : res.errL) {
                String msg = errorMessageBuilder.buildMessage(err);
                err.errMsg = msg;
            }
        }

        return res;
    }

    private ValidationResults doValidate(Object target, ValSpec spec, ValidationResults res, Object rootTarget, Integer index) throws Exception {
        Object fieldValue;
        if (index != null) {
            String s = String.format("%s[%d]", spec.fieldName, index.intValue());
            fieldValue = PropertyUtils.getProperty(target, s);
        } else {
            fieldValue = PropertyUtils.getProperty(target, spec.fieldName);
        }

        if (fieldValue == null && spec.isNotNull) {
            String msg = String.format("unexpected null value");
            addNotNullError(res, spec, msg, target, rootTarget, index);
        }

        RuleContext ctx = new RuleContext();
        ctx.target = target;
        ctx.root = rootTarget;
        ctx.index = index;
//            for(ValidationRule rule: ruleList) {
//                if (rule.canExecute(spec)) {
//                    rule.validate(spec, fieldValue, res, ctx);
//                }
//            }
        //if only isNotNull then will be no runner
        if (fieldValue != null && spec.runner != null) {
            spec.runner.validate(spec, fieldValue, res, ctx);
        }

        return res;
    }

    private void addNotNullError(ValidationResults res, ValSpec spec, String message, Object target, Object rootTarget, Integer index) {
        FieldError err = new FieldError(target.getClass().getSimpleName(), spec.fieldName, index, null, ErrorType.NOT_NULL);
        err.fullTargetPath = FieldError.buildTargetPath(rootTarget, target, spec.fieldName, index);
        err.errMsg = String.format("%s: %s", err.fullTargetPath, message);
        res.errL.add(err);
    }

    public List<ValSpec> getSpecList() {
        return specList;
    }

    public ErrorMessageBuilder getCustomErrorMessageBuilder() {
        return errorMessageBuilder;
    }

    public void setCustomErrorMessageBuilder(ErrorMessageBuilder errorMessageBuilder) {
        this.errorMessageBuilder = errorMessageBuilder;
    }
}
