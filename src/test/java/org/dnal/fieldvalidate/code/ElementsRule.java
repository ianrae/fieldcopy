package org.dnal.fieldvalidate.code;

import java.lang.reflect.Array;
import java.util.Collection;

public class ElementsRule extends ValidationRuleBase {
    private Validator subValidator;

    @Override
    public boolean canExecute(ValSpec spec) {
        return (spec.elementsVal != null);
    }

    @Override
    public void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx) {
        if (spec.elementsVal != null && subValidator == null) {
            spec.elementsVal.buildAndAddSpec();
            this.subValidator = new Validator(spec.elementsVal.specList);
        }

        //TODO need to support all collections and arrays
        int size = -1;
        if (fieldValue instanceof Collection) {
            size = ((Collection<?>) fieldValue).size();
        } else if (fieldValue != null) {
            size = getSizeIfArray(fieldValue);
        }

        if (size < 0) {
            throw new FieldValidateException(String.format("field: %s is not a Collection", spec.fieldName));
        }

        for (int i = 0; i < size; i++) {
            ValidationResults innerRes = subValidator.validate(ctx.target, ctx.target, i);

            if (!innerRes.hasNoErrors()) {
                res.errL.addAll(innerRes.errL);
            }
        }
    }

    private int getSizeIfArray(Object fieldValue) {
        Class clazz = fieldValue.getClass();
        if (clazz.isArray()) {
//                Class arrayType = c.getComponentType();
//                System.out.println("The array is of type: " + arrayType);
            return Array.getLength(fieldValue);
        } else {
            return -1;
        }
    }
}
