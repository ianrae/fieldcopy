package org.dnal.fieldcopy.builder;

//import org.delia.util.StringUtil;

import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.dataclass.Dest1;
import org.dnal.fieldcopy.dataclass.OptionalSrc1;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.mlexer.DottedFieldBuilder;

import java.util.Arrays;
import java.util.Collections;

public class SpecBuilder2 {

    public CopySpec buildSpec(int n) {
        Class<?> srcClass = OptionalSrc1.class;
        Class<?> destClass = Dest1.class;

        CopySpec spec = new CopySpec(srcClass, destClass);
        if (n >= 1) {
            NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "s2", "s2");
            spec.fields.add(fspec);
        }
        if (n >= 2) {
            DottedFieldBuilder dfBuilder = new DottedFieldBuilder("inner1", Arrays.asList("s3"),
                    "s2", Collections.emptyList());

            NormalFieldSpec nspec = new NormalFieldSpec(srcClass, destClass, "inner1.s3", "s2");
            nspec.dfBuilder = dfBuilder;
            spec.fields.add(nspec);
        }

        return spec;
    }

//    public CopySpec addEnum(CopySpec spec) {
//        Class<?> srcClass = spec.srcClass;
//        Class<?> destClass = spec.destClass;
//
//        NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "col1", "col1");
//        spec.fields.add(fspec);
//
//        return spec;
//    }
//
//    public CopySpec addObject(CopySpec spec) {
//        Class<?> srcClass = spec.srcClass;
//        Class<?> destClass = spec.destClass;
//
//        NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "inner1", "inner1");
//        spec.fields.add(fspec);
//
//        return spec;
//    }

    public CopySpec buildSpecEx() {
        Class<?> srcClass = OptionalSrc1.class;
        Class<?> destClass = Customer.class;
        CopySpec spec = new CopySpec(srcClass, destClass);

        NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "profiles", "roles");
        spec.fields.add(fspec);

        return spec;
    }

}
