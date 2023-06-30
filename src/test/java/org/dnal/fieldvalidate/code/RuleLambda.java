package org.dnal.fieldvalidate.code;

import org.dnal.fieldvalidate.code.RuleContext;

public interface RuleLambda {
    String eval(Object fieldValue, RuleContext ctx);
}
