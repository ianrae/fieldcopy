package org.dnal.fieldvalidate.code;

import java.util.ArrayList;
import java.util.List;

public class ValidateBuilder {
    private List<Val1> list = new ArrayList<>();
    private List<ValSpec> specList = new ArrayList<>();
    private boolean haveBuiltLast;

    public Val1 field(String fieldName) {
        buildSpecForLastVal();
        haveBuiltLast = false; //reset
        Val1 val1 = new Val1(fieldName, list, specList);
        list.add(val1);
        return val1;
    }

    public Validator build() {
        buildSpecForLastVal();
        return new Validator(specList);
    }

    private void buildSpecForLastVal() {
        if (haveBuiltLast) return;
        if (!list.isEmpty()) {
            Val1 val = list.get(list.size() - 1);
            val.buildAndAddSpec();
            haveBuiltLast = true;
        }
    }
}
