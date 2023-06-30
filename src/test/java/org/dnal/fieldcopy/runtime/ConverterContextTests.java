package org.dnal.fieldcopy.runtime;

import org.dnal.fieldcopy.converter.FCRegistry;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConverterContextTests {

    @Test
    public void test() {

        ConverterContext ctx = new ConverterContext(new FCRegistry(), new RuntimeOptions());

        List<String> list1 = ctx.createEmptyList(null, String.class);
        assertEquals(null, list1);

        List<String> srcL = Arrays.asList();
        list1 = ctx.createEmptyList(srcL, String.class);
        assertEquals(0, list1.size());

        srcL = Arrays.asList("abc", "def");
        list1 = ctx.createEmptyList(srcL, String.class);
        assertEquals(2, list1.size());
        assertEquals("def", list1.get(1));
    }
}
