package org.dnal.fieldcopy.codegen;

import org.dnal.fieldcopy.builder.CustomerSpecBuilder;
import org.dnal.fieldcopy.dataclass.Address;
import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.dataclass.Inner1;
import org.dnal.fieldcopy.dataclass.Inner1DTO;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.registry.ICRTestBase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AutoFieldSpecCreatorTests extends ICRTestBase {

    public static class TestClass22 {
        private String s1;
        public String s2;
        private String s3;
        private String s4;

        private String getS3() {
            return s3;
        }

        private void setS3(String s3) {
            this.s3 = s3;
        }

        public String getS4() {
            return s4;
        }

        public void setS4(String s4) {
            this.s4 = s4;
        }
    }

    public static class TestClass23 {
        public String S2;
        private String S4;

        public String getS4() {
            return S4;
        }

        public void setS4(String s4) {
            S4 = s4;
        }
    }
    public static class TestClass24 {
        public String S2;
        private String S4;

        public String gets4() {
            return S4;
        }

        public void sets4(String s4) {
            S4 = s4;
        }
    }

    @Test
    public void test() {
        chkAuto(Inner1.class, Inner1DTO.class, 1);
        chkAuto(Customer.class, Customer.class, numFieldsInCustomer);
        chkAuto(Customer.class, Inner1DTO.class, 0);
    }

    @Test
    public void test2() {
        CustomerSpecBuilder specBuilder = new CustomerSpecBuilder();
        CopySpec spec = specBuilder.buildSpec(3);
        specBuilder.addCustom(spec);

        chkAuto(spec, numFieldsInCustomer - 3);
    }

    @Test
    public void testWithIsGetter() {
        CopySpec spec = new CopySpec(Address.class, Address.class);
        spec.autoFlag = true;

        chkAuto(spec, numFieldsInAddress);
    }

    @Test
    public void testExclude() {
        CopySpec spec = new CopySpec(Address.class, Address.class);
        spec.autoFlag = true;
        spec.autoExcludeFields = Arrays.asList("city", "backRef");

        chkAuto(spec, numFieldsInAddress - 2);
    }

    @Test
    public void testPrivate() {
        CopySpec spec = chkAuto(TestClass22.class, TestClass22.class, 2);
        chkFields(spec, "s2", "s4");
    }

    @Test
    public void testMatchIsCaseSensitive() {
        CopySpec spec = chkAuto(TestClass22.class, TestClass23.class, 1);
        chkFields(spec, "s4"); //because getter is getS4 so we infer s4 is fieldname
    }
    @Test
    public void testMatchLowerCaseGetter() {
        chkAuto(TestClass22.class, TestClass24.class, 0);
    }


    //=========
    private int numFieldsInCustomer = 15;
    private int numFieldsInAddress = 4;

    private CopySpec chkAuto(Class<?> srcClass, Class<?> destClass, int expectedSize) {
        CopySpec spec = new CopySpec(srcClass, destClass);
        spec.autoFlag = true;

        AutoFieldSpecCreator autoFieldSpecCreator = new AutoFieldSpecCreator();
        int n = autoFieldSpecCreator.createAutoFields(spec, spec.autoExcludeFields);
        assertEquals(expectedSize, n);
        return spec;
    }
    private void chkAuto(CopySpec spec, int expectedSize) {
        spec.autoFlag = true;

        AutoFieldSpecCreator autoFieldSpecCreator = new AutoFieldSpecCreator();
        int n = autoFieldSpecCreator.createAutoFields(spec, spec.autoExcludeFields);
        assertEquals(expectedSize, n);
    }
    private void chkFields(CopySpec spec, String... fieldNames) {
        int i = 0;
        for(String fieldName: fieldNames) {
            NormalFieldSpec nspec = (NormalFieldSpec) spec.fields.get(i++);
            assertEquals(fieldName, nspec.srcText);
            assertEquals(fieldName, nspec.destText);
        }
        assertEquals(fieldNames.length, spec.fields.size());
    }

}
