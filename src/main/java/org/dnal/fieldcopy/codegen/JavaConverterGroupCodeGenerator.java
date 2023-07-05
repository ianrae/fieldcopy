package org.dnal.fieldcopy.codegen;

import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.log.FieldCopyLog;
import org.dnal.fieldcopy.util.StrListCreator;
import org.dnal.fieldcopy.util.TextFileWriter;

import java.util.List;

public class JavaConverterGroupCodeGenerator {
    private final FieldCopyLog log;
    private String dir;
    public String outpath;

    public JavaConverterGroupCodeGenerator(String dir, FieldCopyLog log) {
        this.dir = dir;
        this.log = log;
    }

    public List<String> genStr(String packageName, String className, List<CopySpec> specs,
                               List<String> imports) {

        outpath = String.format("%s/%s.java", dir, className);

        StrListCreator sc = new StrListCreator();
        int indent = 0;
        sc.o("package %s;", packageName);
        sc.nl();
        for (String s : imports) {
            sc.o("import %s;", s);
        }
        sc.nl();
        sc.o("public class %s implements ConverterGroup {", className);

        indent = 2;
        sc.nl();
        sc.oIndented(indent, "@Override");
        sc.oIndented(indent, "public List<ObjectConverter> getConverters() {");
        indent = 4;

        sc.oIndented(indent, "List<ObjectConverter> list = new ArrayList<>();");
        for (CopySpec spec: specs) {
            String s = String.format("%s", spec.actualClassName);
            sc.addStrIndented(indent, String.format("list.add(new %s());", s));
        }
        sc.oIndented(indent, "return list;");
        sc.addStr("  }");

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
