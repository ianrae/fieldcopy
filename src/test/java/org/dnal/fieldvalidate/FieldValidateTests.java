package org.dnal.fieldvalidate;

import static org.junit.Assert.assertEquals;

import org.apache.commons.beanutils.PropertyUtils;
import org.dnal.fieldcopy.BaseTest;
import org.dnal.fieldvalidate.code.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 * DONE -custom error message. can set on per use basis
 * DONE -custom rule such as emailRule
 * DONE -enum (from string)
 */

public class FieldValidateTests extends BaseTest {

    public static class Address {
        private String street;
        private String city;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }
    }
    public static enum Color { RED,GREEN,BLUE };
    public static class Home {
        private int points;
        private String[] names;
        private String lastName;
        private long id;
        private Double weight;
        private Address addr;
        private List<Integer> zones = new ArrayList<>();
        private Integer[] arSizes;
        private Color color;
        private String colorStr;

        public Address getAddr() {
            return addr;
        }
        public void setAddr(Address addr) {
            this.addr = addr;
        }
        public Double getWeight() {
            return weight;
        }

        public void setWeight(Double weight) {
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
        public List<Integer> getZones() {
            return zones;
        }

        public void setZones(List<Integer> zones) {
            this.zones = zones;
        }
        public Integer[] getArSizes() {
            return arSizes;
        }
        public void setArSizes(Integer[] arSizes) {
            this.arSizes = arSizes;
        }
        public Color getColor() {
            return color;
        }
        public void setColor(Color color) {
            this.color = color;
        }

        public String getColorStr() {
            return colorStr;
        }

        public void setColorStr(String colorStr) {
            this.colorStr = colorStr;
        }
    }
    public static class MyRule implements RuleCondition {

        @Override
        public String getName() {
            return "myrule";
        }

        @Override
        public String eval(Object fieldValue, RuleContext ctx) {
            if (fieldValue.toString().equals("fail")) {
                return "abc";
            }
            return null;
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
    public void testNotNull() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("lastName").notNull();

        Home home = new Home();
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "unexpected null value");
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

        //TODO: add double delta for comparison
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
    public void testMaxBad() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("lastName").notNull().max(50);

        Home home = new Home();
        home.setLastName("bob");
        runFailWithException(vb, home, 1);
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
    @Test
    public void testInString() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("lastName").notNull().in("Jones", "Smith");

        Home home = new Home();
        home.setLastName("Wilson");
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "in(Jones,Smith)");

        home.setLastName("Smith");
        res = runOK(vb, home);
    }

    @Test
    public void testMaxLen() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("lastName").notNull().maxlen(4);

        Home home = new Home();
        home.setLastName("Wilson");
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "maxlen(4)");

        home.setLastName("Sue");
        res = runOK(vb, home);
    }

    @Test
    public void testEval() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("lastName").notNull().eval(new MyRule());

        Home home = new Home();
        home.setLastName("fail");
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "abc");

        home.setLastName("Sue");
        res = runOK(vb, home);
    }
    @Test
    public void testEvalLambda() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("lastName").notNull().eval((Object fieldValue, RuleContext ctx) -> {
            if (fieldValue.toString().equals("fail")) {
                return "abc";
            } else return null;
        });

        Home home = new Home();
        home.setLastName("fail");
        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "abc");

        home.setLastName("Sue");
        res = runOK(vb, home);
    }

    @Test
    public void testMulti() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("lastName").notNull().maxlen(4);
        vb.field("points").notNull().max(100);
        vb.field("weight").max(50.0);

        Home home = new Home();
        home.setLastName("Wilson");
        home.setPoints(101);
        ValidationResults res = runFail(vb, home, 2);
        chkValueErr(res, 0, "maxlen(4)");
        chkValueErr(res, 1, "max(100)");

//        home.setLastName("Sue");
//        res = runOK(vb, home);
    }

    @Test
    public void testName() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("lastName").notNull().maxlen(4);

        Validator runner = vb.build();
        ValidationRule rule = runner.specList.get(0).runner;
        assertEquals("maxlen", rule.getName());
    }

    public static class MyCustomMsgBuilder implements ErrorMessageBuilder {

        @Override
        public String buildMessage(FieldError err) {
            if (err.targetClassName.equals("Address") && err.fieldName.equals("city")) {
                return "Address.city must not exceed 4 chars";
            }
            return err.errMsg;
        }
    }

    @Test
    public void testSubObj() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("lastName").notNull().maxlen(4);
        vb.field("points").notNull().max(100);

        ValidateBuilder vb2 = new ValidateBuilder();
        vb2.field("city").notNull().maxlen(4);
        vb.field("addr").subObj(vb2);

        Home home = new Home();
        home.setLastName("Wilson");
        home.setPoints(100);

        Address addr = new Address();
        addr.setCity("ottawa");
        addr.setStreet("main");
        home.setAddr(addr);

        ValidationResults res = runFail(vb, home, 2);
        chkValueErr(res, 0, "maxlen(4)");
        chkValueErr(res, 1, "maxlen(4)");
    }

    @Test
    public void testCustomErrorMessage() {
        this.customMessageBuilder = new MyCustomMsgBuilder();
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("lastName").notNull().maxlen(4);
        vb.field("points").notNull().max(100);

        ValidateBuilder vb2 = new ValidateBuilder();
        vb2.field("city").notNull().maxlen(4);
        vb.field("addr").subObj(vb2);

        Home home = new Home();
        home.setLastName("Wilson");
        home.setPoints(100);

        Address addr = new Address();
        addr.setCity("ottawa");
        addr.setStreet("main");
        home.setAddr(addr);

        ValidationResults res = runFail(vb, home, 2);
        chkValueErr(res, 0, "maxlen(4)");
        chkValueErr(res, 1, "not exceed 4");
    }

    @Test
    public void testList() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("zones").notNull().elements().max(100);

        Home home = new Home();
        home.getZones().add(10);
        home.getZones().add(102);

        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "max(100)");
    }
    @Test
    public void testArray() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("arSizes").notNull().elements().max(100);

        Home home = new Home();
        Integer ar[] = new Integer[] {45, 50, 111};
        home.setArSizes(ar);

        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "max(100)");
    }
    @Test
    public void testEnum() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("color").notNull().in("RED","BLUE");

        Home home = new Home();
        Integer ar[] = new Integer[] {45, 50, 111};
        home.setColor(Color.GREEN);

        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "in(RED,BLUE)");
    }
    @Test
    public void testEnumBad() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("arSizes").notNull().in("RED","BLUE");

        Home home = new Home();
        Integer ar[] = new Integer[] {45, 50, 111};
        home.setArSizes(ar);

        runFailWithException(vb, home, 1);
    }

    @Test
    public void testEnumFromString() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("colorStr").notNull().inEnum(Color.class);

        Home home = new Home();
        Integer ar[] = new Integer[] {45, 50, 111};
        home.setColorStr("zz");

        ValidationResults res = runFail(vb, home, 1);
        chkValueErr(res, 0, "inEnum([RED, GREEN, BLUE])");
    }
    @Test
    public void testEnumFromStringBad() {
        ValidateBuilder vb = new ValidateBuilder();
        vb.field("arSizes").notNull().inEnum(Color.class);

        Home home = new Home();
        Integer ar[] = new Integer[] {45, 50, 111};
        home.setArSizes(ar);

        runFailWithException(vb, home, 1);
    }

    //--
    private ErrorMessageBuilder customMessageBuilder;


    private ValidationResults runOK(ValidateBuilder vb, Home home) {
        Validator runner = vb.build();
        runner.setCustomErrorMessageBuilder(customMessageBuilder);
        ValidationResults res = runner.validate(home);
        assertEquals(true, res.hasNoErrors());
        return res;
    }

    private ValidationResults runFail(ValidateBuilder vb, Home home, int size) {
        Validator runner = vb.build();
        runner.setCustomErrorMessageBuilder(customMessageBuilder);
        ValidationResults res = runner.validate(home);
        chkFail(res, size);
        return res;
    }
    private void runFailWithException(ValidateBuilder vb, Home home, int size) {
        Validator runner = vb.build();
        runner.setCustomErrorMessageBuilder(customMessageBuilder);
        boolean thrown = false;
        try {
            runner.validate(home);
        } catch (FieldValidateException e) {
//            e.printStackTrace();
            System.out.println("EXCEPTION: " + e.getMessage());
            thrown = true;
        }
        assertEquals(true, thrown);
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
