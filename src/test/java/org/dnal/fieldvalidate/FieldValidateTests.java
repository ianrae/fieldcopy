package org.dnal.fieldvalidate;

import static org.junit.Assert.assertEquals;

import org.apache.commons.beanutils.PropertyUtils;
import org.dnal.fieldcopy.BaseTest;
import org.dnal.fieldvalidate.code.NumberUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FieldValidateTests extends BaseTest {
    public static class FieldValidateException extends RuntimeException {

        public FieldValidateException(String message) {
            super(message);
        }
    }
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
                String msg = String.format("unexpected null value");
                addNotNullError(res, spec, msg);
            }
            if (spec.minObj != null) {
                if (compareValues(fieldValue, spec.minObj) < 0) {
                    String msg = String.format("min(%s) failed. actual value: %s", spec.minObj.toString(), fieldValue.toString());
                    addValueError(res, spec, fieldValue, msg);
                }
            }
            if (spec.maxObj != null) {
                if (compareValues(fieldValue, spec.maxObj) > 0) {
                    String msg = String.format("max(%s) failed. actual value: %s", spec.maxObj.toString(), fieldValue.toString());
                    addValueError(res, spec, fieldValue, msg);
                }
            }
            if (spec.minRangeObj != null && spec.maxRangeObj != null) {
                if (compareValues(fieldValue, spec.minRangeObj) < 0) {
                    String msg = String.format("range(%s,%s) failed. actual value: %s", spec.minRangeObj.toString(), spec.maxRangeObj.toString(),
                                    fieldValue.toString());
                    addValueError(res, spec, fieldValue, msg);
                } else if (compareValues(fieldValue, spec.maxRangeObj) > 0) {
                    String msg = String.format("range(%s,%s) failed. actual value: %s", spec.minRangeObj.toString(), spec.maxRangeObj.toString(),
                            fieldValue.toString());
                    addValueError(res, spec, fieldValue, msg);
                }
            }
            if (spec.inList != null) {
                boolean found = false;
                for(Number el: spec.inList) {
                    if (compareValues(fieldValue, el) == 0) {
                        found = true;
                        break;
                    }
                }

                if (! found) {
                    String elStr = spec.inList.stream().map(Object::toString)
                            .collect(Collectors.joining(","));
                    String msg = String.format("in(%s) failed. actual value: %s", elStr, fieldValue.toString());
                    addValueError(res, spec, fieldValue, msg);
                }
            }
            return res;
        }

        private void addValueError(ValidationResults res, ValSpec spec, Object fieldValue, String message) {
            FieldError err = new FieldError(spec.fieldName, fieldValue, ErrorType.VALUE);
            err.errMsg = String.format("field '%s': %s", spec.fieldName, message);
            res.errL.add(err);
        }
        private void addNotNullError(ValidationResults res, ValSpec spec, String message) {
            FieldError err = new FieldError(spec.fieldName, null, ErrorType.NOT_NULL);
            err.errMsg = String.format("field '%s': %s", spec.fieldName, message);
            res.errL.add(err);
        }

        private int compareValues(Object fieldValue, Object minObj) {
            if (fieldValue instanceof Integer) {
                Integer min = NumberUtils.asInt(minObj);
                return ((Integer) fieldValue).compareTo(min);
            }
            if (fieldValue instanceof Long) {
                Long min = NumberUtils.asLong(minObj);
                return ((Long) fieldValue).compareTo(min);
            }
            if (fieldValue instanceof Float) {
                Float min = NumberUtils.asFloat(minObj);
                return ((Float) fieldValue).compareTo(min);
            }
            if (fieldValue instanceof Double) {
                Double min = NumberUtils.asDouble(minObj);
                return ((Double) fieldValue).compareTo(min);
            }

            throw new FieldValidateException("compareValues failed. unsupported type");
//            return -1;
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
        public Object minRangeObj;
        public Object maxRangeObj;
        public FieldValidateTests.Val1 elementsVal;
        public ValidateBuilder subBuilder;
        public ValidateBuilder mapBuilder;
        public List<Number> inList;


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
        private Object minRangeObj;
        private Object maxRangeObj;
        private Val1 elementsVal;
        private ValidateBuilder subBuilder;
        private ValidateBuilder mapBuilder;
        private List<Number> inList;

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
            spec.minRangeObj = minRangeObj;
            spec.maxRangeObj = maxRangeObj;
            spec.inList = inList;
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
        public Val1 range(int min, int max) {
            minRangeObj = Integer.valueOf(min);
            maxRangeObj = Integer.valueOf(max);
            return this;
        }
        public Val1 range(long min, long max) {
            minRangeObj = Long.valueOf(min);
            maxRangeObj = Long.valueOf(max);
            return this;
        }
        public Val1 range(double min, long max) {
            minRangeObj = Double.valueOf(min);
            maxRangeObj = Double.valueOf(max);
            return this;
        }
        //in has above types and char,string
        public Val1 in(int... vals) {
            this.inList = new ArrayList<Number>();
            for(int n: vals) {
                Integer nval = Integer.valueOf(n);
                inList.add(nval);
            }
            return this;
        }
        public Val1 in(long... vals) {
            this.inList = new ArrayList<Number>();
            for(long n: vals) {
                Long nval = Long.valueOf(n);
                inList.add(nval);
            }
            return this;
        }
        public Val1 in(double... vals) {
            this.inList = new ArrayList<Number>();
            for(double n: vals) {
                Double nval = Double.valueOf(n);
                inList.add(nval);
            }
            return this;
        }

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
        private boolean haveBuiltLast;

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
            if (haveBuiltLast) return;
            if (!list.isEmpty()) {
                Val1 val = list.get(list.size() - 1);
                val.buildAndAddSpec();
                haveBuiltLast = true;
            }
        }
    }


    public static class Home {
        private int points;
        private String[] names;
        private String lastName;
        private long id;
        private double weight;


        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

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

        Home home = new Home();
        home.setPoints(30);
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "min(50)");

        home.setPoints(50);
        res = runOK(vb, home);
    }
    @Test
    public void testMinLong() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("id").notNull().min(50);

        Home home = new Home();
        home.setId(30);
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "min(50)");

        home.setId(50);
        res = runOK(vb, home);
    }
    @Test
    public void testMinDouble() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("weight").notNull().min(50.0);

        Home home = new Home();
        home.setWeight(30.0);
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "min(50.0)");

        home.setWeight(50.0);
        res = runOK(vb, home);
    }

    @Test
    public void testMax() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("points").notNull().max(50);

        Home home = new Home();
        home.setPoints(51);
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "max(50)");

        home.setPoints(50);
        res = runOK(vb, home);
    }
    @Test
    public void testRange() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("points").notNull().range(1,10);

        Home home = new Home();
        home.setPoints(51);
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "range(1,10)");

        home.setPoints(0);
        res = runFail(vb, home, 1);
        chkValueErr(res, 0, "range(1,10)");

        home.setPoints(1);
        res = runOK(vb, home);
        home.setPoints(10);
        res = runOK(vb, home);
    }

    @Test
    public void testIn() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("points").notNull().in(3,4,5);

        Home home = new Home();
        home.setPoints(51);
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "in(3,4,5)");

        home.setPoints(5);
        res = runOK(vb, home);
    }
    @Test
    public void testInDouble() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("weight").notNull().in(3.1,4.2,5.3);

        Home home = new Home();
        home.setWeight(51.0);
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "in(3.1,4.2,5.3)");

        home.setWeight(3.1);
        res = runOK(vb, home);
    }



    //--
    private ValidationResults runOK(ValidateBuilder vb, Home home) {
        Validator runner = vb.build();
        ValidationResults res = runner.validate(home);
        assertEquals(true, res.hasNoErrors());
        return res;
    }

    private ValidationResults runFail(ValidateBuilder vb, Home home, int size) {
        Validator runner = vb.build();
        ValidationResults res = runner.validate(home);
        chkFail(res, size);
        return res;
    }

    private void chkFail(ValidationResults res, int expected) {
        for(FieldError err: res.errL) {
            log(err.errMsg);
        }
        assertEquals(false, res.hasNoErrors());
        assertEquals(expected, res.errL.size());
    }
    private void chkValueErr(ValidationResults res, int index, String expected) {
        assertEquals(false, res.hasNoErrors());
        FieldError err = res.errL.get(index);
        assertEquals(true, err.errMsg.contains(expected));
    }

}
