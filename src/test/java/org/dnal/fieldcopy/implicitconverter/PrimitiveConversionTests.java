package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.builder.PrimsBuilder;
import org.dnal.fieldcopy.builder.SpecBuilder1;
import org.dnal.fieldcopy.types.JavaPrimitive;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class PrimitiveConversionTests extends ImplicitConversionTestBase {

    @Test
    public void test0() {
        byte n = 7;
        byte x = n;
        short sh = n;
        int i = 5;
        long nl = i;
        i = (int) nl;

        float f = 16.2f;
        i = (int) f;
        assertEquals(16, i);
        f = 16.99f;
        i = (int) f;
        assertEquals(16, i);

        char ch = 'a';
        i = ch;
        assertEquals(97, i);
        assertEquals(7, sh);
        ch = (char) sh;
        ch = (char) f;

        String s = "44";
        i = Integer.parseInt(s);
        assertEquals(44, i);
        s = Integer.valueOf(44).toString();
        assertEquals("44", s);

        f = sh;
        f = i;
        assertEquals(44, f);

        ch = 'a';
        f = ch;
        assertEquals(97.0, f);

        boolean b = false;
        b = Boolean.parseBoolean("true");
        assertEquals(true, b);
//        b = sh;
    }

    @Test
    public void testBuild() {
        ImplicitConverterRegistryBuilder icrBuilder = createICRBuilder();
        Map<String, ICRow> renderMap = icrBuilder.getRenderMap();
        assertEquals(expectedNumRows, renderMap.size());

        for (JavaPrimitive prim : JavaPrimitive.values()) {
            String key = icrBuilder.getPrimBuilder().getKeyForPrim(prim);
            ICRow row = renderMap.get(key);
            //byte,short,int,long  float,double, boolean, char + STRING = 9
            int n = 9 - 1; //most types don't have conversion for bool
            switch(prim) {
                case BOOLEAN:
                    n = 2;
                    break;
                default:
                    break;
            }
            assertEquals(n, row.map.size(), prim.name());
        }

        ICRow row = icrBuilder.getPrimBuilder().getRowForPrim(icrBuilder.getPrimBuilder().getStringFieldTypeInfo());
        assertEquals(9, row.map.size(), "STRING");

    }

    @Test
    public void testByte() {
        ImplicitConverterRegistryBuilder icrBuilder = createICRBuilder();
        ICRow row = icrBuilder.getPrimBuilder().getRowForPrim(JavaPrimitive.BYTE);

        chkOne(row, JavaPrimitive.BYTE, null);
        chkOne(row, JavaPrimitive.SHORT, "(byte)x");
        chkOne(row, JavaPrimitive.INT, "(byte)x");
        chkOne(row, JavaPrimitive.LONG, "(byte)x");
        chkOne(row, JavaPrimitive.FLOAT, "(byte)x");
        chkOne(row, JavaPrimitive.DOUBLE, "(byte)x");
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, null);
        chkString(row, "Byte.parseByte(x)");
    }

    @Test
    public void testShort() {
        ImplicitConverterRegistryBuilder icrBuilder = createICRBuilder();
        ICRow row = icrBuilder.getPrimBuilder().getRowForPrim(JavaPrimitive.SHORT);

        chkOne(row, JavaPrimitive.BYTE, null);
        chkOne(row, JavaPrimitive.SHORT, null);
        chkOne(row, JavaPrimitive.INT, "(short)x");
        chkOne(row, JavaPrimitive.LONG, "(short)x");
        chkOne(row, JavaPrimitive.FLOAT, "(short)x");
        chkOne(row, JavaPrimitive.DOUBLE, "(short)x");
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, null);
        chkString(row, "Short.parseShort(x)");
    }

    @Test
    public void testInt() {
        ImplicitConverterRegistryBuilder icrBuilder = createICRBuilder();
        ICRow row = icrBuilder.getPrimBuilder().getRowForPrim(JavaPrimitive.INT); //converting to INT

        chkOne(row, JavaPrimitive.BYTE, null);
        chkOne(row, JavaPrimitive.SHORT, null);
        chkOne(row, JavaPrimitive.INT, null);
        chkOne(row, JavaPrimitive.LONG, "(int)x");
        chkOne(row, JavaPrimitive.FLOAT, "(int)x");
        chkOne(row, JavaPrimitive.DOUBLE, "(int)x");
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, null);
        chkString(row, "Integer.parseInt(x)");
    }

    @Test
    public void testLong() {
        ImplicitConverterRegistryBuilder icrBuilder = createICRBuilder();
        ICRow row = icrBuilder.getPrimBuilder().getRowForPrim(JavaPrimitive.LONG);

        chkOne(row, JavaPrimitive.BYTE, null);
        chkOne(row, JavaPrimitive.SHORT, null);
        chkOne(row, JavaPrimitive.INT, null);
        chkOne(row, JavaPrimitive.LONG, null);
        chkOne(row, JavaPrimitive.FLOAT, "(long)x");
        chkOne(row, JavaPrimitive.DOUBLE, "(long)x");
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, null);
        chkString(row, "Long.parseLong(x)");
    }

    @Test
    public void testFloat() {
        ImplicitConverterRegistryBuilder icrBuilder = createICRBuilder();
        ICRow row = icrBuilder.getPrimBuilder().getRowForPrim(JavaPrimitive.FLOAT);

        chkOne(row, JavaPrimitive.BYTE, null);
        chkOne(row, JavaPrimitive.SHORT, null);
        chkOne(row, JavaPrimitive.INT, null);
        chkOne(row, JavaPrimitive.LONG, null);
        chkOne(row, JavaPrimitive.FLOAT, null);
        chkOne(row, JavaPrimitive.DOUBLE, "(float)x");
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, null);
        chkString(row, "Float.parseFloat(x)");
    }

    @Test
    public void testDouble() {
        ImplicitConverterRegistryBuilder icrBuilder = createICRBuilder();
        ICRow row = icrBuilder.getPrimBuilder().getRowForPrim(JavaPrimitive.DOUBLE);

        chkOne(row, JavaPrimitive.BYTE, null);
        chkOne(row, JavaPrimitive.SHORT, null);
        chkOne(row, JavaPrimitive.INT, null);
        chkOne(row, JavaPrimitive.LONG, null);
        chkOne(row, JavaPrimitive.FLOAT, null);
        chkOne(row, JavaPrimitive.DOUBLE, null);
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, null);
        chkString(row, "Double.parseDouble(x)");
    }

    @Test
    public void testBoolean() {
        ImplicitConverterRegistryBuilder icrBuilder = createICRBuilder();
        ICRow row = icrBuilder.getPrimBuilder().getRowForPrim(JavaPrimitive.BOOLEAN);

        chkNotSupported(row, JavaPrimitive.BYTE);
        chkNotSupported(row, JavaPrimitive.SHORT);
        chkNotSupported(row, JavaPrimitive.INT);
        chkNotSupported(row, JavaPrimitive.LONG);
        chkNotSupported(row, JavaPrimitive.FLOAT);
        chkNotSupported(row, JavaPrimitive.DOUBLE);
        chkOne(row, JavaPrimitive.BOOLEAN, null);
        chkNotSupported(row, JavaPrimitive.CHAR);
        chkString(row, "Boolean.parseBoolean(x)");
    }

    @Test
    public void testCharacter() {
        ImplicitConverterRegistryBuilder icrBuilder = createICRBuilder();
        ICRow row = icrBuilder.getPrimBuilder().getRowForPrim(JavaPrimitive.CHAR);

        String expected = "(char)x";
        chkOne(row, JavaPrimitive.BYTE, expected);
        chkOne(row, JavaPrimitive.SHORT, expected);
        chkOne(row, JavaPrimitive.INT, expected);
        chkOne(row, JavaPrimitive.LONG, expected);
        chkOne(row, JavaPrimitive.FLOAT, expected);
        chkOne(row, JavaPrimitive.DOUBLE, expected);
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, null);
        chkString(row, "Character.parseChar(x)");
    }
    @Test
    public void testString() {
        ImplicitConverterRegistryBuilder icrBuilder = createICRBuilder();
        ICRow row = icrBuilder.getPrimBuilder().getRowForPrim(icrBuilder.getPrimBuilder().getStringFieldTypeInfo());

        chkOne(row, JavaPrimitive.BYTE, "Byte.valueOf(x).toString()");
        chkOne(row, JavaPrimitive.SHORT, "Short.valueOf(x).toString()");
        chkOne(row, JavaPrimitive.INT, "Integer.valueOf(x).toString()");
        chkOne(row, JavaPrimitive.LONG, "Long.valueOf(x).toString()");
        chkOne(row, JavaPrimitive.FLOAT, "Float.valueOf(x).toString()");
        chkOne(row, JavaPrimitive.DOUBLE, "Double.valueOf(x).toString()");
        chkOne(row, JavaPrimitive.BOOLEAN, "Boolean.valueOf(x).toString()");
        chkOne(row, JavaPrimitive.CHAR, "Character.valueOf(x).toString()");
        chkString(row, null);
    }


    @Test
    public void test2() {
        createICRBuilder();

        ConversionInfo convInfo = buildConvInfo("n1", "n1");
        assertEquals(false, convInfo.needsConversion);
        assertEquals(true, convInfo.isSupported);

        convInfo = buildConvInfo("n1", "s2");
        assertEquals(true, convInfo.needsConversion);
        assertEquals(true, convInfo.isSupported);
    }

    @Test
    public void testNotSupported() {
        createICRBuilder();
        ConversionInfo convInfo = buildConvInfoSample("_int", "_boolean");
        assertEquals(true, convInfo.needsConversion); //types are different
        assertEquals(false, convInfo.isSupported);  //not supported (without a plugin)
    }

//    @Test
//    public void test2() {
//        ConversionInfo convInfo = buildConvInfo("n1", "n1");
//
//        ImplicitConverterRegistryBuilder xbuilder = new ImplicitConverterRegistryBuilder();
//        ImplicitConverter xinfo = xbuilder.createFor(convInfo);
//        String src = xinfo.gen(convInfo);
//        assertEquals(null, src);
//
//        convInfo = buildConvInfo("n1", "s2");
//
//        xinfo = xbuilder.createFor(convInfo);
//        src = xinfo.gen(convInfo);
//        assertEquals("abc", src);
//    }


    //--for debugging only
    @Test
    public void testDebug() {

    }


    //============
    private int expectedNumRows = 9;
    private SpecBuilder1 specBuilder = new SpecBuilder1();
    private PrimsBuilder primsBuilder = new PrimsBuilder();


    private void chkNotSupported(ICRow row, JavaPrimitive prim) {
        String key = currentIcrBuilder.getPrimBuilder().getKeyForPrim(prim);
        doChkNotSupported(row, key);
    }

    private void chkOne(ICRow row, JavaPrimitive prim, String expected) {
        String key = currentIcrBuilder.getPrimBuilder().getKeyForPrim(prim);
        doChkOne(row, key, expected);
    }

    protected void chkString(ICRow row, String expected) {
        String key = currentIcrBuilder.getPrimBuilder().getStringFieldTypeInfo().createKey();
        doChkString(row, key, expected);
    }
}
