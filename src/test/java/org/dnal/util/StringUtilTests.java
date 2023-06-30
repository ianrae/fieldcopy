package org.dnal.util;

import org.dnal.fieldcopy.util.StringUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class StringUtilTests {

    @Test
    public void testFilled() {
        char charToAppend = 'a';
        char[] charArray = new char[3];
        Arrays.fill(charArray, charToAppend);
        String newString = new String(charArray);
        assertEquals("aaa", newString);

        newString = StringUtil.createFilled(3, 'a');
        assertEquals("aaa", newString);
    }

    @Test
    public void testSpace() {
        String s = StringUtil.createSpace(4);
        assertEquals("    ", s);
    }
}
