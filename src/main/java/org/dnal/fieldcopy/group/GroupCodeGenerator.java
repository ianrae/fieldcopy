package org.dnal.fieldcopy.group;

import org.dnal.fieldcopy.codegen.JavaConverterCodeGenerator;
import org.dnal.fieldcopy.codegen.JavaConverterGroupCodeGenerator;
import org.dnal.fieldcopy.codegen.JavaSrcSpec;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.log.FieldCopyLog;
import org.dnal.fieldcopy.log.SimpleLog;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyJsonParser;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.parser.fieldcopyjson.ParsedConverterSpec;
import org.dnal.fieldcopy.parser.fieldcopyjson.ParserResults;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;

public class GroupCodeGenerator {
    private String packageName;
    private String additionalConverterPackageName;
    private String outDir;
    private boolean dryRunFlag;
    public FieldCopyOptions options = new FieldCopyOptions();
//    private FCRegistry namedConverters; //for using()

    public boolean generateJavaFiles(ParserResults res) {
        List<List<ObjectConverterSpec>> allAdditionalConverters = new ArrayList<>(); //parallel list with specs
        List<CopySpec> specs = buildSpecList(res, allAdditionalConverters);
        //TODO check that no error happened

        int index = 0;
        for (CopySpec spec : specs) {
            //spec already contains global + spec-specific additional converters
            List<ObjectConverterSpec> additionalConverters = allAdditionalConverters.get(index++);
            findAdditionalConverters(additionalConverters);

            ConverterBodyGenerator bodyGenerator = new ConverterBodyGenerator(options, additionalConverters, null);
            JavaSrcSpec srcSpec = bodyGenerator.doGen(spec, specs);
            boolean b = genSingleConverterClassFile(spec, srcSpec.lines, srcSpec);
            if (!b) {
                return false;
            }
        }

        generateConverterGroupJavaFile(res, specs);

        return true;
    }

    //fill in actual converter classes for each in list
    private void findAdditionalConverters(List<ObjectConverterSpec> additionalConverters) {
        String pkg = isNull(additionalConverterPackageName) ? packageName : additionalConverterPackageName;
        ObjectConverterFinder finder = new ObjectConverterFinder(pkg);
        for(ObjectConverterSpec spec: additionalConverters) {
            if (isNull(spec.converter)) {
                spec.converter  = finder.findOneConverter(spec.converterClassName);
            }
        }
    }

    //TODO: is it OK to not check for duplicated? I think so
//    private List<ObjectConverterSpec> buildAdditionalConverterNames(ParserResults res) {
//        List<ObjectConverterSpec> additionalConverters = new ArrayList<>();
//        for (ParsedConverterSpec parsedConverter : res.converters) {
//            if (parsedConverter.additionalConverters != null) {
//                for (ObjectConverterSpec converterSpec : parsedConverter.additionalConverters) {
//
//                    //TODO might someone want to add same converter class more than once? (with different names)
//                    Optional<ObjectConverterSpec> existing = additionalConverters.stream()
//                            .filter(x -> x.converterClassName.equals(converterSpec.converterClassName)).findAny();
//                    if (! existing.isPresent()) {
//                        additionalConverters.add(converterSpec);
//                    }
//                }
//            }
//        }
//        return additionalConverters;
//    }

    private void generateConverterGroupJavaFile(ParserResults res, List<CopySpec> specs) {
        JavaConverterGroupCodeGenerator codeGen = new JavaConverterGroupCodeGenerator(outDir);
        String className = "DefaultConverterGroup"; //TODO fix

        List<String> imports = new ArrayList<>();
        imports.addAll(Arrays.asList("org.dnal.fieldcopy.Converter",
                "org.dnal.fieldcopy.ConverterGroup",
                "org.dnal.fieldcopy.runtime.ConverterContext",
                "org.dnal.fieldcopy.runtime.ObjectConverter",
                "org.dnal.fieldcopy.types.FieldTypeInformation",
                "org.dnal.fieldcopy.types.FieldTypeInformationImpl",
                "java.util.Optional",
                "java.util.List",
                "java.util.ArrayList"
        ));

        for (CopySpec spec : specs) {
            String s = String.format("%s.%s", packageName, spec.actualClassName);
            imports.add(s);
        }

        List<String> lines = codeGen.genStr(packageName, className, specs, imports);
        if (dryRunFlag) {
            for (String s : lines) {
                System.out.println(s);
            }
        } else {
            codeGen.write(lines);
        }
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getOutDir() {
        return outDir;
    }

    public void setOutDir(String outDir) {
        this.outDir = outDir;
    }

    public boolean isDryRunFlag() {
        return dryRunFlag;
    }

    public void setDryRunFlag(boolean dryRunFlag) {
        this.dryRunFlag = dryRunFlag;
    }

    public String getAdditionalConverterPackageName() {
        return additionalConverterPackageName;
    }

    public FieldCopyOptions getOptions() {
        return options;
    }

    public void setOptions(FieldCopyOptions options) {
        this.options = options;
    }

    public void setAdditionalConverterPackageName(String additionalConverterPackageName) {
        this.additionalConverterPackageName = additionalConverterPackageName;
    }

    private List<CopySpec> buildSpecList(ParserResults res, List<List<ObjectConverterSpec>> allAdditionalConverters) {
        List<CopySpec> specs = new ArrayList<>();
        FieldCopyJsonParser parser = createParser();
        for (ParsedConverterSpec converterSpec : res.converters) {
            CopySpec spec = parser.buildSpecFromAction(converterSpec, res.options);
            specs.add(spec);
            allAdditionalConverters.add(converterSpec.additionalConverters);
        }

        return specs;
    }

    private FieldCopyJsonParser createParser() {
        FieldCopyLog log = new SimpleLog();
        FieldCopyJsonParser parser = new FieldCopyJsonParser(log);
        return parser;
    }

    private boolean genSingleConverterClassFile(CopySpec spec, List<String> bodyLines, JavaSrcSpec srcSpec) {
        JavaConverterCodeGenerator codeGenerator = new JavaConverterCodeGenerator(outDir);

        List<String> imports = new ArrayList<>();
        imports.addAll(Arrays.asList("org.dnal.fieldcopy.Converter",
                "org.dnal.fieldcopy.ConverterGroup",
                "org.dnal.fieldcopy.runtime.ObjectConverter",
                "org.dnal.fieldcopy.runtime.ConverterContext",
                "org.dnal.fieldcopy.types.FieldTypeInformation",
                "org.dnal.fieldcopy.types.FieldTypeInformationImpl",
                "java.util.Optional",
                "java.util.List",
                "java.util.ArrayList",
                spec.srcClass.getName(),
                spec.destClass.getName()
        ));
        imports.addAll(srcSpec.getImportLines());

        List<String> srcLines = codeGenerator.genJavaSource(packageName, srcSpec.className, spec, imports, bodyLines);
        if (!dryRunFlag) {
            codeGenerator.write(srcLines);

            //handle custom converter
            if (spec.hasCustomFields()) {
                srcLines = codeGenerator.genCustomJavaSource("org.dnal.fieldcopy.codegen.gen", srcSpec.className,
                        spec, imports, bodyLines);
                File f = new File(codeGenerator.outpath);
                if (!f.exists()) {
                    codeGenerator.write(srcLines);
                }
            }
        }
        return true;
    }
}
