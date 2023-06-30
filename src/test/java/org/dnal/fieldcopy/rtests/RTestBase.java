package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.TestBase;
import org.dnal.fieldcopy.builder.SpecBuilder1;
import org.dnal.fieldcopy.codegen.JavaSrcSpec;
import org.dnal.fieldcopy.converter.FCRegistry;
import org.dnal.fieldcopy.dataclass.AllPrims1;
import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.FieldSpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.group.ConverterBodyGenerator;
import org.dnal.fieldcopy.mlexer.DottedFieldBuilder;
import org.dnal.fieldcopy.newcodegen.FieldSpecBuilder;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.testhelpers.BodyGeneratorTestHelper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

/**
 *
 */

public class RTestBase extends TestBase {

    //--for debugging only
    @Test
    public void testDebug() {
    }


    //============
    protected SpecBuilder1 specBuilder = new SpecBuilder1();
    protected JavaSrcSpec currentSrcSpec;
    protected FieldCopyOptions options = new FieldCopyOptions();
    protected ObjectConverter customConverter1;
    protected String usingConverterName; //for using()

    protected List<String> doGen(CopySpec spec) {
        return doGen(spec, null);
    }
    protected List<String> doGen(CopySpec spec, CopySpec spec2) {
        //we want to use implicit conversions so use ConverterBodyGenerator
        options.outputFieldCommentFlag = false;

        ConverterBodyGenerator bodyGenerator;
        if (usingConverterName != null) {
            FCRegistry namedConverters = new FCRegistry();
            namedConverters.addNamed(customConverter1, usingConverterName);
            bodyGenerator = BodyGeneratorTestHelper.createBodyGenerator(customConverter1, options, namedConverters);
        } else {
            bodyGenerator = BodyGeneratorTestHelper.createBodyGenerator(customConverter1, options, new FCRegistry());
        }

        List<CopySpec> specs = new ArrayList<>();
        specs.add(spec);
        if (spec2 != null) {
            specs.add(spec2);
        }

//        CodeGenerator codegen = new CodeGenerator();
//        codegen.setOutputFieldCommentFlag(false);
        JavaSrcSpec srcSpec = bodyGenerator.doGen(spec, specs);
        currentSrcSpec = srcSpec;

        List<String> lines = srcSpec.lines;
        return lines;
    }


    protected CopySpec buildSpec(int n) {
        return specBuilder.buildSpec(n);
    }

    protected List<String> buildAndGen(String valueStr, String fieldName) {
        CopySpec spec = new CopySpec(AllPrims1.class, AllPrims1.class);
        specBuilder.addValue(spec, valueStr, fieldName);
        List<String> lines = doGen(spec);
        return lines;
    }

    protected List<String> buildAndGenForDate(String valueStr, String fieldName) {
        CopySpec spec = new CopySpec(Customer.class, Customer.class);
        specBuilder.addField(spec, valueStr, fieldName);
        List<String> lines = doGen(spec);
        return lines;
    }
    protected CopySpec buildValueForDate(String valueStr, String fieldName) {
        CopySpec spec = new CopySpec(Customer.class, Customer.class);
        specBuilder.addValue(spec, valueStr, fieldName);
        return spec;
    }
    protected List<String> buildValueAndGenForDate(String valueStr, String fieldName) {
        CopySpec spec = new CopySpec(Customer.class, Customer.class);
        specBuilder.addValue(spec, valueStr, fieldName);
        List<String> lines = doGen(spec);
        return lines;
    }

    protected CopySpec buildSpec(Class<?> srcClass, Class<?> destClass, String srcText, String destText) {
        CopySpec spec = new CopySpec(srcClass, destClass);
        SpecBuilder1 specBuilder1 = new SpecBuilder1();
        specBuilder1.addField(spec, srcText, destText);
        JavaSrcSpec srcSpec = new JavaSrcSpec("SomeClass");
        FieldSpecBuilder fieldSpecBuilder = new FieldSpecBuilder(options);
        for (FieldSpec field : spec.fields) {
            fieldSpecBuilder.buildFieldSpec(field, srcSpec);
        }

        return spec;
    }

    protected CopySpec buildSpecValue(Class<?> srcClass, Class<?> destClass, String srcText, String destText) {
        CopySpec spec = new CopySpec(srcClass, destClass);
        SpecBuilder1 specBuilder1 = new SpecBuilder1();
        specBuilder1.addValue(spec, srcText, destText);
        JavaSrcSpec srcSpec = new JavaSrcSpec("SomeClass");
        FieldSpecBuilder fieldSpecBuilder = new FieldSpecBuilder(options);
        for (FieldSpec field : spec.fields) {
            fieldSpecBuilder.buildFieldSpec(field, srcSpec);
        }

        return spec;
    }
    protected CopySpec buildWithField(Class<?> srcClass, Class<?> destClass, String srcText, String destText) {
        CopySpec spec = new CopySpec(srcClass, destClass);
        specBuilder.addField(spec, srcText, destText);
        return spec;
    }

    protected CopySpec buildSpecValueSubObj(Class<?> srcClass, Class<?> destClass, String srcText, String destText) {
        CopySpec spec = new CopySpec(srcClass, destClass);
        SpecBuilder1 specBuilder1 = new SpecBuilder1();
        specBuilder1.addValue(spec, srcText, destText);
        buildDottedFields(spec, 0, srcText, destText);

        JavaSrcSpec srcSpec = new JavaSrcSpec("SomeClass");
        FieldSpecBuilder fieldSpecBuilder = new FieldSpecBuilder(options);
        for (FieldSpec field : spec.fields) {
            fieldSpecBuilder.buildFieldSpec(field, srcSpec);
        }

        return spec;
    }
    protected CopySpec buildSpecFieldSubObj(Class<?> srcClass, Class<?> destClass, String srcText, String destText) {
        CopySpec spec = new CopySpec(srcClass, destClass);
        SpecBuilder1 specBuilder1 = new SpecBuilder1();
        specBuilder1.addField(spec, srcText, destText);
        buildDottedFields(spec, 0, srcText, destText);

        JavaSrcSpec srcSpec = new JavaSrcSpec("SomeClass");
        FieldSpecBuilder fieldSpecBuilder = new FieldSpecBuilder(options);
        for (FieldSpec field : spec.fields) {
            fieldSpecBuilder.buildFieldSpec(field, srcSpec);
        }

        return spec;
    }

    protected void buildDottedFields(CopySpec spec, int i, String srcText, String destText) {
        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(i);

        List<String> srcSubL = new ArrayList<>(Arrays.asList(srcText.split("\\.")));
        List<String> destSubL = new ArrayList<>(Arrays.asList(destText.split("\\.")));
        if (srcSubL.size() > 1 || destSubL.size() > 1) {
            String s1 = srcSubL.remove(0);
            String s2 = destSubL.remove(0);
            nspec.dfBuilder = new DottedFieldBuilder(s1, srcSubL, s2, destSubL);
        }
    }

}
