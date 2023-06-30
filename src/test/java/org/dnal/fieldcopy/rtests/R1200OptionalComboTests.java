package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.dataclass.OptCategory;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * R1200 sub-obj optional combination
 * -do all 8 combos of a.b.c -> a.b.c for both optional and not
 */
public class R1200OptionalComboTests extends RTestBase {

    // --- part 1: values ---
    @Test
    public void testValue1() {
        CopySpec spec = buildSpecValueSubObj(OptCategory.class, OptCategory.class, "'abc'", "prod.region.code");
        List<String> lines = doGen(spec);

//        OptCategory src = null;
//        OptCategory dest = null;
//        String tmp1 = "abc'";
//        OptProduct tmp2 = (dest.getProd() == null) ? new OptProduct() : dest.getProd();
//        Region tmp3 = (tmp2.getRegion() == null) ? new Region() : tmp2.getRegion();
//        dest.setProd(tmp2);
//        tmp2.setRegion(tmp3);
//        tmp3.setCode(tmp1);

        String[] ar = {
                "String tmp1 = \"abc\";",
                "OptProduct tmp2 = (dest.getProd() == null) ? new OptProduct() : dest.getProd();",
                "Region tmp3 = (tmp2.getRegion() == null) ? new Region() : tmp2.getRegion();",
                "dest.setProd(tmp2);",
                "tmp2.setRegion(tmp3);",
                "tmp3.setCode(tmp1);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void testValue2() {
        CopySpec spec = buildSpecValueSubObj(OptCategory.class, OptCategory.class, "'abc'", "prod.optRegion.code");
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = \"abc\";",
                "OptProduct tmp2 = (dest.getProd() == null) ? new OptProduct() : dest.getProd();",
                "Optional<Region> tmp3 = (ctx.isNullOrEmpty(tmp2.getOptRegion())) ? Optional.of(new Region()) : tmp2.getOptRegion();",
                "dest.setProd(tmp2);",
                "tmp2.setOptRegion(tmp3);",
                "tmp3.get().setCode(tmp1);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    // --- part 2: 8 combinations ---
    @Test
    public void testCombo1() {
        // F F F
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.region.code", "prod.region.code");
        List<String> lines = doGen(spec);

        String[] ar = {
                "OptProduct tmp1 = src.getProd();",
                "Region tmp2 = tmp1.getRegion();",
                "String tmp3 = tmp2.getCode();",
                "OptProduct tmp4 = (dest.getProd() == null) ? new OptProduct() : dest.getProd();",
                "Region tmp5 = (tmp4.getRegion() == null) ? new Region() : tmp4.getRegion();",
                "dest.setProd(tmp4);",
                "tmp4.setRegion(tmp5);",
                "tmp5.setCode(tmp3);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void testCombo2() {
        // F F T
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.region.code", "prod.region.optCode");
        List<String> lines = doGen(spec);

        String[] ar = {
                "OptProduct tmp1 = src.getProd();",
                "Region tmp2 = tmp1.getRegion();",
                "String tmp3 = tmp2.getCode();",
                "OptProduct tmp4 = (dest.getProd() == null) ? new OptProduct() : dest.getProd();",
                "Region tmp5 = (tmp4.getRegion() == null) ? new Region() : tmp4.getRegion();",
                "dest.setProd(tmp4);",
                "tmp4.setRegion(tmp5);",
                "tmp5.setOptCode(Optional.ofNullable(tmp3));"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void testCombo3() {
        // F T F
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.region.code", "prod.optRegion.code");
        List<String> lines = doGen(spec);

        String[] ar = {
                "OptProduct tmp1 = src.getProd();",
                "Region tmp2 = tmp1.getRegion();",
                "String tmp3 = tmp2.getCode();",
                "OptProduct tmp4 = (dest.getProd() == null) ? new OptProduct() : dest.getProd();",
                "Optional<Region> tmp5 = (ctx.isNullOrEmpty(tmp4.getOptRegion())) ? Optional.of(new Region()) : tmp4.getOptRegion();",
                "dest.setProd(tmp4);",
                "tmp4.setOptRegion(tmp5);",
                "tmp5.get().setCode(tmp3);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void testCombo4() {
        // F T T
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.region.code", "prod.optRegion.optCode");
        List<String> lines = doGen(spec);

        String[] ar = {
                "OptProduct tmp1 = src.getProd();",
                "Region tmp2 = tmp1.getRegion();",
                "String tmp3 = tmp2.getCode();",
                "OptProduct tmp4 = (dest.getProd() == null) ? new OptProduct() : dest.getProd();",
                "Optional<Region> tmp5 = (ctx.isNullOrEmpty(tmp4.getOptRegion())) ? Optional.of(new Region()) : tmp4.getOptRegion();",
                "dest.setProd(tmp4);",
                "tmp4.setOptRegion(tmp5);",
                "tmp5.get().setOptCode(Optional.ofNullable(tmp3));"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void testCombo5() {
        // T F F
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.region.code", "optProd.region.code");
        List<String> lines = doGen(spec);

        String[] ar = {
                "OptProduct tmp1 = src.getProd();",
                "Region tmp2 = tmp1.getRegion();",
                "String tmp3 = tmp2.getCode();",
                "Optional<OptProduct> tmp4 = (ctx.isNullOrEmpty(dest.getOptProd())) ? Optional.of(new OptProduct()) : dest.getOptProd();",
                "Region tmp5 = (tmp4.get().getRegion() == null) ? new Region() : tmp4.get().getRegion();",
                "dest.setOptProd(tmp4);",
                "tmp4.get().setRegion(tmp5);",
                "tmp5.setCode(tmp3);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void testCombo6() {
        // T F T
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.region.code", "optProd.region.optCode");
        List<String> lines = doGen(spec);

        String[] ar = {
                "OptProduct tmp1 = src.getProd();",
                "Region tmp2 = tmp1.getRegion();",
                "String tmp3 = tmp2.getCode();",
                "Optional<OptProduct> tmp4 = (ctx.isNullOrEmpty(dest.getOptProd())) ? Optional.of(new OptProduct()) : dest.getOptProd();",
                "Region tmp5 = (tmp4.get().getRegion() == null) ? new Region() : tmp4.get().getRegion();",
                "dest.setOptProd(tmp4);",
                "tmp4.get().setRegion(tmp5);",
                "tmp5.setOptCode(Optional.ofNullable(tmp3));"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void testCombo7() {
        // T T F
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.region.code", "optProd.optRegion.code");
        List<String> lines = doGen(spec);

        String[] ar = {
                "OptProduct tmp1 = src.getProd();",
                "Region tmp2 = tmp1.getRegion();",
                "String tmp3 = tmp2.getCode();",
                "Optional<OptProduct> tmp4 = (ctx.isNullOrEmpty(dest.getOptProd())) ? Optional.of(new OptProduct()) : dest.getOptProd();",
                "Optional<Region> tmp5 = (ctx.isNullOrEmpty(tmp4.get().getOptRegion())) ? Optional.of(new Region()) : tmp4.get().getOptRegion();",
                "dest.setOptProd(tmp4);",
                "tmp4.get().setOptRegion(tmp5);",
                "tmp5.get().setCode(tmp3);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void testCombo8() {
        // T T T
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.region.code", "optProd.optRegion.optCode");
        List<String> lines = doGen(spec);

        String[] ar = {
                "OptProduct tmp1 = src.getProd();",
                "Region tmp2 = tmp1.getRegion();",
                "String tmp3 = tmp2.getCode();",
                "Optional<OptProduct> tmp4 = (ctx.isNullOrEmpty(dest.getOptProd())) ? Optional.of(new OptProduct()) : dest.getOptProd();",
                "Optional<Region> tmp5 = (ctx.isNullOrEmpty(tmp4.get().getOptRegion())) ? Optional.of(new Region()) : tmp4.get().getOptRegion();",
                "dest.setOptProd(tmp4);",
                "tmp4.get().setOptRegion(tmp5);",
                "tmp5.get().setOptCode(Optional.ofNullable(tmp3));"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }


    //various src combinations
    @Test
    public void testSrcCombo1A() {
        // F T F -> F T F
        // F T F -> F F F
        // T F F -> T F F
        // T F F -> F F F
        // T T T -> T T T
        //       -> F F F

        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.optRegion.code", "prod.optRegion.code");
        List<String> lines = doGen(spec);

        String[] ar = {
                "OptProduct tmp1 = src.getProd();",
                "Optional<Region> tmp2 = tmp1.getOptRegion();",
                "String tmp3 = (ctx.isNullOrEmpty(tmp2)) ? null : tmp2.get().getCode();",
                "OptProduct tmp4 = (dest.getProd() == null) ? new OptProduct() : dest.getProd();",
                "Optional<Region> tmp5 = (ctx.isNullOrEmpty(tmp4.getOptRegion())) ? Optional.of(new Region()) : tmp4.getOptRegion();",
                "dest.setProd(tmp4);",
                "tmp4.setOptRegion(tmp5);",
                "tmp5.get().setCode(tmp3);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void testSrcCombo1B() {
        // F T F -> F F F
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "prod.optRegion.code", "prod.region.code");
        List<String> lines = doGen(spec);

        String[] ar = {
                "OptProduct tmp1 = src.getProd();",
                "Optional<Region> tmp2 = tmp1.getOptRegion();",
                "String tmp3 = (ctx.isNullOrEmpty(tmp2)) ? null : tmp2.get().getCode();",
                "OptProduct tmp4 = (dest.getProd() == null) ? new OptProduct() : dest.getProd();",
                "Region tmp5 = (tmp4.getRegion() == null) ? new Region() : tmp4.getRegion();",
                "dest.setProd(tmp4);",
                "tmp4.setRegion(tmp5);",
                "tmp5.setCode(tmp3);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void testSrcCombo2A() {
        // T F F -> T F F
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "optProd.region.code", "optProd.region.code");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Optional<OptProduct> tmp1 = src.getOptProd();",
                "Region tmp2 = (ctx.isNullOrEmpty(tmp1)) ? null : tmp1.get().getRegion();",
                "String tmp3 = tmp2.getCode();",
                "Optional<OptProduct> tmp4 = (ctx.isNullOrEmpty(dest.getOptProd())) ? Optional.of(new OptProduct()) : dest.getOptProd();",
                "Region tmp5 = (tmp4.get().getRegion() == null) ? new Region() : tmp4.get().getRegion();",
                "dest.setOptProd(tmp4);",
                "tmp4.get().setRegion(tmp5);",
                "tmp5.setCode(tmp3);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void testSrcCombo2B() {
        // T F F -> F F F
        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "optProd.region.code", "prod.region.code");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Optional<OptProduct> tmp1 = src.getOptProd();",
                "Region tmp2 = (ctx.isNullOrEmpty(tmp1)) ? null : tmp1.get().getRegion();",
                "String tmp3 = tmp2.getCode();",
                "OptProduct tmp4 = (dest.getProd() == null) ? new OptProduct() : dest.getProd();",
                "Region tmp5 = (tmp4.getRegion() == null) ? new Region() : tmp4.getRegion();",
                "dest.setProd(tmp4);",
                "tmp4.setRegion(tmp5);",
                "tmp5.setCode(tmp3);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    @Test
    public void testSrcCombo3A() {
        // T T T -> T T T

        CopySpec spec = buildSpecFieldSubObj(OptCategory.class, OptCategory.class, "optProd.optRegion.optCode", "optProd.optRegion.optCode");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Optional<OptProduct> tmp1 = src.getOptProd();",
                "Optional<Region> tmp2 = (ctx.isNullOrEmpty(tmp1)) ? Optional.empty() : tmp1.get().getOptRegion();",
                "Optional<String> tmp3 = (ctx.isNullOrEmpty(tmp2)) ? Optional.empty() : tmp2.get().getOptCode();",
                "Optional<OptProduct> tmp4 = (ctx.isNullOrEmpty(dest.getOptProd())) ? Optional.of(new OptProduct()) : dest.getOptProd();",
                "Optional<Region> tmp5 = (ctx.isNullOrEmpty(tmp4.get().getOptRegion())) ? Optional.of(new Region()) : tmp4.get().getOptRegion();",
                "dest.setOptProd(tmp4);",
                "tmp4.get().setOptRegion(tmp5);",
                "tmp5.get().setOptCode(tmp3);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.OptProduct", "org.dnal.fieldcopy.dataclass.Region");
    }

    //----------

}
