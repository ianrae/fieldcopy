package org.dnal.fieldvalidate.code;

import java.util.ArrayList;
import java.util.List;

public class BuilderFluent1 {
    private final String fieldName;
    private final List<BuilderFluent1> list;
    final List<ValSpec> specList;
    private boolean isNotNull;
    private Object minObj;
    private Object maxObj;
    private Object minRangeObj;
    private Object maxRangeObj;
    private BuilderFluent1 elementsVal;
    private ValidateBuilder subBuilder;
    private ValidateBuilder mapBuilder;
    private List<Number> inList;
    private ArrayList<String> inStrList;
    private Integer strMaxLen;
    private RuleLambda evalRule;
    private Class<? extends Enum> enumClass;
    private Double deltaObj;

    public BuilderFluent1(String fieldName, List<BuilderFluent1> list, List<ValSpec> specList) {
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
        spec.deltaObj = deltaObj;
        spec.minRangeObj = minRangeObj;
        spec.maxRangeObj = maxRangeObj;
        spec.inList = inList;
        spec.inStrList = inStrList;
        spec.strMaxLen = strMaxLen;
        spec.evalRule = evalRule;
        spec.enumClass = enumClass;

        specList.add(spec);
    }

    public BuilderFluent1 notNull() {
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
    public BuilderFluent1 min(int n) {
        minObj = Integer.valueOf(n);
        return this;
    }

    public BuilderFluent1 min(long n) {
        minObj = Long.valueOf(n);
        return this;
    }

    public BuilderFluent1 min(double n) {
        minObj = Double.valueOf(n);
        return this;
    }

    //max
    public BuilderFluent1 max(int n) {
        maxObj = Integer.valueOf(n);
        return this;
    }

    public BuilderFluent1 max(long n) {
        maxObj = Long.valueOf(n);
        return this;
    }

    public BuilderFluent1 max(double n) {
        maxObj = Double.valueOf(n);
        return this;
    }

    //range
    public BuilderFluent1 range(int min, int max) {
        minRangeObj = Integer.valueOf(min);
        maxRangeObj = Integer.valueOf(max);
        return this;
    }

    public BuilderFluent1 range(long min, long max) {
        minRangeObj = Long.valueOf(min);
        maxRangeObj = Long.valueOf(max);
        return this;
    }

    public BuilderFluent1 range(double min, double max) {
        minRangeObj = Double.valueOf(min);
        maxRangeObj = Double.valueOf(max);
        return this;
    }

    //in has above types and char,string
    public BuilderFluent1 in(int... vals) {
        this.inList = new ArrayList<Number>();
        for (int n : vals) {
            Integer nval = Integer.valueOf(n);
            inList.add(nval);
        }
        return this;
    }

    public BuilderFluent1 in(long... vals) {
        this.inList = new ArrayList<Number>();
        for (long n : vals) {
            Long nval = Long.valueOf(n);
            inList.add(nval);
        }
        return this;
    }

    public BuilderFluent1 in(double... vals) {
        this.inList = new ArrayList<Number>();
        for (double n : vals) {
            Double nval = Double.valueOf(n);
            inList.add(nval);
        }
        return this;
    }

    public BuilderFluent1 in(String... vals) {
        this.inStrList = new ArrayList<String>();
        for (String s : vals) {
            inStrList.add(s);
        }
        return this;
    }

    public BuilderFluent1 maxlen(int maxlen) {
        this.strMaxLen = maxlen;
        return this;
    }

    //TODO: implement a runner for this
    public BuilderFluent1 elements() {
        this.elementsVal = new BuilderFluent1(fieldName, list, new ArrayList<>());
        return elementsVal;
    }

    public BuilderFluent1 subObj(ValidateBuilder subBuilder) {
        this.subBuilder = subBuilder;
        return this;
    }

    //TODO: implement a runner for this
    public BuilderFluent1 mapField(ValidateBuilder vb3) {
        this.mapBuilder = vb3;
        return this;
    }

    public BuilderFluent1 eval(RuleLambda rule) {
        this.evalRule = rule;
        return this;
    }

    public void inEnum(Class<? extends Enum> enumClass) {
        this.enumClass = enumClass;
    }

    public BuilderFluent1 delta(double delta) {
        this.deltaObj = delta;
        return this;
    }
//        public Val1 eval(RuleLambda rule) {
//            this.evalRule = rule;
//            return this;
//        }

}
