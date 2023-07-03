package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.builder.CollectionsBuilder;
import org.dnal.fieldcopy.builder.PrimsBuilder;
import org.dnal.fieldcopy.builder.SpecBuilder2;
import org.dnal.fieldcopy.dataclass.*;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.types.JavaCollection;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    //--- lists
    @Test
    public void testListWithCopy() {
        CopySpec spec = collectionsBuilder.buildSpec(JavaCollection.LIST);
        setSkipNull(spec);
        List<String> lines = doGen(spec);
        String importStr = JavaCollection.getImport(JavaCollection.LIST);

        String[] ar = {"List<String> tmp1 = src._list == null ? null : ctx.createEmptyList(src._list, String.class);",
                "if (tmp1 != null) {",
                "dest._list = tmp1;",
                "}"
        };
        chkLines(lines, ar);
        chkImports(currentSrcSpec, importStr);
    }

    @Test
    public void testWitConverter() {
        CopySpec spec = buildSpecFieldSubObj(Customer.class, Customer.class, "addr", "addr");
        setSkipNull(spec);
        CopySpec spec2 = buildSpecFieldSubObj(Address.class, Address.class, "city", "city");
        List<String> lines = doGen(spec, spec2);

        //note. we coalesce to if(tmp!=null) into one
        String[] ar = {
                "Address tmp1 = src.getAddr();",
                "if (tmp1 != null) {",
                "ObjectConverter<Address,Address> conv2 = ctx.locate(Address.class, Address.class);",
                "Address tmp3 = conv2.convert(tmp1, new Address(), ctx);",
                "dest.setAddr(tmp3);",
                "}",
        };
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Address");
    }

    //--for debugging only
    @Test
    public void testDebug() {
    }

    //============
    private CollectionsBuilder collectionsBuilder = new CollectionsBuilder();

    private void setSkipNull(CopySpec spec) {
        NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(0);
        nspec.skipNull = true;
    }
}
