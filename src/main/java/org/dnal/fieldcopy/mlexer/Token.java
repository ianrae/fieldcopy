package org.dnal.fieldcopy.mlexer;

public class Token {
    public int tokType;
    public String value;
    public String stringDelim;

    public Token(int tokType, String value) {
        this.tokType = tokType;
        this.value = value;
    }

    @Override
    public String toString() {
        String ss = value == null ? "" : value;
        return String.format("%d %s", tokType, ss);
    }
}
