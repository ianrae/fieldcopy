package org.dnal.fieldcopy.registry;

import org.dnal.fieldcopy.builder.SpecBuilder1;
import org.dnal.fieldcopy.codegen.JavaSrcSpec;
import org.dnal.fieldcopy.dataclass.Dest1;
import org.dnal.fieldcopy.dataclass.OptionalSrc1;
import org.dnal.fieldcopy.dataclass.Src1;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.FieldSpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.fieldspec.SingleFld;
import org.dnal.fieldcopy.implicitconverter.ImplicitConvRegistry;
import org.dnal.fieldcopy.implicitconverter.ImplicitConvertService;
import org.dnal.fieldcopy.implicitconverter.ImplicitConverter;
import org.dnal.fieldcopy.newcodegen.FieldSpecBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class OptionalTests extends ICRTestBase {

    @Test
    public void testNoConvert() {
        //String -> String so no conversion
        CopySpec spec = buildSpec(OptionalSrc1.class, Dest1.class, "s2", "s2");
        chkConversion(spec, false, true, false);
    }

    @Test
    public void testValToVal() {
        //neither is optional
        CopySpec spec = buildSpec(Src1.class, Dest1.class, "s2", "n1");
        chkConversion(spec, true, false, false);
    }

    @Test
    public void testOptValToVal() {
        CopySpec spec = buildSpec(OptionalSrc1.class, Dest1.class, "s2", "n1");
        chkConversion(spec, true, true, false);
    }

    @Test
    public void testValToOptVal() {
        CopySpec spec = buildSpec(Src1.class, OptionalSrc1.class, "s2", "n1");
        chkConversion(spec, true, false, true);
    }

    @Test
    public void testOptValToOptVal() {
        CopySpec spec = buildSpec(OptionalSrc1.class, OptionalSrc1.class, "s2", "n1");
        chkConversion(spec, true, true, true);
    }

    //============
    private void chkConversion(CopySpec spec, boolean canConvert, boolean srcIsOptional, boolean destIsOptional) {
        ImplicitConvRegistry implicitConvRegistry = createImplicitConvRegistry();
        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(0);

        ImplicitConvertService implicitConvertService = new ImplicitConvertService();
        SingleFld srcFld = nspec.srcFldX.getFirst();
        SingleFld destFld = nspec.destFldX.getFirst();
        List<ImplicitConverter> convL = new ArrayList<>();
        boolean b = implicitConvertService.isConversionSupported(implicitConvRegistry, srcFld, destFld, convL);

        assertEquals(canConvert, b);
        assertEquals(srcIsOptional, srcFld.fieldTypeInfo.isOptional());
        assertEquals(destIsOptional, destFld.fieldTypeInfo.isOptional());
    }

    private CopySpec buildSpec(Class<?> srcClass, Class<?> destClass, String srcText, String destText) {
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
}
