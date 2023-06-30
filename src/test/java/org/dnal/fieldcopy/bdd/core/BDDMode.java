package org.dnal.fieldcopy.bdd.core;

public enum BDDMode {
    CODEGEN, //generate Java classes in the gen subdir
    RUNTIME; //use the generated classes to convert some data

    public static boolean isCodeGen(BDDMode mode) {
        return CODEGEN.equals(mode);
    }
}
