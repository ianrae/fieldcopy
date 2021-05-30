package org.dnal.fieldvalidate.code;

import java.util.ArrayList;
import java.util.List;

public class ValidateBuilder {
    private List<BuilderFluent1> list = new ArrayList<>();
    private List<ValSpec> specList = new ArrayList<>();
    private boolean haveBuiltLast;

    public BuilderFluent1 field(String fieldName) {
        buildSpecForLastVal();
        haveBuiltLast = false; //reset
        BuilderFluent1 val1 = new BuilderFluent1(fieldName, list, specList);
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
            BuilderFluent1 val = list.get(list.size() - 1);
            val.buildAndAddSpec();
            haveBuiltLast = true;
        }
    }
}
