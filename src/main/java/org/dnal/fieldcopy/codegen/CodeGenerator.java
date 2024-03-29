package org.dnal.fieldcopy.codegen;

import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.implicitconverter.ImplicitConvRegistry;
import org.dnal.fieldcopy.log.FieldCopyLog;
import org.dnal.fieldcopy.log.SimpleLog;
import org.dnal.fieldcopy.newcodegen.CopySpecToJavaCodeGenerator;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.registry.ConverterRegistry;
import org.dnal.fieldcopy.util.ReflectionUtil;

public class CodeGenerator {
    private ImplicitConvRegistry implicitConvRegistry;
    private ReflectionUtil refHelper = new ReflectionUtil();
    private ConverterRegistry registry;
    private VarNameGenerator varNameGenerator;
    private FieldCopyOptions options;
    private final FieldCopyLog log;

    public CodeGenerator() {
        //no registry
        options = new FieldCopyOptions();
        log = new SimpleLog();
    }

    public CodeGenerator(ImplicitConvRegistry implicitConvRegistry, ConverterRegistry registry, FieldCopyOptions options, FieldCopyLog log) {
        this.implicitConvRegistry = implicitConvRegistry;
        this.registry = registry;
        this.options = options;
        this.log = log;
    }

    public JavaSrcSpec generate(CopySpec spec) {
        CopySpecToJavaCodeGenerator codegen = new CopySpecToJavaCodeGenerator(implicitConvRegistry, registry, options);
        JavaSrcSpec srcSpec = codegen.generate(spec);
        return srcSpec;
    }

    public FieldCopyOptions getOptions() {
        return options;
    }

    //only call this immediately after ctor
    public void setOptions(FieldCopyOptions options) {
        this.options = options;
    }
}
