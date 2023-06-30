package org.dnal.fieldcopy.registry;

import org.dnal.fieldcopy.builder.SpecBuilder2;
import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.error.NotImplementedException;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * TODO
 * lists
 * -string->string does nothing
 * -string->integer works
 * -string-String custom converter
 * -string-integer custom converter
 * -Array/SetMap
 * DONE -Enum
 * <p>
 * DONE -create custom converter LocalDate->String,etc
 * DONE -reverse String->LocalDate
 * DONE required
 * json parser
 * <p>
 * -load these by default
 * -create converter for BigDouble too
 * DONE -required and default modifiers
 * <p>
 * -null src, dest, values, elements
 * -rule: outer converter src must not be null
 * -converters can always assume src is not null
 * -inner converters (ie. for subobjs) wrap use in if (xxx != null)
 * <p>
 * -things that can be set
 * -date format, timezone
 * -rounding
 * -locale
 * <p>
 * <p>
 * DONE -recursion
 * -registry should wrap converter in something that tracks
 * src object in map, so can't get infinite loop
 * -don't need to do this for src that is primitive,scalar,list/set/map
 * <p>
 * <p>
 * create a custom converter for List<String> -> List<Integer>
 */
public class ListTests extends ICRTestBase {


    @Test
    public void testNoConv() {
        CopySpec spec = specBuilder.buildListSpec("roles", "roles");
        options.createNewListWhenCopying = false;
        List<String> lines = doGen(spec, null);

        String[] ar = {
                "List<String> tmp1 = src.getRoles();",
                "dest.setRoles(tmp1);"
        };
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.util.List");
    }

    @Test
    public void test() {
        CopySpec spec = specBuilder.buildListSpec("roles", "points");
        options.createNewListWhenCopying = false;
        List<String> lines = doGen(spec, null);

        String[] ar = {
                "List<String> tmp1 = src.getRoles();",
                "List<Integer> list2 = new ArrayList<>();",
                "for(String el3: tmp1) {",
                "  Integer tmp4 = Integer.parseInt(el3);",
                "  list2.add(tmp4);",
                "}",
                "dest.setPoints(list2);"
        };
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.util.List");
    }

    @Test
    public void testOptional() {
        SpecBuilder2 specBuilder2 = new SpecBuilder2();
        final CopySpec spec = specBuilder2.buildSpecEx();

        List<String> lines;
        NotImplementedException thrown = Assertions.assertThrows(NotImplementedException.class, () -> {
            doGen(spec, null);
        });
        log(thrown.getMessage());
        chkException(thrown, "Optional of generic value (such as Optional<List<String>> not yet supported");

        //TODO fix this. need a much more enhanced FileTypeInfo that can handle nested things
        //some people actually use List<Optional<String>> yikes
        //https://stackoverflow.com/questions/60533534/collecting-lists-of-optionals-to-a-list-containing-present-optionals

//        String[] ar = {
//                "List<String> tmp1 = src.getRoles();",
//                "List<Integer> list2 = new ArrayList<>();",
//                "for(String el3: tmp1) {",
//                "  Integer tmp4 = Integer.parseInt(el3);",
//                "  list2.add(tmp4);",
//                "}",
//                "dest.setPoints(list2);"
//        };
//        chkLines(lines, ar);
//        chkImports(currentSrcSpec, "java.util.List");
    }


    @Test
    public void testStuff() {
        Customer src = buildCustomer();
        Customer dest = buildCustomer();

        //paste codegen here
//        List<String> tmp1 = src.getRoles();
//        List<String> list2 = new ArrayList<>();
//        for(String tmp3: tmp1) {
//            //conv
//            list2.add(tmp3);
//        }
//        dest.setRoles(list2);

//        //
        List<String> tmp1 = src.getRoles();
        List<Integer> list2 = new ArrayList<>();
        for (String el3 : tmp1) {
            Integer tmp4 = Integer.parseInt(el3);
            list2.add(tmp4);
        }
        dest.setPoints(list2);

        assertEquals(2, dest.getRoles().size());
        List<String> xlist1 = src.getRoles();
        List<String> xlist2 = dest.getRoles();
        assertNotSame(xlist1, xlist2);
    }

    private Customer buildCustomer() {
        Customer cust = new Customer();
        cust.setRoles(Arrays.asList("33", "44"));
        return cust;
    }

    //============


}
