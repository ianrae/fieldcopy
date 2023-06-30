package org.dnal.fieldcopy.builder;


import org.dnal.fieldcopy.dataclass.Dest1;
import org.dnal.fieldcopy.dataclass.Src1;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.mlexer.DottedFieldBuilder;

import java.util.Arrays;
import java.util.Collections;

public class SpecBuilder1 {

    public CopySpec buildSpec(int n) {
        Class<?> srcClass = Src1.class;
        Class<?> destClass = Dest1.class;

        CopySpec spec = new CopySpec(srcClass, destClass);
        if (n >= 1) {
            NormalFieldSpec nspec = new NormalFieldSpec(srcClass, destClass, "n1", "n1");
            spec.fields.add(nspec);
        }
        if (n >= 2) {
            NormalFieldSpec nspec = new NormalFieldSpec(srcClass, destClass, "s2", "s2");
            spec.fields.add(nspec);
        }
        if (n >= 3) {
            NormalFieldSpec nspec = new NormalFieldSpec(srcClass, destClass, "inner1.s3", "s2");
            nspec.dfBuilder = new DottedFieldBuilder("inner1", Arrays.asList("s3"), "s2", Collections.emptyList());
            spec.fields.add(nspec);
        }

        return spec;
    }

    public CopySpec addCustom(CopySpec spec) {
        NormalFieldSpec fspec = new NormalFieldSpec(spec.srcClass, spec.destClass, "n1", "n1");
        fspec.isCustom = true;
        spec.fields.add(fspec);

        return spec;
    }
    public CopySpec addAuto(CopySpec spec) {
        spec.autoFlag = true;
        return spec;
    }

    public CopySpec addEnum(CopySpec spec) {
        Class<?> srcClass = spec.srcClass;
        Class<?> destClass = spec.destClass;

        NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "col1", "col1");
        spec.fields.add(fspec);

        return spec;
    }

    public CopySpec addEnumToString(CopySpec spec) {
        Class<?> srcClass = spec.srcClass;
        Class<?> destClass = spec.destClass;

        NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "col1", "s2");
        spec.fields.add(fspec);

        return spec;
    }
    public CopySpec addStringToEnum(CopySpec spec) {
        Class<?> srcClass = spec.srcClass;
        Class<?> destClass = spec.destClass;

        NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "s2", "col1");
        spec.fields.add(fspec);

        return spec;
    }

    public CopySpec addObject(CopySpec spec) {
        Class<?> srcClass = spec.srcClass;
        Class<?> destClass = spec.destClass;

        NormalFieldSpec nspec = new NormalFieldSpec(srcClass, destClass, "inner1", "inner1");
        spec.fields.add(nspec);

        return spec;
    }
    public CopySpec addValue(CopySpec spec, String valueStr, String destText) {
        Class<?> srcClass = spec.srcClass;
        Class<?> destClass = spec.destClass;

        NormalFieldSpec nspec = new NormalFieldSpec(srcClass, destClass, valueStr, destText);
        nspec.srcTextIsValue = true;
        spec.fields.add(nspec);

        return spec;
    }
    public CopySpec addField(CopySpec spec, String valueStr, String destText) {
        Class<?> srcClass = spec.srcClass;
        Class<?> destClass = spec.destClass;

        NormalFieldSpec nspec = new NormalFieldSpec(srcClass, destClass, valueStr, destText);
        spec.fields.add(nspec);

        return spec;
    }

    public CopySpec buildSpecWithInner() {
        Class<?> srcClass = Src1.class;
        Class<?> destClass = Dest1.class;

        NormalFieldSpec nspec = new NormalFieldSpec(srcClass, destClass, "inner1.s3", "s2");
        nspec.dfBuilder = new DottedFieldBuilder("inner1", Arrays.asList("s3"), "s2", Collections.emptyList());

        CopySpec spec = new CopySpec(srcClass, destClass);
        spec.fields.add(nspec);
        return spec;
    }

    public CopySpec buildConversionSpec(int n) {
        Class<?> srcClass = Src1.class;
        Class<?> destClass = Dest1.class;

        CopySpec spec = new CopySpec(srcClass, destClass);
        if (n >= 1) {
            NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "s2", "n1");
            spec.fields.add(fspec);
        }
        if (n >= 2) {
            NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "n1", "s2");
            spec.fields.add(fspec);
        }

        return spec;
    }

    public CopySpec buildSpecWithImplicitConv() {
        Class<?> srcClass = Src1.class;
        Class<?> destClass = Dest1.class;

        CopySpec spec = new CopySpec(srcClass, destClass);
        NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "n1", "s2");
        spec.fields.add(fspec);

        return spec;
    }

    public CopySpec buildSpecForUnsupportedConversion() {
        Class<?> srcClass = Src1.class;
        Class<?> destClass = Dest1.class;

        CopySpec spec = new CopySpec(srcClass, destClass);
        NormalFieldSpec fspec = new NormalFieldSpec(srcClass, destClass, "n1", "inner1");
        spec.fields.add(fspec);

        return spec;
    }
}
