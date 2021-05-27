package org.dnal.fieldvalidate;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Array;

import org.dnal.fieldcopy.BaseTest;
import org.dnal.fieldcopy.converter.ConverterContext;
import org.dnal.fieldcopy.converter.FieldInfo;
import org.dnal.fieldcopy.service.beanutils.BUBeanDetectorService;
import org.dnal.fieldcopy.service.beanutils.old.ArrayElementConverter;
import org.junit.Test;

public class FieldValidateTests extends BaseTest {
    public static class ValidationResults {

    }
    public static class Val1 {

        private final String fieldName;

        public Val1(String fieldName) {
            this.fieldName = fieldName;
        }

        public Val1 notNull() {
            return this;
        }

        public Val1 field(String fieldName) {
            return new Val1(fieldName);
        }


        public ValidationResults run() {
            return new ValidationResults();
        }
    }
    public static class Validator {
        private final Home target;

        public Validator(Home obj) {
            this.target = obj;
        }

        public Val1 field(String fieldName) {
            return new Val1(fieldName);
        }
    }


    public static class Home {
        private String[] names;

        public String[] getNames() {
            return names;
        }

        public void setNames(String[] names) {
            this.names = names;
        }

    }

    @Test
    public void test() {
        assertEquals(3, 3);

        Home obj = new Home();
        Validator val = new Validator(obj);

        ValidationResults res = val.field("size").notNull()
                .field("firstName").notNull()
                .run();


        
    }


    //--
}
