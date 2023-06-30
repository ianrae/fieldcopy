package org.dnal.fieldcopy.bdd.core;

import static java.util.Objects.isNull;

public class SnippetContext {
    public BDDMode mode;
    public int rNumber; //create feature. eg R200
    public int rSubNumber;
    public int testNum;
    public Integer useConverterFromTest;

    public String buildSuffix() {
        int testIndex = isNull(useConverterFromTest) ? testNum : useConverterFromTest;
        if (rNumber == rSubNumber) {
            return String.format("R%dT%d", rNumber, testIndex);
        } else {
            return String.format("R%dsub%dT%d", rNumber, rSubNumber, testIndex);
        }
    }

}
