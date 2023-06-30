package org.dnal.fieldcopy.codegen;

import org.dnal.fieldcopy.builder.PrimsBuilder;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.registry.ICRTestBase;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class MyCodeGeneratorTests extends ICRTestBase {


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
    public void testGen1() {
        CopySpec spec = specBuilder.buildListSpec("roles", "points");
        options.createNewListWhenCopying = false;
        List<String> bodyLines = doGen(spec, null);
        chkImports(currentSrcSpec, "java.util.List");
        assertEquals("CustomerToCustomerConverter", currentSrcSpec.className);

        MyCodeGenerator myCodeGenerator = new MyCodeGenerator(outDir);
        myCodeGenerator.gen(spec, bodyLines, currentSrcSpec);
    }
    @Test
    public void testGen2() {
        CopySpec spec = specBuilder.buildSpec(2);
        specBuilder.addObject(spec);
        List<String> bodyLines = doGen(spec, null);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Address");
        currentSrcSpec.className = "CustomerToCustomerConverter2";

        MyCodeGenerator myCodeGenerator = new MyCodeGenerator(outDir);
        myCodeGenerator.gen(spec, bodyLines, currentSrcSpec);
    }

    @Test
    public void testGen3() {
        CopySpec spec = specBuilder.buildSpec(2);
        specBuilder.addObject(spec);
        CopySpec spec2 = addrSpecBuilder.buildSpec(2);

        List<String> bodyLines = doGen(spec, spec2);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Address");
        currentSrcSpec.className = "CustomerToCustomerConverter3";

        MyCodeGenerator myCodeGenerator = new MyCodeGenerator(outDir);
        myCodeGenerator.gen(spec, bodyLines, currentSrcSpec);

        //and address
        bodyLines = doGen(spec2, null);
        chkNoImports(currentSrcSpec);
        currentSrcSpec.className = "AddressToAddressConverter";

        myCodeGenerator = new MyCodeGenerator(outDir);
        myCodeGenerator.gen(spec2, bodyLines, currentSrcSpec);
    }

    @Test
    public void testGen4() {
        CopySpec spec = specBuilder.buildSpec(2);
        specBuilder.addObject(spec);
        CopySpec spec2 = addrSpecBuilder.buildSpec(2);
        addrSpecBuilder.addBackRef(spec2);

//        List<String> bodyLines = doGen(spec, spec2);
//        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Address");
//        currentSrcSpec.className = "CustomerToCustomerConverter3";

        //and address
        List<String> bodyLines = doGen(spec2, spec);
        chkImports(currentSrcSpec, "org.dnal.fieldcopy.dataclass.Customer");
        currentSrcSpec.className = "AddressToAddressConverterBackRef";

        MyCodeGenerator myCodeGenerator = new MyCodeGenerator(outDir);
        myCodeGenerator.gen(spec2, bodyLines, currentSrcSpec);
    }

    @Test
    public void testGen5() {
        CopySpec spec = specBuilder.buildDateSpec(4);
        List<String> bodyLines = doGen(spec, null);
        chkImports(currentSrcSpec, "java.time.LocalDate", "java.time.LocalTime", "java.time.LocalDateTime", "java.time.ZonedDateTime");
        currentSrcSpec.className = "CustomerToCustomerConverterDate";

        MyCodeGenerator myCodeGenerator = new MyCodeGenerator(outDir);
        myCodeGenerator.gen(spec, bodyLines, currentSrcSpec);
    }

    @Test
    public void testGen6() {
        CopySpec spec = specBuilder.buildDateSpecReverse(4);
        List<String> bodyLines = doGen(spec, null);
        chkImports(currentSrcSpec, "java.time.LocalDate", "java.time.LocalTime", "java.time.LocalDateTime", "java.time.ZonedDateTime");
        currentSrcSpec.className = "CustomerToCustomerConverterDate2";

        MyCodeGenerator myCodeGenerator = new MyCodeGenerator(outDir);
        myCodeGenerator.gen(spec, bodyLines, currentSrcSpec);
    }

    @Test
    public void testGen7() {
        CopySpec spec = specBuilder.buildSpec(0);
        specBuilder.addCustom(spec);
        List<String> bodyLines = doGen(spec, null);
        chkNoImports(currentSrcSpec);
        currentSrcSpec.className = "CustomerToCustomerConverterCustom";

        MyCodeGenerator myCodeGenerator = new MyCodeGenerator(outDir);
        myCodeGenerator.gen(spec, bodyLines, currentSrcSpec);
    }


    //============
    private PrimsBuilder primsBuilder = new PrimsBuilder();
    private String outDir = "C:/tmp/fieldcopy2/gen";

}
