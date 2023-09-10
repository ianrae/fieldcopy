package org.dnal.fieldcopy.registry;

import org.dnal.fieldcopy.builder.AddressSpecBuilder;
import org.dnal.fieldcopy.builder.CustomerSpecBuilder;
import org.dnal.fieldcopy.codegen.CodeGenerator;
import org.dnal.fieldcopy.codegen.JavaSrcSpec;
import org.dnal.fieldcopy.converter.FCRegistry;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.group.ConverterBodyGenerator;
import org.dnal.fieldcopy.implicitconverter.ImplicitConvRegistry;
import org.dnal.fieldcopy.implicitconverter.ImplicitConverterRegistryBuilder;
import org.dnal.fieldcopy.parser.ParserTestBase;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.testhelpers.BodyGeneratorTestHelper;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ICRTestBase extends ParserTestBase {


    //============
    protected CustomerSpecBuilder specBuilder = new CustomerSpecBuilder();
    protected AddressSpecBuilder addrSpecBuilder = new AddressSpecBuilder();
    protected JavaSrcSpec currentSrcSpec;
    protected ObjectConverter<?, ?> customConverter1;
    protected FCRegistry finalReg;
    protected FieldCopyOptions options = new FieldCopyOptions();

    protected List<String> doGen(CopySpec spec, CopySpec spec2) {
        options.outputFieldCommentFlag = false;
        ConverterBodyGenerator bodyGenerator = createBodyGenerator();
        List<CopySpec> specs = new ArrayList<>();
        if (spec2 != null) {
            specs.add(spec2);
        }

        currentSrcSpec = bodyGenerator.doGen(spec, specs);
        finalReg = bodyGenerator.getFinalReg();
        return currentSrcSpec.lines;
    }

    private ConverterBodyGenerator createBodyGenerator() {
        return BodyGeneratorTestHelper.createBodyGenerator(customConverter1, options);
    }

    //used by other tests
    protected ImplicitConvRegistry createImplicitConvRegistry() {
        ImplicitConverterRegistryBuilder icrBuilder = new ImplicitConverterRegistryBuilder();
        icrBuilder.init();
        icrBuilder.startBuild();
        icrBuilder.buildAdditional();
        ImplicitConvRegistry implicitConvRegistry = new ImplicitConvRegistry(icrBuilder.getRenderMap(), icrBuilder.getPrimBuilder().getStringFieldTypeInfo());
        return implicitConvRegistry;
    }

    protected List<String> generateCode(CopySpec spec, ImplicitConvRegistry implicitConvRegistry, ConverterRegistry registry) {
        options.outputFieldCommentFlag = false;
        CodeGenerator codegen = new CodeGenerator(implicitConvRegistry, registry, options, log);
        JavaSrcSpec srcSpec = codegen.generate(spec);
        currentSrcSpec = srcSpec;

        List<String> lines = srcSpec.lines;
        dumpLines(lines);
        return lines;
    }
}
