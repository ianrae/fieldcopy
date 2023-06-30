package org.dnal.fieldcopy.registry;

import org.dnal.fieldcopy.builder.SpecBuilder1;
import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.dataclass.Address;
import org.dnal.fieldcopy.dataclass.Inner1;
import org.dnal.fieldcopy.dataclass.Inner1DTO;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.implicitconverter.ImplicitConvRegistry;
import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class ObjectConverterTests extends ICRTestBase {

    public static class DummyConverter<S, T> implements ObjectConverter<S, T> {
        private Class<S> srcClass;
        private Class<T> destClass;

        public DummyConverter(Class<S> srcClass, Class<T> destClass) {
            this.srcClass = srcClass;
            this.destClass = destClass;
        }


        @Override
        public FieldTypeInformation getSourceFieldTypeInfo() {
            FieldTypeInformation srcInfo = new FieldTypeInformationImpl(srcClass);
            return srcInfo;
        }

        @Override
        public FieldTypeInformation getDestinationFieldTypeInfo() {
            FieldTypeInformation destInfo = new FieldTypeInformationImpl(destClass);
            return destInfo;
        }

        @Override
        public T convert(S src, T dest, ConverterContext ctx) {
            return dest;
        }
    }


    public static class Src1Registry extends ConverterRegistryBase {
        public Src1Registry() {
            ObjectConverter<Inner1, Inner1> conv = new DummyConverter<>(Inner1.class, Inner1.class);
            add(conv);
        }
    }

    @Test
    public void testNoConv() {
        CopySpec spec = specBuilder1.buildSpec(1);
        List<String> lines = doGen(spec);

        String[] ar = {
                "int tmp1 = src.getN1();",
                "dest.setN1(tmp1);"
        };
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testImplicit() {
        CopySpec spec = specBuilder1.buildSpecWithImplicitConv();
        List<String> lines = doGen(spec);

        String[] ar = {
                "int tmp1 = src.getN1();",
                "String tmp2 = Integer.valueOf(tmp1).toString();", //conv
                "dest.setS2(tmp2);"
        };
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
//        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Inner1");
    }

    @Test
    public void testNoConverterError() {
        CopySpec spec = specBuilder1.buildSpecForUnsupportedConversion();

        FieldCopyException thrown = Assertions.assertThrows(FieldCopyException.class, () -> {
            List<String> lines = doGen(spec);
        });
        log(thrown.getMessage());
        chkException(thrown, "Cannot convert 'int n1' to 'Inner1 inner1'");
    }

    @Test
    public void testExplicit() {
        CopySpec spec = specBuilder1.buildSpec(0);
        specBuilder1.addObject(spec);
        List<String> lines = doGen(spec);

        String[] ar = {
                "Inner1 tmp1 = src.getInner1();",
                "if (tmp1 != null) {",
                "ObjectConverter<Inner1,Inner1> conv2 = ctx.locate(Inner1.class, Inner1.class);",
                "Inner1 tmp3 = conv2.convert(tmp1, new Inner1(), ctx);",
                "dest.setInner1(tmp3);",
                "}",
        };
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Inner1");
    }

    @Test
    public void testEnum() {
        CopySpec spec = specBuilder1.buildSpec(0);
        specBuilder1.addEnumToString(spec);
        List<String> lines = doGen(spec);

        String[] ar = {
                "Color tmp1 = src.getCol1();",
                "String tmp2 = tmp1.name();",
                "dest.setS2(tmp2);"
        };
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Color");
    }

    @Test
    public void testStringToEnum() {
        CopySpec spec = specBuilder1.buildSpec(0);
        specBuilder1.addStringToEnum(spec);
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = src.getS2();",
                "Color tmp2 = Enum.valueOf(org.dnal.fieldcopy.dataclass.Color.class, tmp1);",
                "dest.setCol1(tmp2);"
        };
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Color");
    }

    @Test
    public void testCustom() {
        CopySpec spec = specBuilder1.buildSpec(0);
        specBuilder1.addCustom(spec);
        List<String> lines = doGen(spec);

        String[] ar = {
                "int tmp1 = src.getN1();",
                "int tmp2 = convertN1(tmp1, src, dest, ctx);",
                "dest.setN1(tmp2);"
        };
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);

        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(0);
        assertEquals("convertN1", nspec.customMethodName);
        assertEquals("int", nspec.customReturnType);
    }

    @Test
    public void testAuto() {
        CopySpec spec = new CopySpec(Inner1.class, Inner1DTO.class);
        spec.autoFlag = true;
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = src.s3;",
                "dest.s3 = tmp1;"
        };
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testAuto_IsGetter() {
        CopySpec spec = new CopySpec(Address.class, Address.class);
        spec.autoFlag = true;
        List<String> lines = doGen(spec);

        String[] ar = {
                "Customer tmp1 = src.getBackRef();",
                "dest.setBackRef(tmp1);",
                "String tmp2 = src.getCity();",
                "dest.setCity(tmp2);",
                "boolean tmp3 = src.isFlag1();",
                "dest.setFlag1(tmp3);",
                "String tmp4 = src.getStreet1();",
                "dest.setStreet1(tmp4);",
        };
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Customer");
    }

    @Test
    public void testAuto_Exclude() {
        CopySpec spec = new CopySpec(Address.class, Address.class);
        spec.autoFlag = true;
        spec.autoExcludeFields = Arrays.asList("backRef", "city");
        List<String> lines = doGen(spec);

        String[] ar = {
                "boolean tmp1 = src.isFlag1();",
                "dest.setFlag1(tmp1);",
                "String tmp2 = src.getStreet1();",
                "dest.setStreet1(tmp2);",
        };
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testAutoAndNormal() {
        CopySpec spec = new CopySpec(Address.class, Address.class);
        NormalFieldSpec fspec = new NormalFieldSpec(Address.class, Address.class, "street1", "city");
        spec.fields.add(fspec);
        spec.autoFlag = true;
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = src.getStreet1();",
                "dest.setCity(tmp1);",
                "Customer tmp2 = src.getBackRef();",
                "dest.setBackRef(tmp2);",
                "boolean tmp3 = src.isFlag1();",
                "dest.setFlag1(tmp3);"
        };
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Customer");
    }

    @Test
    public void testAutoAndNormalOtherWas() {
        CopySpec spec = new CopySpec(Address.class, Address.class);
        NormalFieldSpec fspec = new NormalFieldSpec(Address.class, Address.class, "city", "street1");
        spec.fields.add(fspec);
        spec.autoFlag = true;
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = src.getCity();",
                "dest.setStreet1(tmp1);",
                "Customer tmp2 = src.getBackRef();",
                "dest.setBackRef(tmp2);",
                "boolean tmp3 = src.isFlag1();",
                "dest.setFlag1(tmp3);"
        };
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Customer");
    }

    //============
    private SpecBuilder1 specBuilder1 = new SpecBuilder1();

    private List<String> doGen(CopySpec spec) {
        Src1Registry registry = new Src1Registry();
        ImplicitConvRegistry implicitConvRegistry = createImplicitConvRegistry();

        return generateCode(spec, implicitConvRegistry, registry);
    }
}
