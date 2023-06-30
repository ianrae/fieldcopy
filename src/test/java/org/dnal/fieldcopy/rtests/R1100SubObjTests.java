package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.builder.SpecBuilder1;
import org.dnal.fieldcopy.codegen.JavaSrcSpec;
import org.dnal.fieldcopy.dataclass.*;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.FieldSpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.newcodegen.FieldSpecBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * R1100 sub-obj
 * -addr -> addr
 * -list<addr> -> list<addr>
 * -Opt and NonOpt of addr
 * -uses another converter that you must have defined
 * -null src value handled correctly
 */
public class R1100SubObjTests extends RTestBase {

    @Test
    public void testValuePrim() {
        CopySpec spec = buildSpecValueSubObj(Parent1.class, Parent1.class, "true", "child1.flag1");
        List<String> lines = doGen(spec);

        String[] ar = {
                "boolean tmp1 = true;",
                "Child1 tmp2 = (dest.getChild1() == null) ? new Child1() : dest.getChild1();",
                "dest.setChild1(tmp2);",
                "tmp2.setFlag1(tmp1);",};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Child1");
    }

    @Test
    public void testValuePrimDouble() {
        //should only create new Child1() once
        CopySpec spec = buildSpecValueSubObj(Parent1.class, Parent1.class, "true", "child1.flag1");
        addValueField(spec, "'athens'", "child1.city");
        List<String> lines = doGen(spec);

        String[] ar = {
                "boolean tmp1 = true;",
                "Child1 tmp2 = (dest.getChild1() == null) ? new Child1() : dest.getChild1();",
                "dest.setChild1(tmp2);",
                "tmp2.setFlag1(tmp1);",
                "String tmp3 = \"athens\";",
                "Child1 tmp4 = (dest.getChild1() == null) ? new Child1() : dest.getChild1();",
                "dest.setChild1(tmp4);",
                "tmp4.setCity(tmp3);"
        };
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Child1");
    }

    @Test
    public void testValueScalar() {
        CopySpec spec = buildSpecValueSubObj(Customer.class, Customer.class, "'abc'", "addr.city");
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = \"abc\";",
                "Address tmp2 = (dest.getAddr() == null) ? new Address() : dest.getAddr();",
                "dest.setAddr(tmp2);",
                "tmp2.setCity(tmp1);",};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Address");
    }

    @Test
    public void testValueEnum() {
        CopySpec spec = buildSpecValueSubObj(Parent1.class, Parent1.class, "'RED'", "child1.someColor");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Color tmp1 = Color.RED;",
                "Child1 tmp2 = (dest.getChild1() == null) ? new Child1() : dest.getChild1();",
                "dest.setChild1(tmp2);",
                "tmp2.setSomeColor(tmp1);",};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Color", "org.dnal.fieldcopy.dataclass.Child1");
    }

    @Test
    public void testValueDate() {
        CopySpec spec = buildSpecValueSubObj(Parent1.class, Parent1.class, "'2022-02-28'", "child1.moveDate");
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = \"2022-02-28\";",
                "LocalDate tmp2 = ctx.toLocalDate(tmp1);",
                "Child1 tmp3 = (dest.getChild1() == null) ? new Child1() : dest.getChild1();",
                "dest.setChild1(tmp3);",
                "tmp3.setMoveDate(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.time.LocalDate", "org.dnal.fieldcopy.dataclass.Child1");
    }

    //--optional
    @Test
    public void testValueOptionalScalar() {
        CopySpec spec = buildSpecValueSubObj(Parent1.class, Parent1.class, "'abc'", "maybeChild.city");
        List<String> lines = doGen(spec);

        //need to handle both maybeChild being null or Optional.empty()
        String[] ar = {
                "String tmp1 = \"abc\";",
                "Optional<Child1> tmp2 = (ctx.isNullOrEmpty(dest.getMaybeChild())) ? Optional.of(new Child1()) : dest.getMaybeChild();",
                "dest.setMaybeChild(tmp2);",
                "tmp2.get().setCity(tmp1);",};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Child1");
    }

    @Test
    public void testValueOptionalScalar2() {
        CopySpec spec = buildSpecValueSubObj(Parent1.class, Parent1.class, "'abc'", "maybeChild.optCity");
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = \"abc\";",
                "Optional<Child1> tmp2 = (ctx.isNullOrEmpty(dest.getMaybeChild())) ? Optional.of(new Child1()) : dest.getMaybeChild();",
                "dest.setMaybeChild(tmp2);",
                "tmp2.get().setOptCity(Optional.ofNullable(tmp1));",};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Child1");
    }


    // --- fields
    @Test
    public void testFieldPrim() {
        CopySpec spec = buildSpecFieldSubObj(Parent1.class, Parent1.class, "child1.flag1", "child1.flag1");
        List<String> lines = doGen(spec);

        //sample code
//        Parent1 src = null;
//        Parent1 dest = null;
//        boolean tmp1 = src.getChild1().isFlag1();
//        Child1 tmp2 = (dest.getChild1() == null) ? new Child1() : dest.getChild1();
//        tmp2.setFlag1(tmp1);

        String[] ar = {
                "Child1 tmp1 = src.getChild1();",
                "boolean tmp2 = tmp1.isFlag1();",
                "Child1 tmp3 = (dest.getChild1() == null) ? new Child1() : dest.getChild1();",
                "dest.setChild1(tmp3);",
                "tmp3.setFlag1(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Child1");
    }

    @Test
    public void testFieldScalar() {
        CopySpec spec = buildSpecFieldSubObj(Customer.class, Customer.class, "addr.city", "addr.city");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Address tmp1 = src.getAddr();",
                "String tmp2 = tmp1.getCity();",
                "Address tmp3 = (dest.getAddr() == null) ? new Address() : dest.getAddr();",
                "dest.setAddr(tmp3);",
                "tmp3.setCity(tmp2);",};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Address");
    }

    @Test
    public void testFieldEnum() {
        CopySpec spec = buildSpecFieldSubObj(Parent1.class, Parent1.class, "child1.someColor", "child1.someColor");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Child1 tmp1 = src.getChild1();",
                "Color tmp2 = tmp1.getSomeColor();",
                "Child1 tmp3 = (dest.getChild1() == null) ? new Child1() : dest.getChild1();",
                "dest.setChild1(tmp3);",
                "tmp3.setSomeColor(tmp2);",};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Child1", "org.dnal.fieldcopy.dataclass.Color");
    }

    @Test
    public void testFieldDate() {
        CopySpec spec = buildSpecFieldSubObj(Parent1.class, Parent1.class, "child1.moveDate", "child1.moveDate");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Child1 tmp1 = src.getChild1();",
                "LocalDate tmp2 = tmp1.getMoveDate();",
                "Child1 tmp3 = (dest.getChild1() == null) ? new Child1() : dest.getChild1();",
                "dest.setChild1(tmp3);",
                "tmp3.setMoveDate(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Child1", "java.time.LocalDate");
    }

    //--optional
    @Test
    public void testFieldOptionalScalar() {
        CopySpec spec = buildSpecFieldSubObj(Parent1.class, Parent1.class, "maybeChild.city", "maybeChild.city");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Optional<Child1> tmp1 = src.getMaybeChild();",
                "String tmp2 = (ctx.isNullOrEmpty(tmp1)) ? null : tmp1.get().getCity();",
                "Optional<Child1> tmp3 = (ctx.isNullOrEmpty(dest.getMaybeChild())) ? Optional.of(new Child1()) : dest.getMaybeChild();",
                "dest.setMaybeChild(tmp3);",
                "tmp3.get().setCity(tmp2);",};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Child1");
    }

    @Test
    public void testFieldOptionalScalar2() {
        CopySpec spec = buildSpecValueSubObj(Parent1.class, Parent1.class, "'abc'", "maybeChild.optCity");
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = \"abc\";",
                "Optional<Child1> tmp2 = (ctx.isNullOrEmpty(dest.getMaybeChild())) ? Optional.of(new Child1()) : dest.getMaybeChild();",
                "dest.setMaybeChild(tmp2);",
                "tmp2.get().setOptCity(Optional.ofNullable(tmp1));",};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Child1");
    }

    @Test
    public void testFieldList() {
        CopySpec spec = buildOneListField("_double", "_int");
        setRequired(spec);
        List<String> lines = doGen(spec);

        String[] ar = {
                "List<Double> tmp1 = src._double == null ? null : ctx.createEmptyList(src._double, Double.class);",
                "if (tmp1 == null) ctx.throwUnexpectedNullError(getSourceFieldTypeInfo(), \"_double\");",
                "List<Integer> list2 = new ArrayList<>();",
                "for(Double el3: tmp1) {",
                "  Integer tmp4 = el3.intValue();",
                "  list2.add(tmp4);",
                "}",
                "dest._int = list2;"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.util.List");
    }


    //----------
    private CopySpec buildOneField(String src, String dest) {
        return buildWithField(AllScalars1.class, AllPrims1.class, src, dest);
    }

    private void setRequired(CopySpec spec) {
        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(0);
        nspec.isRequired = true;
    }

    private CopySpec buildOneListField(String src, String dest) {
        return buildWithField(AllLists1.class, AllLists1.class, src, dest);
    }

    protected void addValueField(CopySpec spec, String srcText, String destText) {
        SpecBuilder1 specBuilder1 = new SpecBuilder1();
        specBuilder1.addValue(spec, srcText, destText);
        int n = spec.fields.size();
        buildDottedFields(spec, n - 1, srcText, destText);

        JavaSrcSpec srcSpec = new JavaSrcSpec("SomeClass");
        FieldSpecBuilder fieldSpecBuilder = new FieldSpecBuilder(options);
        for (FieldSpec field : spec.fields) {
            fieldSpecBuilder.buildFieldSpec(field, srcSpec);
        }
    }

}
