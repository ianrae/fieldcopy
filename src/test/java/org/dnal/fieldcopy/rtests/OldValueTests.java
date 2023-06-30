package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.TestBase;
import org.dnal.fieldcopy.builder.*;
import org.dnal.fieldcopy.codegen.CodeGenerator;
import org.dnal.fieldcopy.codegen.JavaSrcSpec;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.types.JavaCollection;
import org.dnal.fieldcopy.types.JavaPrimitive;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Objects.isNull;

/**
 * //  DONE  R200: empty list (no a -> b)
 * //
 * //  DONE  R210 value -> field   primitives
 * //  DONE   R215 value -> field   String/Long/... Objects
 * //  DONE  R220 value -> field   enum
 * //  TODO ...  R213 value -> field   custom String
 * //  DONE  R225  Vehicle //entire object
 * //  DONE  R230 array/list/set/map
 * //  DONE R240 Optional<?>
 * //      -alltypes
 */
public class OldValueTests extends TestBase {

    @Test
    public void testR200() {
        CopySpec spec = buildSpec(0);
        List<String> lines = doGen(spec);
        String[] arEmpty = {};
        chkLines(lines, arEmpty);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testR210() {
        for (JavaPrimitive prim : JavaPrimitive.values()) {
            testPrimValue(prim);
        }
    }

    @Test
    public void testR215() {
        for (JavaPrimitive prim : JavaPrimitive.values()) {
            testScalarValue(prim);
        }
    }

    @Test
    public void testR220() {
        CopySpec spec = specBuilder.buildSpec(0);
        spec = specBuilder.addEnum(spec);
        List<String> lines = doGen(spec);

        String javaTypeStr = "Color";
        String[] ar = {String.format("%s tmp1 = src.getCol1();", javaTypeStr),
                String.format("dest.setCol1(tmp1);")};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Color");
    }

    //  TODO ...  R213 value -> field   custom String

    @Test
    public void testR225() {
        CopySpec spec = specBuilder.buildSpec(0);
        spec = specBuilder.addObject(spec);
        List<String> lines = doGen(spec);

        String javaTypeStr = "Inner1";
        String[] ar = {String.format("%s tmp1 = src.getInner1();", javaTypeStr),
                String.format("dest.setInner1(tmp1);")};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Inner1");
    }

    @Test
    public void testR230() {
        options.createNewListWhenCopying = false;
        for (JavaCollection prim : JavaCollection.values()) {
            testCollectionValue(prim);
        }
    }
    //TODO:
    //R231 list<list>, list<set>, list<map>
    //R232 set<list>, set<set>, set<map>
    //R233 map<list>, map<set>, map<map>

    @Test
    public void testR240() {
        CopySpec spec = specBuilder2.buildSpec(2); //TODO make this 2 later
        List<String> lines = doGen(spec);

        //TODO fix this. we're doing
        // s2 -> s2
        // inner1.s3 -> s2  this part isn't working
        //we generate: String tmp4 = (dest.getS2() == null) ? new String() : dest.getS2();
        // don't need to do new String(). It's a scalar object. we can simply do dest.setS2(tmp3)

        //then we need tests for all 3 cases
        // addr.city -> addr.city
        // addr.city -> someStr
        // someString -> addr.city

        /* TODO: interesting problem.
         -if we have inner1.s3 -> s2 and src.inner1 is null what do we do?
           -a) crash. we implied inner1 has a value   <--- LET'S DO THIS FOR NOW
           -b) default to null and don't crash. see this:
              String tmp2 = Optional.ofNullable(src.inner1).map(x -> s3).orElse(null);
         */

        //TODO need more tests of Optional to Optional and all 4 cases

        String[] ar = {
                "Optional<String> tmp1 = src.s2;",
                "dest.setS2((ctx.isNullOrEmpty(tmp1)) ? null : tmp1.orElse(null));",
                "Optional<Inner1> tmp2 = src.inner1;",
                "String tmp3 = (ctx.isNullOrEmpty(tmp2)) ? null : tmp2.get().s3;",
                "dest.setS2(tmp3);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Inner1");
    }
    //TODO Optional<List>
    //TODO Optional<Set>
    //TODO Optional<Map>

    //--for debugging only
    @Test
    public void testDebug() {
    }


    //============
    private SpecBuilder1 specBuilder = new SpecBuilder1();
    private PrimsBuilder primsBuilder = new PrimsBuilder();
    private CollectionsBuilder collectionsBuilder = new CollectionsBuilder();
    private SpecBuilder2 specBuilder2 = new SpecBuilder2();
    private JavaSrcSpec currentSrcSpec;
    private FieldCopyOptions options = new FieldCopyOptions();

    private List<String> doGen(CopySpec spec) {
        CodeGenerator codegen = new CodeGenerator();
        codegen.setOptions(options);
        codegen.getOptions().outputFieldCommentFlag = false;
        JavaSrcSpec srcSpec = codegen.generate(spec);
        currentSrcSpec = srcSpec;

        List<String> lines = srcSpec.lines;
        dumpLines(lines);
        return lines;
    }


    private CopySpec buildSpec(int n) {
        return specBuilder.buildSpec(n);
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
