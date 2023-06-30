package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.dataclass.OptCategory;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.fieldspec.SingleFld;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * R1300 custom object converter
 * -define 0,1,more
 * -define one and then override with another one
 * -gets used for cust.inner1
 */
public class R600SubObjConverterTests extends RTestBase {

    @Test
    public void test0() {
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "code1", "code1");
        setCustom(spec);
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = src.getCode1();",
                "String tmp2 = convertCode1(tmp1, src, dest, ctx);",
                "dest.setCode1(tmp2);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }


    @Test
    public void test1() {
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.region.code", "code1");
        setCustom(spec);
        List<String> lines = doGen(spec);

        String[] ar = {
                "OptProduct tmp1 = src.getProd();",
                "Region tmp2 = tmp1.getRegion();",
                "String tmp3 = tmp2.getCode();",
                "String tmp4 = convertProd_region_code(tmp3, src, dest, ctx);",
                "dest.setCode1(tmp4);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void testOpt1() {
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "OptProd.region.code", "code1");
        setCustom(spec);
        List<String> lines = doGen(spec);

        String[] ar = {
                "Optional<OptProduct> tmp1 = src.getOptProd();",
                "Region tmp2 = (ctx.isNullOrEmpty(tmp1)) ? null : tmp1.get().getRegion();",
                "String tmp3 = tmp2.getCode();",
                "String tmp4 = convertOptProd_region_code(tmp3, src, dest, ctx);",
                "dest.setCode1(tmp4);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    //----------
    private NormalFieldSpec setCustom(CopySpec spec) {
        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(0);
        nspec.isCustom = true;
        return nspec;
    }

}
