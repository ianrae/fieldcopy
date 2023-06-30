package org.dnal.fieldcopy.codegen;

import org.dnal.fieldcopy.TestBase;
import org.dnal.fieldcopy.converter.FCRegistry;
import org.dnal.fieldcopy.dataclass.Dest1;
import org.dnal.fieldcopy.dataclass.Src1;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.implicitconverter.ImplicitConvRegistry;
import org.dnal.fieldcopy.implicitconverter.ImplicitConverterRegistryBuilder;
import org.dnal.fieldcopy.mlexer.ASTToSpecBuilder;
import org.dnal.fieldcopy.mlexer.ConvLangParser;
import org.dnal.fieldcopy.mlexer.Token;
import org.dnal.fieldcopy.mlexer.ast.AST;
import org.dnal.fieldcopy.newcodegen.CopySpecToJavaCodeGenerator;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TODO: start here after UK Trip
 *  -add code in CodeGenerator.generate
 *   -boolean useNewCodeGen
 *   -if true then invoke NewCodeGenerator
 * We are re-writing java code generation to handle addr.city and addr.region.code
 * The idea is to have 3 types of generators
 * SourceValGen
 * ConversionGen  implicit, explicit, and list copying
 * DestValGen
 */
public class CopySpecToJavaCodeGeneratorTests extends TestBase {

    @Test
    public void test() {
        CopySpecToJavaCodeGenerator codegen = buildNewCodeGenerator();
        CopySpec spec = buildSpecFromAST("n1 -> n1");
        JavaSrcSpec srcSpec = codegen.generate(spec);

        assertEquals("Src1ToDest1Converter", srcSpec.className);
        List<String> lines = srcSpec.lines;
        dumpLines(lines);
        assertEquals(4, lines.size());
        assertEquals("", lines.get(0));
        assertEquals("// n1 -> n1", lines.get(1));
        assertEquals("int tmp1 = src.getN1();", lines.get(2));
        assertEquals("dest.setN1(tmp1);", lines.get(3));
    }

    @Test
    public void testBad() {
        CopySpecToJavaCodeGenerator codegen = buildNewCodeGenerator();
        log("sdf");
        FieldCopyException thrown = Assertions.assertThrows(FieldCopyException.class, () -> {
            CopySpec spec = buildSpecFromAST("wrong");
        });
        chkException(thrown, "syntax error in converter");
    }


    //============
    private CopySpec buildSpecFromAST(String convLangSrc) {
        ConvLangParser parser = new ConvLangParser();
        List<Token> toks = parser.parseIntoTokens(convLangSrc);

        List<AST> list = parser.parseIntoAST(toks);
        ASTToSpecBuilder builder = new ASTToSpecBuilder();
        CopySpec spec = builder.buildSpec(Src1.class, Dest1.class);
        builder.addToSpec(spec, list, convLangSrc);
        return spec;
    }

    protected ImplicitConvRegistry createImplicitConvRegistry() {
        ImplicitConverterRegistryBuilder icrBuilder = new ImplicitConverterRegistryBuilder();
        icrBuilder.init();
        icrBuilder.startBuild();
        icrBuilder.buildAdditional();
        ImplicitConvRegistry implicitConvRegistry = new ImplicitConvRegistry(icrBuilder.getRenderMap(), icrBuilder.getPrimBuilder().getStringFieldTypeInfo());
        return implicitConvRegistry;
    }
    private CopySpecToJavaCodeGenerator buildNewCodeGenerator() {
        ImplicitConvRegistry implicitConvRegistry = createImplicitConvRegistry();
        FieldCopyOptions options = new FieldCopyOptions();
        CopySpecToJavaCodeGenerator codegen = new CopySpecToJavaCodeGenerator(implicitConvRegistry, new FCRegistry(), options);
        return codegen;
    }



}
