package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.builder.CollectionsBuilder;
import org.dnal.fieldcopy.builder.PrimsBuilder;
import org.dnal.fieldcopy.builder.SpecBuilder2;
import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.dataclass.Dest1;
import org.dnal.fieldcopy.dataclass.OptionalSrc1;
import org.dnal.fieldcopy.dataclass.Src1;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.types.JavaCollection;
import org.dnal.fieldcopy.types.JavaPrimitive;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Objects.isNull;

public class R550SkipNullTests extends RTestBase {

    /**
     * R550 skipNull
     * -means we use defaultVal if src value is null
     * -defaultVal can be null
     * -value -> field
     * -field -> field
     * -prim
     * -scalar
     * -list,date,enum
     * -NonO->O,O->NonO,O->O
     */

    @Test
    public void testString() {
        CopySpec spec = buildWithField(Src1.class, Dest1.class, "s2", "s2");
        setSkipNull(spec);
        List<String> lines = doGen(spec);
        String[] ar = {
                "String tmp1 = src.getS2();",
                "if (tmp1 != null) {",
                "dest.setS2(tmp1);",
                "}"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testPrim() {
        CopySpec spec = buildWithField(Src1.class, Dest1.class, "n1", "n1");
        setSkipNull(spec);
        List<String> lines = doGen(spec);
        String[] ar = {
                "int tmp1 = src.getN1();",
                "dest.setN1(tmp1);",
        };
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testFieldScalar() {
        CopySpec spec = buildSpecFieldSubObj(Customer.class, Customer.class, "addr.city", "addr.city");
        setSkipNull(spec);
        List<String> lines = doGen(spec);

        String[] ar = {
                "Address tmp1 = src.getAddr();",
                "if (tmp1 != null) {",
                "String tmp2 = tmp1.getCity();",
                "if (tmp2 != null) {",
                "Address tmp3 = (dest.getAddr() == null) ? new Address() : dest.getAddr();",
                "dest.setAddr(tmp3);",
                "tmp3.setCity(tmp2);",
                "}",
                "}"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Address");
    }

    //TODO test that primitive fields, we ignore skipNull

    @Test
    public void testEnum() {
        CopySpec spec = buildWithField(Src1.class, Dest1.class, "col1", "col1");
        setSkipNull(spec);
        List<String> lines = doGen(spec);

        String[] ar = {
                "Color tmp1 = src.getCol1();",
                "if (tmp1 != null) {",
                "dest.setCol1(tmp1);",
                "}"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Color");
    }


    //--- optional
    @Test
    public void testOptToNonOpt() {
        CopySpec spec = buildWithField(OptionalSrc1.class, Dest1.class, "s2", "s2");
        setSkipNull(spec);
        List<String> lines = doGen(spec);

        String[] ar = {
                "Optional<String> tmp1 = src.s2;",
                "if (ctx.isNullOrEmpty(tmp1)) {",
                "dest.setS2((ctx.isNullOrEmpty(tmp1)) ? null : tmp1.orElse(null));",
                "}"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

//    //--- lists
//    @Test
//    public void testR230() {
//        options.createNewListWhenCopying = false;
//        for (JavaCollection prim : JavaCollection.values()) {
//            testCollectionValue(prim);
//        }
//    }
//
//    @Test
//    public void testListWithCopy() {
//        CopySpec spec = collectionsBuilder.buildSpec(JavaCollection.LIST);
//        List<String> lines = doGen(spec);
//        String importStr = JavaCollection.getImport(JavaCollection.LIST);
//
//        String[] ar = {"List<String> tmp1 = src._list == null ? null : ctx.createEmptyList(src._list, String.class);",
//                "dest._list = tmp1;"
//        };
//        chkLines(lines, ar);
//        chkImports(currentSrcSpec, importStr);
//    }
//


    //--for debugging only
    @Test
    public void testDebug() {
    }

    //============
    private PrimsBuilder primsBuilder = new PrimsBuilder();
    private SpecBuilder2 specBuilder2 = new SpecBuilder2();
    private CollectionsBuilder collectionsBuilder = new CollectionsBuilder();

    private void setSkipNull(CopySpec spec) {
        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(0);
        nspec.skipNull = true;
    }

    private void testPrimValue(JavaPrimitive prim) {
        CopySpec spec = primsBuilder.buildSpec(prim);
        List<String> lines = doGen(spec);

        String javaTypeStr = JavaPrimitive.lowify(prim);

        String[] ar = {String.format("%s tmp1 = src._%s;", javaTypeStr, javaTypeStr),
                String.format("dest._%s = tmp1;", javaTypeStr)};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    private void testScalarValue(JavaPrimitive prim) {
        CopySpec spec = primsBuilder.buildSpecScalars(prim);
        List<String> lines = doGen(spec);

//        String str = StringUtil.uppify(prim.name().toLowerCase(Locale.ROOT));
        String str = JavaPrimitive.lowify(prim);
        String javaTypeStr = JavaPrimitive.getScalarType(prim);

        String[] ar = {String.format("%s tmp1 = src._%s;", javaTypeStr, str),
                String.format("dest._%s = tmp1;", str)};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    private void testCollectionValue(JavaCollection prim) {
        CopySpec spec = collectionsBuilder.buildSpec(prim);
        List<String> lines = doGen(spec);

        String str = JavaCollection.lowify(prim);
        String javaTypeStr = JavaCollection.getVarType(prim, "String", "Integer"); //Integer only used by Map
        String importStr = JavaCollection.getImport(prim);

        String[] ar = {String.format("%s tmp1 = src._%s;", javaTypeStr, str),
                String.format("dest._%s = tmp1;", str)};
        chkLines(lines, ar);
        if (isNull(importStr)) {
            chkNoImports(currentSrcSpec);
        } else {
            chkImports(currentSrcSpec, importStr);
        }
    }

}
