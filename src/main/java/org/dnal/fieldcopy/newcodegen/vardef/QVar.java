package org.dnal.fieldcopy.newcodegen.vardef;

import org.dnal.fieldcopy.types.FieldTypeInformation;

public class QVar {
    public String varName;
    public FieldTypeInformation fti;

    public QVar(String varName, FieldTypeInformation fti) {
        this.varName = varName;
        this.fti = fti;
    }
}
