package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.dataclass.Color;
import org.dnal.fieldcopy.dataclass.ExtendedCustomer;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.junit.jupiter.api.Test;

import java.util.List;

/*
     R1400 inheritance
     -works for both base and derived class fields
     -auto (see R700 testExcludeInheritance)
 */
public class R1400InheritanceTests extends RTestBase {

    @Test
    public void test() {
        CopySpec spec = buildWithField(ExtendedCustomer.class, ExtendedCustomer.class, "firstName", "firstName");
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = src.getFirstName();",
                "dest.setFirstName(tmp1);"
        };
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void test2() {
        CopySpec spec = buildWithField(ExtendedCustomer.class, ExtendedCustomer.class, "favColor", "firstName");
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = src.getFavColor();",
                "dest.setFirstName(tmp1);"
        };
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void test3() {
        CopySpec spec = buildWithField(ExtendedCustomer.class, ExtendedCustomer.class, "favColor", "favColor");
        List<String> lines = doGen(spec);

        String[] ar = {
                "String tmp1 = src.getFavColor();",
                "dest.setFavColor(tmp1);"
        };
        chkLines(lines, ar);
        chkNoImports(currentSrcSpec);
    }

    @Test
    public void testSubObj() {
        CopySpec spec = buildSpecFieldSubObj(ExtendedCustomer.class, ExtendedCustomer.class, "zoneAddr.zone", "zoneAddr.zone");
        List<String> lines = doGen(spec);

        String[] ar = {
                "ZoneAddress tmp1 = src.getZoneAddr();",
                "String tmp2 = tmp1.getZone();",
                "ZoneAddress tmp3 = (dest.getZoneAddr() == null) ? new ZoneAddress() : dest.getZoneAddr();",
                "dest.setZoneAddr(tmp3);",
                "tmp3.setZone(tmp2);",
        };
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.ZoneAddress");
    }


    //----

}
