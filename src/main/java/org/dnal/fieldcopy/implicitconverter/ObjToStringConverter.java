package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.types.JavaPrimitive;

public class ObjToStringConverter implements ImplicitConverter {

    public ObjToStringConverter() {
    }

    @Override
    public String gen(String varName) {
        //        s = xss.toString();

        return String.format("%s.toString()", varName);
    }
}
