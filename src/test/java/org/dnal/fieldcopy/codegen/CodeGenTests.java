package org.dnal.fieldcopy.codegen;

import org.dnal.fieldcopy.builder.PrimsBuilder;
import org.dnal.fieldcopy.builder.SpecBuilder1;
import org.dnal.fieldcopy.codegen.gen.Src1ToDest1Converter;
import org.dnal.fieldcopy.converter.FCRegistry;
import org.dnal.fieldcopy.dataclass.Dest1;
import org.dnal.fieldcopy.dataclass.Src1;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.registry.ICRTestBase;
import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.RuntimeOptions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 *
 */
public class CodeGenTests extends ICRTestBase {

    @Test
    public void test1() {
//        String dir = "C:/tmp/fieldcopy2/gen";
//        JavaConverterCodeGenerator codeGenerator = new JavaConverterCodeGenerator(dir);
//
//        List<String> imports = Arrays.asList("org.dnal.fieldcopy.Converter",
//                "org.dnal.fieldcopy.runtime.ConverterContext",
//                "org.dnal.fieldcopy.dataclass.Dest1",
//                "org.dnal.fieldcopy.dataclass.Src1"
//        );
//        List<String> lines = Arrays.asList("int x = src.getN1()");
//        List<String> srcLines = codeGenerator.genStr("org.dnal.fieldcopy.codegen.gen", "Src1ToDest1Converter",
//                "Src1", "Dest1", imports, lines);
//        codeGenerator.write(srcLines);

        SpecBuilder1 specBuilder1 = new SpecBuilder1();

        CopySpec spec = specBuilder1.buildSpec(2);
        List<String> bodyLines = doGen(spec, null);
        chkNoImports(currentSrcSpec);
        assertEquals("Src1ToDest1Converter", currentSrcSpec.className);

        MyCodeGenerator myCodeGenerator = new MyCodeGenerator(outDir);
        myCodeGenerator.gen(spec, bodyLines, currentSrcSpec);

    }

//    @Test
//    public void test2() {
//        CopySpec spec = primsBuilder.buildSpec(JavaPrimitive.INT);
//        CodeGenerator codegen = new CodeGenerator();
//        JavaSrcSpec srcSpec = codegen.generate(spec);
//
//        List<String> lines = srcSpec.lines;
//        dumpLines(lines);
//
//        MyCodeGenerator myCodeGenerator = new MyCodeGenerator(outDir);
//        myCodeGenerator.gen(spec, lines, srcSpec);
//
////        JavaConverterCodeGenerator codeGenerator = new JavaConverterCodeGenerator(dir);
////
////        List<String> baseImports = Arrays.asList("org.dnal.fieldcopy.Converter",
////                "org.dnal.fieldcopy.runtime.ConverterContext"
////        );
////        List<String> imports = new ArrayList<>(baseImports);
////        imports.add(spec.srcClass.getName());
////        imports.add(spec.destClass.getName());
////
////        List<String> srcLines= codeGenerator.genStr("org.dnal.fieldcopy.codegen.gen", srcSpec.className,
////                spec.srcClass.getSimpleName(), spec.destClass.getSimpleName(), imports, lines);
////        codeGenerator.write(srcLines);
//    }

    @Test
    public void test3() {
        Src1 src = createSrc();
        Dest1 dest = new Dest1();

        Src1ToDest1Converter converter = new Src1ToDest1Converter();
        ConverterContext ctx = new ConverterContext(new FCRegistry(), new RuntimeOptions());
        Dest1 dest2 = converter.convert(src, dest, ctx);

        assertEquals(45, dest.n1);
        assertEquals("abc", dest.s2);
        assertSame(dest, dest2);
    }

    private Src1 createSrc() {
        Src1 obj = new Src1();
        obj.n1 = 45;
        obj.s2 = "abc";
        return obj;
    }


    //============
    private PrimsBuilder primsBuilder = new PrimsBuilder();
    private String outDir = "C:/tmp/fieldcopy2/gen";

}
