package org.dnal.fieldcopy.bdd;


import org.dnal.fieldcopy.bdd.core.BDDMode;
import org.dnal.fieldcopy.bdd.core.BDDTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BDDTests extends BDDTestBase {

    /*
     R100 value->field
     number,char,bool,string,date
     -for prims
     -for scalars
     -list,etc not supported
     -date
     -enum
     -null as a value
     -optional dest  -test int,string,enum,date
    */
    @Test
    public void testR100() {
        runR100(100, "t0-values.txt",2);
        runR100(101, "t0-values-null.txt",1);
        runR100(102, "t0-values-optional.txt",1);
    }

    /*
     R200 field->field //no-conversion
     -empty/ no fields
     -number,char,bool,string,date
     -for prims
     -for scalars
     -list,etc
     -null src value handled correctly
     -date
     -enum
     -NonO->O,O->NonO,O->O -test int,string,list,enum,date
    */
    @Test
    public void testR200() {
        runR200(200, "t0-happy-path.txt", 3);
        runR200(201, "t0-empty.txt", 1);
        runR200(202, "t0-fields.txt", 3);
        runR200(203, "t0-fields-list.txt", 3);
        runR200(203, "t0-fields-optional1.txt", 3);
        runR200(204, "t0-fields-optional2.txt", 3);
        runR200(205, "t0-fields-optional3.txt", 3);
        //TODO runR200(203, "t0-fields-optional-list.txt", 3);
    }
    @Test
    public void testR250() {
        runR250("t0-all-prims.txt", 2);
        runR250("t0-all-scalars.txt", 3);
    }

    /*
     R300 field->field //implicit-conversion
     -we have lower-level tests of all the combinations
     -number,char,bool,string,date
     -prim -> prim
     -prim -> scalar
     -scalar -> prim
     -scalar -> scalar
     -list,etc
     -null src value handled correctly
     -date
     -enum
     -NonO->O,O->NonO,O->O -test int,string,list,enum,date
    */
    @Test
    public void testR300() {
    }

    /*
     R400 required
     -means src value is not null, fail if null
     -value -> field //fail if value null
     -field -> field
     -prim //required makes no sense
     -scalar
     -list,date,enum
     -NonO->O,O->NonO,O->O
    */
    @Test
    public void testR400() {
    }

    /*
     R500 default
     -means we use defaultVal if src value is null
     -defaultVal can be null
     -value -> field
     -field -> field
     -prim
     -scalar
     -list,date,enum
     -NonO->O,O->NonO,O->O
    */
    @Test
    public void testR500() {
        runR500("t0-default.txt", 2);
        runR500(501, "t0-default-enum.txt", 2);
    }

    /*
     R550 skipNull
     -means we use defaultVal if src value is null
     -defaultVal can be null
     -value -> field
     -field -> field
     -prim
     -scalar
     -list,date,enum
     -NonO->O,O->NonO,O->O
    */
    @Test
    public void testR550() {
    }

    /*
     R600  custom
     -codegen creates abstract base class
     -and creates method for the field
    */
    @Test
    public void testR600() {
    }

    /*
     R700 auto and exclude
     -test prim,scal,list,enum,date
     -mainly test that all fields are covered
    */
    @Test
    public void testR700() {
        runR700("t0-auto.txt", 2);
    }

    /*
     R800 field vs getter
     -test both
     -test that if both present we use getter and setter
    */
    @Test
    public void testR800() {
    }

    /*
     R900 date format
     -format(...) modifier in the json on a field
     -fns like year(),month()...
     -default date/time formats used by value->field is changeable
    */
    @Test
    public void testR900() {
        runR900("t0-date-fmt.txt", 1);
        runR900(901, "t0-date-fmt2.txt", 1);
        runR900(902, "t0-date-fmt-value.txt", 1);
    }

    /*
     TODO: R1000 using
     -using(myConverter) on a field in the json
     -used when we have multiple possible converters and we're saying which one
    */
    @Test
    public void testR1000() {
        runR1000( "t0-using-named-converter1.txt",1);
    }

    /*
     R1100 sub-obj
     -addr -> addr
     -list<addr> -> list<addr>
     -Opt and NonOpt of addr
     -uses another converter that you must have defined
     -null src value handled correctly
     -addtional converter
    */
    @Test
    public void testR1100() {
        runR1100("t0-subobj.txt", 1);
        runR1100(601, "t0-subobj-to-value.txt", 1);
        runR1100(602, "t0-subobj-to-value-shared.txt", 1);
        runR1100(603, "t0-value-to-subobj-shared.txt", 1);
        runR1100(604, "t0-subobj-additionalconverter.txt", 1);
        runR1100(604, "t0-subobj-address.txt", 1);
    }

    /*
     R1200 sub-obj drill-down
     addr.city -> field
     field -> addr.city
     addr.city -> addr.city
     -do all 8 combos of a.b.c -> a.b.c for both optional and not
     -list,enum,date
     -null src value handled correctly
     -null intermediate value handled correctly
    */
    @Test
    public void testR1200() {
    }

    /*
     R1300 custom object converter
     -define 0,1,more
     -define one and then override with another one
     -gets used for cust.inner1
    */
    @Test
    public void testR1300() {
        runR1300("t0-custom-converter1.txt", 1);
        runR1300(1301, "t0-custom-converter2.txt",1);
        runR1300(1302, "t0-custom-converter3.txt",1);
        runR1300(1303, "t0-custom-converter4.txt",1);
        runR1300(1304, "t0-custom-named-converter1.txt",1);
        runR1300(1305, "t0-custom-converter5.txt",1);
        runR1300(1306, "t0-custom-named-converter1.txt",1);
    }

    /*
     R1400 inheritance
     -works for both base and derived class fields
     -auto
    */
    @Test
    public void testR1400() {
    }

    /*
     R1500 field options
     -disable field comment
     -defaultSourcePackage and defaultDestPackage
    */
    @Test
    public void testR1500() {
    }

    /*
     R1600 coverage detection
     -way of detecting that 'all' fields were copied
     -also 'all' - exclude list
    */
    @Test
    public void testR1600() {
    }

    /*
     R1700  codegen
     -fieldcopy picks the converter class name
     -explicit converter name
    */
    @Test
    public void testR1700() {
    }

    /*
     R1800  fluent
     FieldCopy fc = FieldCopy.using(ConverterGroup.class).addConverter(new MyConverter())
     .timeZone(ZoneId.xx),     .dateFormat("sdfsd"),     .timeFormat("sdfsd"),     .dateTimeFormat("sdfsd"),     .zonedDateFormat("sdfsd"),
    */
    @Test
    public void testR1800() {
    }

    /*
     R1900  runtime
     -use already generated converters
     -actually use them
     -handle null correctly
    */
    @Test
    public void testR1900() {
    }

    /*
     R2000  failure tests
     -bad json  FieldCopyJsonParserTests.testParseBadJson
     -bad field -> field syntax R2000FailureTests.testBadField
     -can't parse value  R2000FailureTests.testBadValue
     -can't find class
     -can't find value
     -no converter for X
     -conversion not supported
     -path not found //codegen
     -null-not-allowed
     -not-impl-yet
     */
    @Test
    public void testR2000() {
    }


    @Test
    public void testRunDebug() {
        singleTestToRunIndex = 0;
//        mode = BDDMode.RUNTIME;
//        runR1000( "t0-using-named-converter1.txt",1);
    }

    //---
    @BeforeEach
    public void init() {
        mode = BDDMode.CODEGEN;
//        mode = BDDMode.RUNTIME;
//        if (testCounter == 0) {
//            if (BDDMode.isCodeGen(mode)) {
//                deleteGeneratedJavaFiles();
//            }
//        }
//        testCounter++;
    }
}
