package org.dnal.fieldcopy.codegen;

import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.util.StrListCreator;
import org.dnal.fieldcopy.util.TextFileWriter;

import java.util.List;

public class JavaConverterCodeGenerator {
    private String dir;
    public String outpath;

    public JavaConverterCodeGenerator(String dir) {
        this.dir = dir;
    }

    public List<String> genJavaSource(String packageName, String className, CopySpec spec,
                                      List<String> imports, List<String> lines) {
        String srcClassName = spec.srcClass.getSimpleName();
        String destClassName = spec.destClass.getSimpleName();
        List<NormalFieldSpec> customFields = spec.buildCustomList();

        String suffix = (spec.hasCustomFields()) ? "Base" : "";
        outpath = spec.buildFilePathAndSetActualClassName(dir, className, suffix);

        StrListCreator sc = new StrListCreator();
        int indent = 0;
        sc.o("package %s;", packageName);
        sc.nl();
        for (String s : imports) {
            sc.o("import %s;", s);
        }
        sc.nl();

        String abstractStr = customFields.isEmpty() ? "" : "abstract ";
        sc.o("public %sclass %s implements ObjectConverter<%s, %s> {", abstractStr, spec.actualClassName, srcClassName, destClassName);

        indent = 2;
        sc.nl();
        sc.oIndented(indent, "@Override");
        sc.oIndented(indent, "public FieldTypeInformation getSourceFieldTypeInfo() {");
        sc.oIndented(indent + 2, "  return new FieldTypeInformationImpl(%s.class);", srcClassName);
        sc.oIndented(indent, "}");
        sc.nl();
        sc.oIndented(indent, "@Override");
        sc.oIndented(indent, "public FieldTypeInformation getDestinationFieldTypeInfo() {");
        sc.oIndented(indent, "  return new FieldTypeInformationImpl(%s.class);", destClassName);
        sc.oIndented(indent, "}");
        sc.nl();

        sc.addStrIndented(indent, "@Override");
        sc.oIndented(indent, "public %s convert(%s src, %s dest, ConverterContext ctx) {", destClassName,
                srcClassName, destClassName);

        indent = 4;
        sc.oIndented(indent, "ctx.throwIfInfiniteLoop(src);");
        for (String s : lines) {
            sc.addStrIndented(indent, s);
        }
        sc.nl();
        sc.oIndented(indent, "return dest;");
        sc.addStr("  }");
        indent = 2;

        //do custom
        for (NormalFieldSpec nspec : customFields) {
            //"int tmp2 = convertN1(tmp1, src, dest, ctx);",
            sc.oIndented(indent, "protected abstract %s %s(%s srcValue, %s src, %s dest, ConverterContext ctx);",
                    nspec.customReturnType,
                    nspec.customMethodName,
                    nspec.customReturnType,
                    srcClassName,
                    destClassName);
        }

        sc.addStr("}");

        return sc.getLines();
    }

    public List<String> genCustomJavaSource(String packageName, String className, CopySpec spec,
                                      List<String> imports, List<String> lines) {
        String srcClassName = spec.srcClass.getSimpleName();
        String destClassName = spec.destClass.getSimpleName();
        List<NormalFieldSpec> customFields = spec.buildCustomList();

        className  = spec.buildActualClassName(className, "");
        outpath = String.format("%s/%s.java", dir, className);

        StrListCreator sc = new StrListCreator();
        int indent = 0;
        sc.o("package %s;", packageName);
        sc.nl();
        for (String s : imports) {
            sc.o("import %s;", s);
        }
        sc.nl();

        String baseClassName = String.format("%sBase", className);
        sc.o("public class %s extends %s {", className, baseClassName);

        indent = 2;

        //do custom
        for (NormalFieldSpec nspec : customFields) {
            //"int tmp2 = convertN1(tmp1, src, dest, ctx);",
            sc.addStrIndented(indent, "@Override");
            sc.oIndented(indent, "protected %s %s(%s srcValue, %s src, %s dest, ConverterContext ctx) {",
                    nspec.customReturnType,
                    nspec.customMethodName,
                    nspec.customReturnType,
                    srcClassName,
                    destClassName);
            sc.oIndented(indent + 2, "//TODO add custom code here");
            sc.oIndented(indent + 2, "return null;");
            sc.addStr("  }");

        }

        sc.addStr("}");

        return sc.getLines();
    }



    public void write(List<String> srcLines) {
        TextFileWriter w = new TextFileWriter();
        log("writing " + outpath);
        w.writeFile(outpath, srcLines);
    }

    private void log(String s) {
        System.out.println(s);
    }
}
