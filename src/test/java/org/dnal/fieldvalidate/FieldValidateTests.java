package org.dnal.fieldvalidate;

import static org.junit.Assert.assertEquals;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.dnal.fieldcopy.BaseTest;
import org.dnal.fieldvalidate.code.ErrorType;
import org.dnal.fieldvalidate.code.FieldError;
import org.dnal.fieldvalidate.code.FieldValidateException;
import org.dnal.fieldvalidate.code.NumberUtils;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO
 * DONE -custom error message. can set on per use basis
 * -custom rule such as emailRule
 * -enum (from string)
 */

public class FieldValidateTests extends BaseTest {

    public static class RuleContext {
        public Object root; //if null then means same as target
        public Object target;
        public Integer index;
    }

    public interface ValidationRule {
        boolean canExecute(ValSpec spec);
        void validate(ValSpec spec,  Object fieldValue, ValidationResults res, RuleContext ctx);
        String getName();
    }
    public static abstract class ValidationRuleBase implements ValidationRule {

        @Override
        public String getName() {
            String name = this.getClass().getSimpleName();
            if (name.endsWith("Rule")) {
                name = StringUtils.substringBeforeLast(name, "Rule");
            }
            return name.toLowerCase();
        }

        @Override
        public abstract boolean canExecute(ValSpec spec);

        @Override
        public abstract void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx);

        protected int compareStrValues(Object fieldValue, String el) {
            return fieldValue.toString().compareTo(el);
        }

        protected void addValueError(ValidationResults res, ValSpec spec, Object fieldValue, String message, RuleContext ctx) {
            FieldError err = new FieldError(ctx.target.getClass().getSimpleName(), spec.fieldName, ctx.index, fieldValue, ErrorType.VALUE);
            err.fullTargetPath = FieldError.buildTargetPath(ctx.root, ctx.target, spec.fieldName, ctx.index);
            err.errMsg = String.format("%s: %s", err.fullTargetPath, message);
            res.errL.add(err);
        }
        protected int compareValues(Object fieldValue, Object minObj) {
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
        }

    }
    public static class MinRule extends ValidationRuleBase {
        @Override
        public boolean canExecute(ValSpec spec) {
            return spec.minObj != null;
        }

        @Override
        public void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx) {
            if (compareValues(fieldValue, spec.minObj) < 0) {
                String msg = String.format("min(%s) failed. actual value: %s", spec.minObj.toString(), fieldValue.toString());
                addValueError(res, spec, fieldValue, msg, ctx);
            }
        }
    }
    public static class MaxRule extends ValidationRuleBase {
        @Override
        public boolean canExecute(ValSpec spec) {
            return spec.maxObj != null;
        }

        @Override
        public void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx) {
            if (compareValues(fieldValue, spec.maxObj) > 0) {
                String msg = String.format("max(%s) failed. actual value: %s", spec.maxObj.toString(), fieldValue.toString());
                addValueError(res, spec, fieldValue, msg, ctx);
            }
        }
    }
    public static class RangeRule extends ValidationRuleBase {
        @Override
        public boolean canExecute(ValSpec spec) {
            return (spec.minRangeObj != null && spec.maxRangeObj != null);
        }

        @Override
        public void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx) {
            if (compareValues(fieldValue, spec.minRangeObj) < 0) {
                String msg = String.format("range(%s,%s) failed. actual value: %s", spec.minRangeObj.toString(), spec.maxRangeObj.toString(),
                        fieldValue.toString());
                addValueError(res, spec, fieldValue, msg, ctx);
            } else if (compareValues(fieldValue, spec.maxRangeObj) > 0) {
                String msg = String.format("range(%s,%s) failed. actual value: %s", spec.minRangeObj.toString(), spec.maxRangeObj.toString(),
                        fieldValue.toString());
                addValueError(res, spec, fieldValue, msg, ctx);
            }
        }
    }
    public static class InNumericRule extends ValidationRuleBase {
        @Override
        public boolean canExecute(ValSpec spec) {
            return (spec.inList != null);
        }

        @Override
        public void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx) {
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
                addValueError(res, spec, fieldValue, msg, ctx);
            }
        }
    }
    public static class InStringRule extends ValidationRuleBase {
        @Override
        public boolean canExecute(ValSpec spec) {
            return (spec.inStrList != null);
        }

        @Override
        public void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx) {
            boolean found = false;
            for(String el: spec.inStrList) {
                if (compareStrValues(fieldValue, el) == 0) {
                    found = true;
                    break;
                }
            }
            if (! found) {
                String elStr = spec.inStrList.stream().map(Object::toString)
                        .collect(Collectors.joining(","));
                String msg = String.format("in(%s) failed. actual value: %s", elStr, fieldValue.toString());
                addValueError(res, spec, fieldValue, msg, ctx);
            }
        }
    }
    public static class MaxLenRule extends ValidationRuleBase {
        @Override
        public boolean canExecute(ValSpec spec) {
            return (spec.strMaxLen != null);
        }

        @Override
        public void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx) {
            int len = fieldValue.toString().length();
            if (len > spec.strMaxLen.intValue()) {
                String msg = String.format("maxlen(%d) failed. actual value: %s", spec.strMaxLen, fieldValue.toString());
                addValueError(res, spec, fieldValue, msg, ctx);
            }
        }
    }
    public static class SubObjRule extends ValidationRuleBase {
        private Validator subValidator;

        @Override
        public boolean canExecute(ValSpec spec) {
            return (spec.subBuilder != null);
        }

        @Override
        public void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx) {
            if (spec.subBuilder != null && subValidator == null) {
                subValidator = spec.subBuilder.build();
            }
            ValidationResults innerRes = subValidator.validate(fieldValue, ctx.target, null);

            if (! innerRes.hasNoErrors()) {
                res.errL.addAll(innerRes.errL);
            }
        }
    }
    public static class ElementsRule extends ValidationRuleBase {
        private Validator subValidator;

        @Override
        public boolean canExecute(ValSpec spec) {
            return (spec.elementsVal != null);
        }

        @Override
        public void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx) {
            if (spec.elementsVal != null && subValidator == null) {
                spec.elementsVal.buildAndAddSpec();
                this.subValidator = new Validator(spec.elementsVal.specList);
            }

            List<?> lll = (List<?>) fieldValue; //TODO need to support all collections and arrays
            for(int i = 0; i < lll.size(); i++) {
//                ValidationResults innerRes = subValidator.validate(fieldValue, ctx.target);
                ValidationResults innerRes = subValidator.validate(ctx.target, ctx.target, i);

                if (! innerRes.hasNoErrors()) {
                    res.errL.addAll(innerRes.errL);
                }

            }
        }
    }

    public interface RuleLambda {
        String eval(Object fieldValue, RuleContext ctx);
    }
    public interface RuleCondition extends RuleLambda {
        String getName();
    }
    public static class EvalRule extends ValidationRuleBase {
        private RuleLambda evalRule;

        @Override
        public String getName() {
            if (evalRule instanceof RuleCondition) {
                return ((RuleCondition) evalRule).getName();
            }
            return super.getName();
        }

        @Override
        public boolean canExecute(ValSpec spec) {
            return (spec.evalRule != null);
        }

        @Override
        public void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx) {
            this.evalRule = spec.evalRule; //for getName

            String errStr = spec.evalRule.eval(fieldValue, ctx);
            if (errStr != null) {
                this.addValueError(res, spec, fieldValue, errStr, ctx);
            }
        }
    }

    public interface CustomErrorMessageBuilder {
        String buildMessage(FieldError err);
    }

    public static class Validator {
        private final List<ValSpec> specList;
        private Object target;
        private List<ValidationRule> ruleList = new ArrayList<>();
        private CustomErrorMessageBuilder customErrorMessageBuilder;

        public Validator(List<ValSpec> specList) {
            this.specList = specList;
            this.ruleList.add(new MinRule());
            this.ruleList.add(new MaxRule());
            this.ruleList.add(new RangeRule());
            this.ruleList.add(new InNumericRule());
            this.ruleList.add(new InStringRule());
            this.ruleList.add(new MaxLenRule());
            this.ruleList.add(new EvalRule());
            this.ruleList.add(new SubObjRule());
            this.ruleList.add(new ElementsRule());

            //set spec.runner
            for(ValSpec spec: specList) {
                if (spec.runner != null) {
                    continue;
                }
                for(ValidationRule rule: ruleList) {
                    if (rule.canExecute(spec)) {
                        spec.runner = createNewInstance(rule); //each spec needs its own rule instance
                        break;
                    }
                }
                //Note. if spec only has isNotNull then runner will be null. which is ok.
            }
        }

        private ValidationRule createNewInstance(ValidationRule rule) {
            ValidationRule copy = null;
            try {
                copy = rule.getClass().getDeclaredConstructor().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return copy;
        }

        public ValidationResults validate(Object target) {
            return validate(target, null, null);
        }
        public ValidationResults validate(Object target, Object rootTarget, Integer index) {
            ValidationResults res =  new ValidationResults();
            for(ValSpec spec: specList) {
                try {
                    doValidate(target, spec, res, rootTarget, index);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (customErrorMessageBuilder != null) {
                for(FieldError err: res.errL) {
                    String msg = customErrorMessageBuilder.buildMessage(err);
                    err.errMsg = msg;
                }
            }

            return res;
        }

        private ValidationResults doValidate(Object target, ValSpec spec, ValidationResults res, Object rootTarget, Integer index) throws Exception {
            Object fieldValue;
            if (index != null) {
                String s = String.format("%s[%d]", spec.fieldName, index.intValue());
                fieldValue = PropertyUtils.getProperty(target, s);
            } else {
                 fieldValue = PropertyUtils.getProperty(target, spec.fieldName);
            }

            if (fieldValue == null && spec.isNotNull) {
                String msg = String.format("unexpected null value");
                addNotNullError(res, spec, msg, target, rootTarget, index);
            }

            RuleContext ctx = new RuleContext();
            ctx.target = target;
            ctx.root = rootTarget;
            ctx.index = index;
//            for(ValidationRule rule: ruleList) {
//                if (rule.canExecute(spec)) {
//                    rule.validate(spec, fieldValue, res, ctx);
//                }
//            }
            //if only isNotNull then will be no runner
            if (fieldValue != null && spec.runner != null) {
                spec.runner.validate(spec, fieldValue, res, ctx);
            }

            return res;
        }

        private void addNotNullError(ValidationResults res, ValSpec spec, String message, Object target, Object rootTarget, Integer index) {
            FieldError err = new FieldError(target.getClass().getSimpleName(), spec.fieldName, index,null, ErrorType.NOT_NULL);
            err.fullTargetPath = FieldError.buildTargetPath(rootTarget, target, spec.fieldName, index);
            err.errMsg = String.format("%s: %s", err.fullTargetPath, message);
            res.errL.add(err);
        }

        public List<ValSpec> getSpecList() {
            return specList;
        }

        public CustomErrorMessageBuilder getCustomErrorMessageBuilder() {
            return customErrorMessageBuilder;
        }
        public void setCustomErrorMessageBuilder(CustomErrorMessageBuilder customErrorMessageBuilder) {
            this.customErrorMessageBuilder = customErrorMessageBuilder;
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
        public ArrayList<String> inStrList;
        public Integer strMaxLen;
        public RuleLambda evalRule;
        public ValidationRule runner; //set by Validator

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
        final List<ValSpec> specList;
        private boolean isNotNull;
        private Object minObj;
        private Object maxObj;
        private Object minRangeObj;
        private Object maxRangeObj;
        private Val1 elementsVal;
        private ValidateBuilder subBuilder;
        private ValidateBuilder mapBuilder;
        private List<Number> inList;
        private ArrayList<String> inStrList;
        private Integer strMaxLen;
        private RuleLambda evalRule;

        public Val1(String fieldName, List<Val1> list, List<ValSpec> specList) {
            this.fieldName = fieldName;
            this.list = list;
            this.specList = specList;
        }

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
            spec.inStrList = inStrList;
            spec.strMaxLen = strMaxLen;
            spec.evalRule = evalRule;
            
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
        public Val1 in(String... vals) {
            this.inStrList = new ArrayList<String>();
            for(String s: vals) {
                inStrList.add(s);
            }
            return this;
        }
        public Val1 maxlen(int maxlen) {
            this.strMaxLen = maxlen;
            return this;
        }

        //TODO: implement a runner for this
        public Val1 elements() {
            this.elementsVal = new Val1(fieldName, list, new ArrayList<>());
            return elementsVal;
        }
        public Val1 subObj(ValidateBuilder subBuilder) {
            this.subBuilder = subBuilder;
            return this;
        }

        //TODO: implement a runner for this
        public Val1 mapField(ValidateBuilder vb3) {
            this.mapBuilder = vb3;
            return this;
        }

        public Val1 eval(RuleLambda rule) {
            this.evalRule = rule;
            return this;
        }
//        public Val1 eval(RuleLambda rule) {
//            this.evalRule = rule;
//            return this;
//        }

    }
    public static class ValidateBuilder {
        private List<Val1> list = new ArrayList<>();
        private List<ValSpec> specList = new ArrayList<>();
        private boolean haveBuiltLast;

        public Val1 field(String fieldName) {
            buildSpecForLastVal();
            haveBuiltLast = false; //reset
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
    public static class Home {
        private int points;
        private String[] names;
        private String lastName;
        private long id;
        private Double weight;
        private Address addr;
        private List<Integer> zones = new ArrayList<>();

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

    public static class MyCustomMsgBuilder implements  CustomErrorMessageBuilder {

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

    //--
    private CustomErrorMessageBuilder customMessageBuilder;


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
