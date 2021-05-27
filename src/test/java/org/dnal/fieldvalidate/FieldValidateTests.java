package org.dnal.fieldvalidate;

import static org.junit.Assert.assertEquals;

import org.apache.commons.beanutils.PropertyUtils;
import org.dnal.fieldcopy.BaseTest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FieldValidateTests extends BaseTest {
    public enum ErrorType {
        NOT_NULL,
        VALUE
    }
    public static class FieldError {
        public String fieldName;
        public Object fieldValue;
        public String errMsg;
        public ErrorType errType;

        public FieldError(String fieldName, Object fieldValue, ErrorType errType) {
            this.fieldName = fieldName;
            this.fieldValue = fieldValue;
            this.errType = errType;
        }
    }
    public static class ValidationResults {
        public List<FieldError> errL = new ArrayList<>();

        public boolean hasNoErrors() {
            return errL.isEmpty();
        }
    }
    public static class Validator {
        private final List<ValSpec> specList;
        private Object target;

        public Validator(List<ValSpec> specList) {
            this.specList = specList;
        }
        public ValidationResults validate(Object target) {
            ValidationResults res =  new ValidationResults();
            for(ValSpec spec: specList) {
                try {
                    doValidate(target, spec, res);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return res;
        }

        private ValidationResults doValidate(Object target, ValSpec spec, ValidationResults res) throws Exception {
            Object fieldValue = PropertyUtils.getProperty(target, spec.fieldName);
            if (fieldValue == null && spec.isNotNull) {
                FieldError err = new FieldError(spec.fieldName, null, ErrorType.NOT_NULL);
                err.errMsg = String.format("field '%s': unexpected null value", spec.fieldName);
                res.errL.add(err);
            } else if (spec.minObj != null) {
                if (compareValues(fieldValue, spec.minObj) < 0) {
                    FieldError err = new FieldError(spec.fieldName, fieldValue, ErrorType.VALUE);
                    err.errMsg = String.format("field '%s': min(%s) value failed. actual value: %s", spec.fieldName, spec.minObj.toString(), fieldValue.toString());
                    res.errL.add(err);
                }
            }
            return res;
        }

        private int compareValues(Object fieldValue, Object minObj) {
            return -1;
        }

        public List<ValSpec> getSpecList() {
            return specList;
        }
    }

    public static class ValSpec {
        public String fieldName;
        public boolean isNotNull;
        public Object minObj;
        public Object maxObj;
        public FieldValidateTests.Val1 elementsVal;
        public ValidateBuilder subBuilder;
        public ValidateBuilder mapBuilder;


        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(fieldName);
            sb.append(":");
            sb.append(isNotNull ? "notNull" : "");
            return sb.toString();
        }
    }

    public static class Val1 {
        private final String fieldName;
        private final List<Val1> list;
        private final List<ValSpec> specList;
        private boolean isNotNull;
        private Object minObj;
        private Object maxObj;
        private Val1 elementsVal;
        private ValidateBuilder subBuilder;
        private ValidateBuilder mapBuilder;

        public Val1(String fieldName, List<Val1> list, List<ValSpec> specList) {
            this.fieldName = fieldName;
            this.list = list;
            this.specList = specList;
        }

//        public Val1 field(String fieldName) {
//            buildAndAddSpec();
//            Val1 val1 = new Val1(fieldName, list, specList);
//            list.add(val1);
//            return val1;
//        }

        void buildAndAddSpec() {
            ValSpec spec = new ValSpec();
            spec.elementsVal = elementsVal;
            spec.fieldName = fieldName;
            spec.mapBuilder = mapBuilder;
            spec.isNotNull = isNotNull;
            spec.subBuilder = subBuilder;
            spec.minObj = minObj;
            spec.maxObj = maxObj;
            specList.add(spec);
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
            this.elementsVal = new Val1(fieldName, list, specList);
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
        private List<ValSpec> specList = new ArrayList<>();

        public Val1 field(String fieldName) {
            buildSpecForLastVal();
            Val1 val1 = new Val1(fieldName, list, specList);
            list.add(val1);
            return val1;
        }

        public Validator build() {
            buildSpecForLastVal();
            return new Validator(specList);
        }
        private void buildSpecForLastVal() {
            if (!list.isEmpty()) {
                Val1 val = list.get(list.size() - 1);
                val.buildAndAddSpec();
            }
        }
    }


    public static class Home {
        private int points;
        private String[] names;
        private String lastName;

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String[] getNames() {
            return names;
        }

        public void setNames(String[] names) {
            this.names = names;
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
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
//        ValidationResults res = runner.validate(obj);
    }

    @Test
    public void testEmpty() {
        ValidateBuilder vb = new ValidateBuilder();
        Validator runner = vb.build(); //can cache this for perf
        List<ValSpec> list = runner.getSpecList();
        assertEquals(0, list.size());
    }

    @Test
    public void test1() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("field1");
        vb.field("field2").notNull();
        Validator runner = vb.build(); //can cache this for perf
        List<ValSpec> list = runner.getSpecList();
        assertEquals(2, list.size());

        String s = list.get(0).toString();
        assertEquals("field1:", s);
        s = list.get(1).toString();
        assertEquals("field2:notNull", s);
    }

    @Test
    public void testBeanUtils() throws Exception {
        Home home = new Home();
        home.setPoints(42);

        Integer n = (Integer) PropertyUtils.getProperty(home, "points");
        assertEquals(42, n.intValue());
    }

    @Test
    public void testRunnerOK() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("lastName").notNull();
        Validator runner = vb.build(); //can cache this for perf
        List<ValSpec> list = runner.getSpecList();
        assertEquals(1, list.size());

        Home home = new Home();
        home.setLastName("bob");
        ValidationResults res = runner.validate(home);
        assertEquals(true, res.hasNoErrors());
    }
    @Test
    public void testRunnerValError() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("lastName").notNull();
        Validator runner = vb.build(); //can cache this for perf
        List<ValSpec> list = runner.getSpecList();
        assertEquals(1, list.size());

        Home home = new Home();
        ValidationResults res = runner.validate(home);
        assertEquals(false, res.hasNoErrors());
        assertEquals(1, res.errL.size());
        FieldError err = res.errL.get(0);
        assertEquals(ErrorType.NOT_NULL, err.errType);
    }

    @Test
    public void testMin() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("points").notNull().min(50);
        Validator runner = vb.build(); //can cache this for perf
        List<ValSpec> list = runner.getSpecList();
        assertEquals(1, list.size());

        Home home = new Home();
        home.setPoints(30);
        ValidationResults res = runner.validate(home);
        assertEquals(false, res.hasNoErrors());
        assertEquals(1, res.errL.size());
        FieldError err = res.errL.get(0);
        assertEquals(ErrorType.VALUE, err.errType);
        log(err.errMsg);
    }


    //--
}
