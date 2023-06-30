package org.dnal.fieldcopy.codegen;

public class VarNameGenerator {
    public int nextI = 1;

    public String nextVarName() {
        return nextVarName("tmp");
    }
    public String nextVarName(String prefix) {
        String s = String.format("%s%d", prefix, nextI++);
        return s;
    }
}
