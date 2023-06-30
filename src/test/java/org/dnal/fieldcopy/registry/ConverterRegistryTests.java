package org.dnal.fieldcopy.registry;

import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.codegen.gen.Src1ToDest1Converter;
import org.dnal.fieldcopy.dataclass.Dest1;
import org.dnal.fieldcopy.dataclass.Src1;
import org.dnal.fieldcopy.implicitconverter.ImplicitConversionTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
public class ConverterRegistryTests extends ImplicitConversionTestBase {

    public static class MyRegistry extends ConverterRegistryBase {
        public MyRegistry() {
            add(new Src1ToDest1Converter());
            add(new Src1ToDest1Converter(), "otherOne");
        }
    }

    @Test
    public void test() {
        MyRegistry reg = new MyRegistry();
        assertEquals(2, reg.size());

        ObjectConverter<Src1, Dest1> conv1 = reg.find(Src1.class, Dest1.class);
        assertNotNull(conv1);
        conv1 = reg.find(Src1.class, Dest1.class, "otherOne");
        assertNotNull(conv1);
        conv1 = reg.find(Src1.class, Dest1.class, "junk33");
        assertNull(conv1);

        ObjectConverter<?, ?> conv2 = reg.find(String.class, Dest1.class);
        assertNull(conv2);
        conv2 = reg.find(String.class, Dest1.class, "otherOne"); //only name used for lookup
        assertNotNull(conv2);
    }

    //------------------

}
