package org.dnal.fieldcopy.builder;

import org.dnal.fieldcopy.dataclass.AllPrims1;
import org.dnal.fieldcopy.dataclass.AllScalars1;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.types.JavaPrimitive;

import java.util.Locale;

public class PrimsBuilder {

    public CopySpec buildSpec(JavaPrimitive prim) {
        Class<?> srcClass = AllPrims1.class;
        Class<?> destClass = AllPrims1.class;

        String fieldName = String.format("_%s", JavaPrimitive.lowify(prim));
        CopySpec spec = new CopySpec(srcClass, destClass);
        NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, fieldName, fieldName);
        spec.fields.add(fspec);

        return spec;
    }

    public CopySpec buildSpecScalars(JavaPrimitive prim) {
        Class<?> srcClass = AllScalars1.class;
        Class<?> destClass = AllScalars1.class;

        String fieldName = String.format("_%s", JavaPrimitive.lowify(prim));
        CopySpec spec = new CopySpec(srcClass, destClass);
        NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, fieldName, fieldName);
        spec.fields.add(fspec);

        return spec;
    }
}
