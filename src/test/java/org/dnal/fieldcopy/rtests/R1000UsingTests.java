package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.dataclass.Parent1;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * -using(myConverter) on a field in the json
 * -used when we have multiple possible converters and we're saying which one
 */
public class R1000UsingTests extends RTestBase {

    @Test
    public void testValue() {
        CopySpec spec = buildSpecValueSubObj(Parent1.class, Parent1.class, "true", "child1.flag1");
        setUsing(spec, "MyConv1"); //converter name
        List<String> lines = doGen(spec);

        String[] ar = {
                "boolean tmp1 = true;",
                "ObjectConverter<boolean,boolean> conv2 = ctx.locate(boolean.class, boolean.class, \"MyConv1\");",
                "boolean tmp3 = conv2.convert(tmp1, null, ctx);",
                "Child1 tmp4 = (dest.getChild1() == null) ? new Child1() : dest.getChild1();",
                "dest.setChild1(tmp4);",
                "tmp4.setFlag1(tmp3);",};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Child1");
    }

    @Test
    public void testFail() {
        CopySpec spec = buildSpecValueSubObj(Parent1.class, Parent1.class, "true", "child1.flag1");
        setUsing(spec, "MyConv1"); //converter name
        customConverter1 = null;
        usingConverterName = null;

        FieldCopyException thrown = Assertions.assertThrows(FieldCopyException.class, () -> {
            List<String> lines = doGen(spec);
        });
        chkException(thrown, "using(MyConv1) specified but cannot find named converter 'MyConv1'");
    }

    //----------
    private void setUsing(CopySpec spec, String usingVal) {
        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(0);
        nspec.usingConverterName = usingVal;

        customConverter1 = new R1300CustomConverterTests.MyCustConverter3();
        usingConverterName = usingVal;
    }

}
