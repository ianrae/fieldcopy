package org.dnal.fieldcopy.newcodegen.vardef;

import org.dnal.fieldcopy.types.FieldTypeInformation;

public class QField {
    public String fieldName;
    public FieldTypeInformation fti;
    public boolean useIsGetter;
    public boolean isPublicField;

    public QField(String fieldName, FieldTypeInformation fti, boolean useIsGetter, boolean isPublicField) {
        this.fieldName = fieldName;
        this.fti = fti;
        this.useIsGetter = useIsGetter;
        this.isPublicField = isPublicField;
    }
}
