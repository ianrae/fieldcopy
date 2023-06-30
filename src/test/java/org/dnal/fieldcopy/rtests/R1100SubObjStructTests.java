package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.dataclass.Address;
import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * R1100 sub-obj
 * -if sub-obj has a converter it is used, other-wise simply assigns dest field to value of src field.
 */
public class R1100SubObjStructTests extends RTestBase {

    @Test
    public void testWithoutConverter() {
        CopySpec spec = buildSpecFieldSubObj(Customer.class, Customer.class, "addr", "addr");
        List<String> lines = doGen(spec);

        String[] ar = {
                "Address tmp1 = src.getAddr();",
                "dest.setAddr(tmp1);",
        };
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Address");
    }

    @Test
    public void testWitConverter() {
        CopySpec spec = buildSpecFieldSubObj(Customer.class, Customer.class, "addr", "addr");
        CopySpec spec2 = buildSpecFieldSubObj(Address.class, Address.class, "city", "city");
        List<String> lines = doGen(spec, spec2);

        String[] ar = {
                "Address tmp1 = src.getAddr();",
                "if (tmp1 != null) {",
                "ObjectConverter<Address,Address> conv2 = ctx.locate(Address.class, Address.class);",
                "Address tmp3 = conv2.convert(tmp1, new Address(), ctx);",
                "dest.setAddr(tmp3);",
                "}",
        };
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Address");
    }

    //----------

}
