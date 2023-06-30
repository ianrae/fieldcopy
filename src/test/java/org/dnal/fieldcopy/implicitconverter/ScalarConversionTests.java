package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.types.JavaPrimitive;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class ScalarConversionTests extends ImplicitConversionTestBase {


    @Test
    public void testBuild() {
        initBuilder(JavaPrimitive.BYTE);
        Map<String, ICRow> renderMap = currentIcrBuilder.getRenderMap();
        assertEquals(17, renderMap.size()); //9 + 8 scalars

        for (JavaPrimitive prim : JavaPrimitive.values()) {
            String key = currentScalarBuilder.getKeyForPrim(prim);
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

    }

    @Test
    public void test0() {
        byte _byte = 7;
        Byte by = Byte.valueOf(_byte);
        by = Byte.valueOf((byte) 7);
        Integer n = by.intValue();
        by = n.byteValue();

        Long nl = 5L;
        n = nl.intValue();
        nl = n.longValue();
        Short sh = by.shortValue();
        by = n.byteValue();

        Float fl = 45.6f;
        n = fl.intValue();

        char ch = 'a';
        Character cch = Character.valueOf(ch);
        ch = cch.charValue();
        n = Integer.valueOf(cch.charValue());
        cch = Character.valueOf((char) n.intValue());
        cch = Character.valueOf(ch);

        String sss = Character.toString(ch);
        String sss2 = Character.toString(ch);

//        Boolean bb = n.bo
    }

    @Test
    public void testByte() {
        ICRow row = initBuilder(JavaPrimitive.BYTE); //converting to BYTE

        chkOne(row, JavaPrimitive.BYTE, null);
        chkOne(row, JavaPrimitive.SHORT, "x.byteValue()");
        chkOne(row, JavaPrimitive.INT, "x.byteValue()");
        chkOne(row, JavaPrimitive.LONG, "x.byteValue()");
        chkOne(row, JavaPrimitive.FLOAT, "x.byteValue()");
        chkOne(row, JavaPrimitive.DOUBLE, "x.byteValue()");
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, "Byte.valueOf(x.charValue())");
        chkString(row, "Byte.parseByte(x)");
    }


    @Test
    public void testShort() {
        ICRow row = initBuilder(JavaPrimitive.SHORT); //converting to SHORT

        chkOne(row, JavaPrimitive.BYTE, "x.shortValue()");
        chkOne(row, JavaPrimitive.SHORT, null);
        chkOne(row, JavaPrimitive.INT, "x.shortValue()");
        chkOne(row, JavaPrimitive.LONG, "x.shortValue()");
        chkOne(row, JavaPrimitive.FLOAT, "x.shortValue()");
        chkOne(row, JavaPrimitive.DOUBLE, "x.shortValue()");
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, "Short.valueOf(x.charValue())");
        chkString(row, "Short.parseShort(x)");
    }

    @Test
    public void testInteger() {
        ICRow row = initBuilder(JavaPrimitive.INT); //converting to INT

        chkOne(row, JavaPrimitive.BYTE, "x.intValue()");
        chkOne(row, JavaPrimitive.SHORT, "x.intValue()");
        chkOne(row, JavaPrimitive.INT, null);
        chkOne(row, JavaPrimitive.LONG, "x.intValue()");
        chkOne(row, JavaPrimitive.FLOAT, "x.intValue()");
        chkOne(row, JavaPrimitive.DOUBLE, "x.intValue()");
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, "Integer.valueOf(x.charValue())");
        chkString(row, "Integer.parseInt(x)");
    }

    @Test
    public void testLong() {
        ICRow row = initBuilder(JavaPrimitive.LONG); //converting to LONG

        chkOne(row, JavaPrimitive.BYTE, "x.longValue()");
        chkOne(row, JavaPrimitive.SHORT, "x.longValue()");
        chkOne(row, JavaPrimitive.INT, "x.longValue()");
        chkOne(row, JavaPrimitive.LONG, null);
        chkOne(row, JavaPrimitive.FLOAT, "x.longValue()");
        chkOne(row, JavaPrimitive.DOUBLE, "x.longValue()");
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, "Long.valueOf(x.charValue())");
        chkString(row, "Long.parseLong(x)");
    }

    @Test
    public void testFloat() {
        ICRow row = initBuilder(JavaPrimitive.FLOAT); //converting to FLOAT

        chkOne(row, JavaPrimitive.BYTE, "x.floatValue()");
        chkOne(row, JavaPrimitive.SHORT, "x.floatValue()");
        chkOne(row, JavaPrimitive.INT, "x.floatValue()");
        chkOne(row, JavaPrimitive.LONG, "x.floatValue()");
        chkOne(row, JavaPrimitive.FLOAT, null);
        chkOne(row, JavaPrimitive.DOUBLE, "x.floatValue()");
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, "Float.valueOf(x.charValue())");
        chkString(row, "Float.parseFloat(x)");
    }

    @Test
    public void testDouble() {
        ICRow row = initBuilder(JavaPrimitive.DOUBLE); //converting to DOUBLE

        chkOne(row, JavaPrimitive.BYTE, "x.doubleValue()");
        chkOne(row, JavaPrimitive.SHORT, "x.doubleValue()");
        chkOne(row, JavaPrimitive.INT, "x.doubleValue()");
        chkOne(row, JavaPrimitive.LONG, "x.doubleValue()");
        chkOne(row, JavaPrimitive.FLOAT, "x.doubleValue()");
        chkOne(row, JavaPrimitive.DOUBLE, null);
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, "Double.valueOf(x.charValue())");
        chkString(row, "Double.parseDouble(x)");
    }

    @Test
    public void testBoolean() {
        ICRow row = initBuilder(JavaPrimitive.BOOLEAN); //converting to BOOLEAN

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
        ICRow row = initBuilder(JavaPrimitive.CHAR); //converting to CHAR

        String expected = "Character.valueOf((char)x.intValue())";
        chkOne(row, JavaPrimitive.BYTE, expected);
        chkOne(row, JavaPrimitive.SHORT, expected);
        chkOne(row, JavaPrimitive.INT, expected);
        chkOne(row, JavaPrimitive.LONG, expected);
        chkOne(row, JavaPrimitive.FLOAT, expected);
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, null);
        chkString(row, "Character.parseChar(x)");
    }

    @Test
    public void testString() {
        initBuilder(JavaPrimitive.CHAR); //prim type doesn't matter, we just need to create
        ICRow row = currentIcrBuilder.getPrimBuilder().getRowForString();

        chkOne(row, JavaPrimitive.BYTE, "x.toString()");
        chkOne(row, JavaPrimitive.SHORT, "x.toString()");
        chkOne(row, JavaPrimitive.INT, "x.toString()");
        chkOne(row, JavaPrimitive.LONG, "x.toString()");
        chkOne(row, JavaPrimitive.FLOAT, "x.toString()");
        chkOne(row, JavaPrimitive.DOUBLE, "x.toString()");
        chkOne(row, JavaPrimitive.BOOLEAN, "x.toString()");
        chkOne(row, JavaPrimitive.CHAR, "x.toString()");
        chkString(row, null);
    }

    //--for debugging only
    @Test
    public void testDebug() {

    }


    //============
    private ScalarBuilder currentScalarBuilder;

    private ICRow initBuilder(JavaPrimitive prim) {
        ImplicitConverterRegistryBuilder icrBuilder = createICRBuilder();
        ScalarBuilder builder = new ScalarBuilder();
        builder.init(icrBuilder.getRenderMap(), icrBuilder.getPrimBuilder().getStringFieldTypeInfo());
        builder.build();
        currentScalarBuilder = builder;
        return builder.getRowForPrim(prim);
    }

    private void chkNotSupported(ICRow row, JavaPrimitive prim) {
//        String key = currentIcrBuilder.getPrimBuilder().getKeyForPrim(prim);
        String key = currentScalarBuilder.getKeyForPrim(prim);
        doChkNotSupported(row, key);
    }

    private void chkOne(ICRow row, JavaPrimitive prim, String expected) {
//        String key = currentIcrBuilder.getPrimBuilder().getKeyForPrim(prim);
        String key = currentScalarBuilder.getKeyForPrim(prim);
        doChkOne(row, key, expected);
    }

    //    private void chkOnePrim(ICRow row, JavaPrimitive prim, String expected) {
//        String key = currentIcrBuilder.getPrimBuilder().getKeyForPrim(prim);
//        doChkOne(row, key, expected);
//    }
    protected void chkString(ICRow row, String expected) {
//        String key = currentScalarBuilder.getStringFieldTypeInfo().createKey();
        String key = currentIcrBuilder.getPrimBuilder().getStringFieldTypeInfo().createKey();
        doChkString(row, key, expected);
    }

}
