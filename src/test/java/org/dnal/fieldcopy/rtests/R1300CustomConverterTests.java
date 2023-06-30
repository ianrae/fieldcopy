package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.Converter;
import org.dnal.fieldcopy.ConverterGroup;
import org.dnal.fieldcopy.FieldCopy;
import org.dnal.fieldcopy.dataclass.OptionalSrc1;
import org.dnal.fieldcopy.dataclass.Src1;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.runtime.ObjectConverterBase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
     R1300 custom object converter
 -define 0,1,more
 -define one and then override with another one
 -gets used for cust.inner1
 */
public class R1300CustomConverterTests extends RTestBase {

    public static class MyCustConverter3 extends ObjectConverterBase {
        public MyCustConverter3() {
            super(String.class, Integer.class);
        }
        @Override
        public Object convert(Object src, Object dest, ConverterContext ctx) {
            String s = (String) src;
            Integer nn = Integer.parseInt(s);
            return nn;
        }
    }

    public static class MyGroup3 implements ConverterGroup {
        @Override
        public List<ObjectConverter> getConverters() {
            List<ObjectConverter> list = Arrays.asList(new MyCustConverter3());
            return list;
        }
    }


    @Test
    public void test() {
        CopySpec spec = buildWithField(Src1.class, Src1.class, "s2", "n2");
        customConverter1 = new MyCustConverter3();
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = src.getS2();",
                "if (tmp1 != null) {",
                "ObjectConverter<String,Integer> conv2 = ctx.locate(String.class, Integer.class);",
                "Integer tmp3 = conv2.convert(tmp1, null, ctx);",
                "dest.setN2(tmp3);",
                "}"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testDestOpt() {
        CopySpec spec = buildWithField(Src1.class, OptionalSrc1.class, "s2", "n1");
        customConverter1 = new MyCustConverter3();
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = src.getS2();",
                "if (tmp1 != null) {",
                "ObjectConverter<String,Integer> conv2 = ctx.locate(String.class, Integer.class);",
                "Integer tmp3 = conv2.convert(tmp1, null, ctx);",
                "dest.n1 = Optional.ofNullable(tmp3);",
                "}"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testSrcOpt() {
        CopySpec spec = buildWithField(OptionalSrc1.class, Src1.class, "s2", "n2");
        customConverter1 = new MyCustConverter3();
        List<String> lines = doGen(spec);

        String[] ar = {
                "Optional<String> tmp1 = src.s2;",
                "if (tmp1 != null) {",
                "ObjectConverter<String,Integer> conv2 = ctx.locate(String.class, Integer.class);",
                "Integer tmp3 = conv2.convert(tmp1.get(), null, ctx);",
                "dest.setN2(tmp3);",
                "}"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testOpt() {
        CopySpec spec = buildWithField(OptionalSrc1.class, OptionalSrc1.class, "s2", "n1");
        customConverter1 = new MyCustConverter3();
        List<String> lines = doGen(spec);

        String[] ar = {
                "Optional<String> tmp1 = src.s2;",
                "if (tmp1 != null) {",
                "ObjectConverter<String,Integer> conv2 = ctx.locate(String.class, Integer.class);",
                "Integer tmp3 = conv2.convert(tmp1.get(), null, ctx);",
                "dest.n1 = Optional.ofNullable(tmp3);",
                "}"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testRuntime() {
        MyCustConverter3 conv3 = new MyCustConverter3();
        FieldCopy fc = FieldCopy.with(MyGroup3.class).build();
        Converter<String, Integer> converter = fc.getConverter(conv3.getSourceFieldTypeInfo(), conv3.getDestinationFieldTypeInfo());

        String src = "446";
        Integer dest = 55;

        Integer dest2 = converter.convert(src, dest);

        assertEquals(446, dest2.intValue());
    }

    //----

}
