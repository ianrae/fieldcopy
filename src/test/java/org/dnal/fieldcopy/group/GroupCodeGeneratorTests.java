package org.dnal.fieldcopy.group;

import org.dnal.fieldcopy.Converter;
import org.dnal.fieldcopy.FieldCopy;
import org.dnal.fieldcopy.dataclass.Address;
import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.group.gen.DefaultConverterGroup;
import org.dnal.fieldcopy.parser.fieldcopyjson.FileLoader;
import org.dnal.fieldcopy.parser.fieldcopyjson.ParserResults;
import org.dnal.fieldcopy.registry.ICRTestBase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 *
 */
public class GroupCodeGeneratorTests extends ICRTestBase {

    @Test
    public void tesGen() {
        String path = buildPath("parser/convlang-customer1.json"); //also tests config.defaultSourcePackage
        FileLoader fileLoader = new FileLoader();
        ParserResults res = fileLoader.loadAndParseFile(path);
        dumpObj("actions:", res.converters);
        chkNoErrors(res);

        String outputPath = "src/test/java/org/dnal/fieldcopy/group/gen";

        GroupCodeGenerator groupCodeGenerator = new GroupCodeGenerator();
        groupCodeGenerator.setPackageName("org.dnal.fieldcopy.group.gen");
        groupCodeGenerator.setOutDir(outputPath);
//        groupCodeGenerator.setDryRunFlag(true);
        boolean b = groupCodeGenerator.generateJavaFiles(res);
        assertEquals(true, b);
    }

    @Test
    public void tesRuntime() {
        Customer src = createSrc();
        Customer dest = new Customer();

        FieldCopy fc = FieldCopy.with(DefaultConverterGroup.class).build();
        Converter<Customer, Customer> converter = fc.getConverter(Customer.class, Customer.class);

        Customer dest2 = converter.convert(src, dest);

        assertEquals("bob", dest.getFirstName()); //not converted
        assertEquals("44", dest.getLastName());
        assertSame(dest, dest2);
    }

    //============
    private String outDir = "C:/tmp/fieldcopy2/tmp";

    private void chkInt(int expected, List<Integer> list, int i) {
        Integer n = list.get(i);
        assertEquals(expected, n.intValue());
    }

    private Address createAddress() {
        Address addr = new Address();
        addr.setStreet1("main");
        addr.setCity("kingston");
        return addr;
    }

    private Customer createSrc() {
        Customer cust = new Customer();
        cust.setFirstName("bob");
        cust.setLastName("smith");
        cust.setRoles(Arrays.asList("35", "37"));
        cust.setNumPoints(44);
        cust.setAddr(createAddress());
        return cust;
    }

}
