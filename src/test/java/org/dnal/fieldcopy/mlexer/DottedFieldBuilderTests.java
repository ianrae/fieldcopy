package org.dnal.fieldcopy.mlexer;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DottedFieldBuilderTests {

    @Test
    public void testAddrCityParse() {
        DottedFieldBuilder dfBuilder = new DottedFieldBuilder("addr", Arrays.asList("city"), "lastName", null);
        assertEquals(2, dfBuilder.getMax());
        assertEquals("addr", dfBuilder.getIthSrc(0));
        assertEquals(null, dfBuilder.getIthDest(0));
        assertEquals("city", dfBuilder.getIthSrc(1));
        assertEquals("lastName", dfBuilder.getIthDest(1));
    }

}
