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
        private boolean isNotNull;
        private Object minObj;
        private Object maxObj;
        private Val1 elementsVal;
        private ValidateBuilder subBuilder;
        private ValidateBuilder mapBuilder;

        public Val1(String fieldName, List<Val1> list) {
            this.fieldName = fieldName;
            this.list = list;
        }

        public Val1 field(String fieldName) {
            Val1 val1 = new Val1(fieldName, list);
            list.add(val1);
            return val1;
        }

        public Val1 notNull() {
            isNotNull = true;
            return this;
        }

//        byte	1 byte	Stores whole numbers from -128 to 127
//        short	2 bytes	Stores whole numbers from -32,768 to 32,767
//        int	4 bytes	Stores whole numbers from -2,147,483,648 to 2,147,483,647
//        long	8 bytes	Stores whole numbers from -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807
//        float	4 bytes	Stores fractional numbers. Sufficient for storing 6 to 7 decimal digits
//        double	8 bytes	Stores fractional numbers. Sufficient for storing 15 decimal digits
//        boolean	1 bit	Stores true or false values
//        char	2 bytes	Stores a single character/letter or ASCII values
        public Val1 min(int n) {
            minObj = Integer.valueOf(n);
            return this;
        }
        public Val1 min(long n) {
            minObj = Long.valueOf(n);
            return this;
        }
        public Val1 min(double n) {
            minObj = Double.valueOf(n);
            return this;
        }
        //max
        public Val1 max(int n) {
            maxObj = Integer.valueOf(n);
            return this;
        }
        public Val1 max(long n) {
            maxObj = Long.valueOf(n);
            return this;
        }
        public Val1 max(double n) {
            maxObj = Double.valueOf(n);
            return this;
        }
        //range
        //in has above types and char,string

        public Val1 elements() {
            this.elementsVal = new Val1(list);
            return elementsVal;
        }
        public Val1 subObj(ValidateBuilder subBuilder) {
            this.subBuilder = subBuilder;
            return this;
        }

        public Val1 mapField(ValidateBuilder vb3) {
            this.mapBuilder = vb3;
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
        //regex
        //anon fn
        vb.field("size").notNull();
        vb.field("firstName").notNull();

        //list of int
        vb.field("taxCodes").notNull().elements().min(33);

        //list of struct
        ValidateBuilder vb2 = new ValidateBuilder();
        vb2.field("city").notNull();
        vb.field("addrs").notNull().subObj(vb2);

        //map
        ValidateBuilder vb3 = new ValidateBuilder();
        vb3.field("city").notNull();
        vb.field("priceMap").notNull().mapField(vb3);

        Validator runner = vb.build(); //can cache this for perf
        ValidationResults res = runner.validate(obj);

    }


    //--
}
