package org.dnal.fieldcopy.codegen;

import org.dnal.fieldcopy.fieldspec.CopySpec;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MyCodeGenerator {
    private String outDir;

    public MyCodeGenerator(String outDir) {
        this.outDir = outDir;
    }

    public boolean gen(CopySpec spec, List<String> bodyLines, JavaSrcSpec srcSpec) {
        JavaConverterCodeGenerator codeGenerator = new JavaConverterCodeGenerator(outDir);

        List<String> imports = new ArrayList<>();
        imports.addAll(Arrays.asList("org.dnal.fieldcopy.Converter",
                "org.dnal.fieldcopy.runtime.ObjectConverter",
                "org.dnal.fieldcopy.runtime.ConverterContext",
                "org.dnal.fieldcopy.types.FieldTypeInformation",
                "org.dnal.fieldcopy.types.FieldTypeInformationImpl",
                "java.util.Optional",
                "java.util.ArrayList",
                spec.srcClass.getName(),
                spec.destClass.getName()
        ));
        imports.addAll(srcSpec.getImportLines());

        List<String> srcLines = codeGenerator.genJavaSource("org.dnal.fieldcopy.codegen.gen", srcSpec.className,
                spec, imports, bodyLines);
        codeGenerator.write(srcLines);
        //handle custom converter
        if (spec.hasCustomFields()) {
            srcLines = codeGenerator.genCustomJavaSource("org.dnal.fieldcopy.codegen.gen", srcSpec.className,
                    spec, imports, bodyLines);
            File f = new File(codeGenerator.outpath);
            if (! f.exists()) {
                codeGenerator.write(srcLines);
            }
        }
        return true;
    }
}
