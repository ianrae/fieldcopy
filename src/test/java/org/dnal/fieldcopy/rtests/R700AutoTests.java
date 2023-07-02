package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.dataclass.Dest1;
import org.dnal.fieldcopy.dataclass.ExtendedCustomer;
import org.dnal.fieldcopy.dataclass.Src1;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/*
 R700 auto and exclude
 -test prim,scal,list,enum,date
 -mainly test that all fields are covered
*/
public class R700AutoTests extends RTestBase {

    @Test
    public void test() {
        CopySpec spec = new CopySpec(Src1.class, Dest1.class);
        spec.autoFlag = true;
        List<String> lines = doGen(spec);

        String[] ar = {
                "Color tmp1 = src.getCol1();",
                "dest.setCol1(tmp1);",
                "Inner1 tmp2 = src.getInner1();",
                "dest.setInner1(tmp2);",
                "int tmp3 = src.getN1();",
                "dest.setN1(tmp3);",
                "String tmp4 = src.getS2();",
                "dest.setS2(tmp4);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Color", "org.dnal.fieldcopy.dataclass.Inner1");
    }

    @Test
    public void testExclude() {
        CopySpec spec = new CopySpec(Customer.class, Customer.class);
        spec.autoFlag = true;
        spec.autoExcludeFields = Arrays.asList("firstName", "lastName", "points", "time", "ldt", "utilDate", "dateStr", "timeStr", "dateTimeStr", "zonedDateTimeStr");
        List<String> lines = doGen(spec);

        String[] ar = {

                "Address tmp1 = src.getAddr();",
                "dest.setAddr(tmp1);",
                "LocalDate tmp2 = src.getDate();",
                "dest.setDate(tmp2);",
                "int tmp3 = src.getNumPoints();",
                "dest.setNumPoints(tmp3);",
                "List<String> tmp4 = src.getRoles() == null ? null : ctx.createEmptyList(src.getRoles(), String.class);",
                "dest.setRoles(tmp4);",
                "ZonedDateTime tmp5 = src.getZdt();",
                "dest.setZdt(tmp5);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Address", "java.time.LocalDate", "java.util.List", "java.time.ZonedDateTime");
    }

    @Test
    public void testExcludeInheritance() {
        CopySpec spec = new CopySpec(ExtendedCustomer.class, ExtendedCustomer.class);
        spec.autoFlag = true;
        spec.autoExcludeFields = Arrays.asList("firstName", "lastName", "points", "time", "ldt", "utilDate", "dateStr", "timeStr",
                "dateTimeStr", "zonedDateTimeStr", "date", "roles", "zdt");
        List<String> lines = doGen(spec);

        String[] ar = {

                "Address tmp1 = src.getAddr();",
                "dest.setAddr(tmp1);",
                "String tmp2 = src.getFavColor();",
                "dest.setFavColor(tmp2);",
                "int tmp3 = src.getNumPoints();",
                "dest.setNumPoints(tmp3);",
                "ZoneAddress tmp4 = src.getZoneAddr();",
                "dest.setZoneAddr(tmp4);",
        };
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Address", "org.dnal.fieldcopy.dataclass.ZoneAddress");
    }

}