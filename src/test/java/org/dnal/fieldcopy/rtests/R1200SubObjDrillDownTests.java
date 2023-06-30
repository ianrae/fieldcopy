package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.dataclass.OptCategory;
import org.dnal.fieldcopy.dataclass.OptProduct;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * R1200 sub-obj drill-down
 * addr.city -> field
 * field -> addr.city
 * addr.city -> addr.city
 * -list,enum,date
 * -null src value handled correctly
 * -null intermediate value handled correctly
 */
public class R1200SubObjDrillDownTests extends RTestBase {

    @Test
    public void test1() {
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.region.code", "code1");
        List<String> lines = doGen(spec);

        //with with src subobjects we implicitly assume they exist eg src.prod.region are not null (see skipNull)

        String[] ar = {
                "OptProduct tmp1 = src.getProd();",
                "Region tmp2 = tmp1.getRegion();",
                "String tmp3 = tmp2.getCode();",
                "dest.setCode1(tmp3);"};

        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void test2() {
        CopySpec spec = buildSpecFieldSubObj(OptProduct.class, OptCategory.class, "region.code", "code1");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Region tmp1 = src.getRegion();",
                "String tmp2 = tmp1.getCode();",
                "dest.setCode1(tmp2);"};

        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void test1Opt1() {
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "optProd.region.code", "code1");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Optional<OptProduct> tmp1 = src.getOptProd();",
                "Region tmp2 = (ctx.isNullOrEmpty(tmp1)) ? null : tmp1.get().getRegion();",
                "String tmp3 = tmp2.getCode();",
                "dest.setCode1(tmp3);"};

        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void test1Opt2() {
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "optProd.optRegion.code", "code1");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Optional<OptProduct> tmp1 = src.getOptProd();",
                "Optional<Region> tmp2 = (ctx.isNullOrEmpty(tmp1)) ? Optional.empty() : tmp1.get().getOptRegion();",
                "String tmp3 = (ctx.isNullOrEmpty(tmp2)) ? null : tmp2.get().getCode();",
                "dest.setCode1(tmp3);"};

        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void test1Opt3() {
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "optProd.optRegion.optCode", "code1");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Optional<OptProduct> tmp1 = src.getOptProd();",
                "Optional<Region> tmp2 = (ctx.isNullOrEmpty(tmp1)) ? Optional.empty() : tmp1.get().getOptRegion();",
                "Optional<String> tmp3 = (ctx.isNullOrEmpty(tmp2)) ? Optional.empty() : tmp2.get().getOptCode();",
                "dest.setCode1((ctx.isNullOrEmpty(tmp3)) ? null : tmp3.orElse(null));"};

        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }


    //and now the other way
    @Test
    public void testOther1() {
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "code1", "prod.region.code");
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = src.getCode1();",
                "OptProduct tmp2 = (dest.getProd() == null) ? new OptProduct() : dest.getProd();",
                "Region tmp3 = (tmp2.getRegion() == null) ? new Region() : tmp2.getRegion();",
                "dest.setProd(tmp2);",
                "tmp2.setRegion(tmp3);",
                "tmp3.setCode(tmp1);",};

        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void testOther2() {
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptProduct.class, "code1", "region.code");
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = src.getCode1();",
                "Region tmp2 = (dest.getRegion() == null) ? new Region() : dest.getRegion();",
                "dest.setRegion(tmp2);",
                "tmp2.setCode(tmp1);"};

        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void testOther1Opt1() {
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "code1", "optProd.region.code");
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = src.getCode1();",
                "Optional<OptProduct> tmp2 = (ctx.isNullOrEmpty(dest.getOptProd())) ? Optional.of(new OptProduct()) : dest.getOptProd();",
                "Region tmp3 = (tmp2.get().getRegion() == null) ? new Region() : tmp2.get().getRegion();",
                "dest.setOptProd(tmp2);",
                "tmp2.get().setRegion(tmp3);",
                "tmp3.setCode(tmp1);",
        };

        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void testOther1Opt2() {
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "code1", "optProd.optRegion.code");
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = src.getCode1();",
                "Optional<OptProduct> tmp2 = (ctx.isNullOrEmpty(dest.getOptProd())) ? Optional.of(new OptProduct()) : dest.getOptProd();",
                "Optional<Region> tmp3 = (ctx.isNullOrEmpty(tmp2.get().getOptRegion())) ? Optional.of(new Region()) : tmp2.get().getOptRegion();",
                "dest.setOptProd(tmp2);",
                "tmp2.get().setOptRegion(tmp3);",
                "tmp3.get().setCode(tmp1);"};

        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void testOther1Opt3() {
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "code1", "optProd.optRegion.optCode");
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = src.getCode1();",
                "Optional<OptProduct> tmp2 = (ctx.isNullOrEmpty(dest.getOptProd())) ? Optional.of(new OptProduct()) : dest.getOptProd();",
                "Optional<Region> tmp3 = (ctx.isNullOrEmpty(tmp2.get().getOptRegion())) ? Optional.of(new Region()) : tmp2.get().getOptRegion();",
                "dest.setOptProd(tmp2);",
                "tmp2.get().setOptRegion(tmp3);",
                "tmp3.get().setOptCode(Optional.ofNullable(tmp1));"};

        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }


    //----------

}
