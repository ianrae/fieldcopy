package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.dataclass.Dest1;
import org.dnal.fieldcopy.dataclass.OptionalSrc1;
import org.dnal.fieldcopy.dataclass.Src1;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * R250 implicit conversion when src or dest is optional
 * alltypes
 * list
 * dates...
 * enum
 */
public class R300ConversionOptionalTests extends RTestBase {
    @Test
    public void testValToVal() {
        //neith are optional
        CopySpec spec = buildSpec(Src1.class, Dest1.class, "n1", "s2");

        List<String> lines = doGen(spec);
        String[] ar = {
                "int tmp1 = src.getN1();",
                "String tmp2 = Integer.valueOf(tmp1).toString();",
                "dest.setS2(tmp2);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testOptValToVal() {
        CopySpec spec = buildSpec(OptionalSrc1.class, Dest1.class, "n1", "s2");

        List<String> lines = doGen(spec);
        String[] ar = {
                "Optional<Integer> tmp1 = src.n1;",
                "String tmp2 = tmp1.get().toString();",
                "dest.setS2(tmp2);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testValToOptVal() {
        CopySpec spec = buildSpec(Src1.class, OptionalSrc1.class, "n1", "s2");

        List<String> lines = doGen(spec);
        String[] ar = {
                "int tmp1 = src.getN1();",
                "String tmp2 = Integer.valueOf(tmp1).toString();",
                "dest.s2 = Optional.ofNullable(tmp2);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testOptValToOptVal() {
        CopySpec spec = buildSpec(OptionalSrc1.class, OptionalSrc1.class, "n1", "s2");

        List<String> lines = doGen(spec);
        String[] ar = {
                "Optional<Integer> tmp1 = src.n1;",
                "String tmp2 = tmp1.get().toString();",
                "dest.s2 = Optional.ofNullable(tmp2);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    //TODO
    //list
    //enum
    //Dates


    //--for debugging only
    @Test
    public void testDebug() {

    }


    //============
}
