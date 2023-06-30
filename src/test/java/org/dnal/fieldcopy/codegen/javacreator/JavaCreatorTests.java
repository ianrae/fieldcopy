package org.dnal.fieldcopy.codegen.javacreator;

import org.dnal.fieldcopy.TestBase;
import org.dnal.fieldcopy.codegen.JavaSrcSpec;
import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.fieldspec.SingleFld;
import org.dnal.fieldcopy.fieldspec.SingleValue;
import org.dnal.fieldcopy.newcodegen.javacreator.JavaCreator;
import org.dnal.fieldcopy.newcodegen.javacreator.JavaCreatorImpl;
import org.dnal.fieldcopy.newcodegen.javacreator.JavaField;
import org.dnal.fieldcopy.newcodegen.javacreator.JavaVar;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.util.ReflectionUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaCreatorTests extends TestBase {


    @Test
    public void test() {
        JavaCreatorImpl sc = buildJavaCreator();
        sc.text("int x = 7;");
        log(sc);

        String[] ar = {
                "int x = 7;",
        };
        chkLines(sc, ar);
    }

    private JavaCreatorImpl buildJavaCreator() {
        JavaCreatorImpl sc = new JavaCreatorImpl(new JavaSrcSpec("someClass"), new FieldCopyOptions());
        return sc;
    }

    @Test
    public void testIndent() {
        JavaCreatorImpl sc = buildJavaCreator();
        sc.indent = 2;
        sc.text("int x = 7;");
        log(sc);

        String[] ar = {
                "  int x = 7;",
        };
        chkLines(sc, ar);
    }

    @Test
    public void testIfExpr() {
        JavaCreatorImpl sc = buildJavaCreator();
        sc.text(sc.generateIfNullExpr("tmp1"));
        log(sc);

        String[] ar = {
                "if (tmp1 == null)",
        };
        chkLines(sc, ar);
    }

    @Test
    public void testGetter() {
        JavaCreatorImpl sc = buildJavaCreator();

        FieldTypeInformation fti = helper.getFieldInformation(Customer.class, "firstName");
        SingleFld fld = new SingleValue("firstName", fti);
        JavaField jfield = new JavaField("firstName", fld);
        JavaVar javaVar = sc.getStatement("tmp1", "src", jfield.fti, false, jfield);
        log(sc);

        assertEquals("tmp1", javaVar.varName);
        assertEquals("String", javaVar.varType);
        assertEquals(String.class, javaVar.jfield.get().fti.getFieldType());
        assertEquals("firstName", javaVar.jfield.get().fieldName);

        String[] ar = {
                "String tmp1 = src.getFirstName();",
        };
        chkLines(sc, ar);
    }


    //----
    public ReflectionUtil helper = new ReflectionUtil();

    private void log(JavaCreator jc) {
        List<String> lines = jc.getLines();
        for (String line : lines) {
            log(line);
        }
    }

    private void chkLines(JavaCreatorImpl sc, String[] ar) {
        List<String> lines = sc.getLines();
        chkLines(lines, ar);
    }


}
