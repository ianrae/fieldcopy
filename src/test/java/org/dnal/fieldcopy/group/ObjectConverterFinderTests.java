package org.dnal.fieldcopy.group;

import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.registry.ICRTestBase;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class ObjectConverterFinderTests extends ICRTestBase {

    @Test
    public void test() {
//        ReflectionUtil helper = new ReflectionUtil();
//        BobCustomerConverter cc = new BobCustomerConverter();
//        Class<?> zz = helper.getClassFromName(cc.getClass().getName());

        ObjectConverterFinder finder = new ObjectConverterFinder(converterClassPackage);
        List<String> list = Arrays.asList("BobCustomerConverter");
        List<ObjectConverter<?,?>> converters = finder.findConverters(list);

        assertEquals(1, converters.size());
    }

    @Test
    public void testFail() {
        ObjectConverterFinder finder = new ObjectConverterFinder(converterClassPackage);
        List<String> list = Arrays.asList("NoSuchClass");

        FieldCopyException thrown = Assertions.assertThrows(FieldCopyException.class, () -> {
            List<ObjectConverter<?,?>> converters = finder.findConverters(list);
        });
        log(thrown.getMessage());
        chkException(thrown, "unknown class: org.dnal.fieldcopy.group.gen.NoSuchClass");
    }

    //produces messy stack dump.
    //uncomment to run
//    @Test
//    public void testFailNoDefaultCtor() {
//        ObjectConverterFinder finder = new ObjectConverterFinder("org.dnal.fieldcopy.bdd.customconverter");
//        List<String> list = Arrays.asList("BadConverter");
//
//        FieldCopyException thrown = Assertions.assertThrows(FieldCopyException.class, () -> {
//            List<ObjectConverter<?,?>> converters = finder.findConverters(list);
//        });
//        log(thrown.getMessage());
//        chkException(thrown, "failed to create: class org.dnal.fieldcopy.bdd.customconverter.BadConverter");
//    }

    //============
    private String converterClassPackage = "org.dnal.fieldcopy.group.gen";

}
