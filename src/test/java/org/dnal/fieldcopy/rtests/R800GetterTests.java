package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.dataclass.Address;
import org.dnal.fieldcopy.dataclass.Getter1;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * R480 getter/field
 * -src class or dest class has public fields
 * -src class or dest class has getters
 * -has both public field and getter
 */
public class R800GetterTests extends RTestBase {
    public static class PrivateGetter1 {
        public String str1;

        protected String getStr1() {
            return str1;
        }

        protected void setStr1(String str1) {
            this.str1 = str1;
        }
    }

    public static class PrivateGetter2 {
        private String str1;

        protected String getStr1() {
            return str1;
        }

        protected void setStr1(String str1) {
            this.str1 = str1;
        }
    }

    //other tests have already tests getter and public field

    @Test
    public void testBothFieldAndGetter() {
        CopySpec spec = buildWithField(Getter1.class, Getter1.class, "str1", "str1");
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = src.getStr1();",
                "dest.setStr1(tmp1);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testPrivateGetter() {
        CopySpec spec = buildWithField(PrivateGetter1.class, PrivateGetter1.class, "str1", "str1");
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = src.str1;",
                "dest.str1 = tmp1;"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testPrivateGetterAndPrivateField() {
        CopySpec spec = buildWithField(PrivateGetter2.class, PrivateGetter2.class, "str1", "str1");
        FieldCopyException thrown = Assertions.assertThrows(FieldCopyException.class, () -> {
            List<String> lines = doGen(spec);
        });
        chkException(thrown, "Can't find field 'str1'");
    }

    @Test
    public void testIsGetter() {
        CopySpec spec = buildWithField(Address.class, Address.class, "flag1", "flag1");
        List<String> lines = doGen(spec);

        Address addr = new Address();
        boolean b = addr.isFlag1();

        String[] ar = {
                "boolean tmp1 = src.isFlag1();",
                "dest.setFlag1(tmp1);"};
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

}
