package org.dnal.fieldvalidate;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.BaseTest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FieldValidateTests extends BaseTest {
    public static class ValidationResults {

    }
    public static class Validator {
        private Object target;

        public Validator() {
//            this.target = target;
        }
        public ValidationResults validate(Object target) {
            return new ValidationResults();
        }

    }
    public static class Val1 {

        private final String fieldName;
        private final List<Val1> list;

        public Val1(String fieldName, List<Val1> list) {
            this.fieldName = fieldName;
            this.list = list;
        }

        public Val1 field(String fieldName) {
            return new Val1(fieldName, list);
        }

        public Val1 notNull() {
            return this;
        }

    }
    public static class ValidateBuilder {
        private List<Val1> list = new ArrayList<>();

        public Val1 field(String fieldName) {
            Val1 val1 = new Val1(fieldName, list);
            list.add(val1);
            return val1;
        }

        public Validator build() {
            return new Validator();
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
        ValidateBuilder vb = new ValidateBuilder();
//        ValidationResults res = val.field("size").notNull()
//                .field("firstName").notNull()
//                .run();


        //range(int,int), ...
        //min(int)..., max(...)
        //in(int,int,int...)
        //anon fn
        vb.field("size").notNull();
        vb.field("firstName").notNull();

        Validator runner = vb.build(); //can cache this for perf
        ValidationResults res = runner.validate(obj);

    }


    //--
}
