package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.JavaPrimitive;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integer -> int. eg int n = nCount.intValue();
 * src: Integer
 * dest: int
 * #4. SUNDAY
 */
public class ScalarToPrimConversionTests extends ImplicitConversionTestBase {


    @Test
    public void testBuild() {
        initBuilder(JavaPrimitive.BYTE);
        Map<String, ICRow> renderMap = currentIcrBuilder.getRenderMap();
        assertEquals(17, renderMap.size()); //9 + 8 scalars

        for (JavaPrimitive prim : JavaPrimitive.values()) {
            String key = currentScalarToPrimBuilder.getKeyForPrim(prim);
            ICRow row = renderMap.get(key);
            //byte,short,int,long  float,double, boolean, char + STRING = 9
            int n = 15; //most types don't have conversion for bool
            switch (prim) {
                case BOOLEAN:
                    n = 3;
                    break;
                default:
                    break;
            }
            assertEquals(n, row.map.size(), prim.name());
        }

    }

    @Test
    public void test0() {
        Byte b3 = 5;
        Integer n3 = 7;
        Short sh3 = 55;
        Float f3 = 15.6f;
        Character c3 = 'a';

        int n = n3.intValue();
        n = b3.intValue();
        n = sh3.intValue();
        n = f3.intValue();
        assertEquals(15, n);
        n = c3.charValue();

        float f = n3.floatValue();
        f = sh3.floatValue();
        f = c3.charValue();

        char ch = (char) n3.intValue();
        ch = (char) sh3.intValue();
        ch = (char) f3.intValue();

        short sh = n3.shortValue();
        sh = f3.shortValue();
        sh = (short) c3.charValue();

        byte by = (byte) n;
        by = (byte) f;
        by = (byte) ch;

        b3 = by;
        b3 = sh3.byteValue();
    }

    @Test
    public void testByte() {
        ICRow row = initBuilder(JavaPrimitive.BYTE); //converting to BYTE

        String expected = "(byte)x";
        chkOne(row, JavaPrimitive.BYTE, null);
        chkOne(row, JavaPrimitive.SHORT, expected);
        chkOne(row, JavaPrimitive.INT, expected);
        chkOne(row, JavaPrimitive.LONG, expected);
        chkOne(row, JavaPrimitive.FLOAT, expected);
        chkOne(row, JavaPrimitive.DOUBLE, expected);
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, expected);
        chkString(row, "Byte.parseByte(x)");
    }

    @Test
    public void testShort() {
        ICRow row = initBuilder(JavaPrimitive.SHORT); //converting to SHORT
        // short sh = n3.shortValue();
        // sh = (short) c3.charValue();

        String expected = "x.shortValue()";
        chkOne(row, JavaPrimitive.BYTE, expected);
        chkOne(row, JavaPrimitive.SHORT, null);
        chkOne(row, JavaPrimitive.INT, expected);
        chkOne(row, JavaPrimitive.LONG, expected);
        chkOne(row, JavaPrimitive.FLOAT, expected);
        chkOne(row, JavaPrimitive.DOUBLE, expected);
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, "(short) x.charValue()");
        chkString(row, "Short.parseShort(x)");
    }

    @Test
    public void testInteger() {
        ICRow row = initBuilder(JavaPrimitive.INT); //converting to INT

        String expected = "x.intValue()";
        chkOne(row, JavaPrimitive.BYTE, expected);
        chkOne(row, JavaPrimitive.SHORT, expected);
        chkOne(row, JavaPrimitive.INT, null);
        chkOne(row, JavaPrimitive.LONG, expected);
        chkOne(row, JavaPrimitive.FLOAT, expected);
        chkOne(row, JavaPrimitive.DOUBLE, expected);
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, "x.charValue()");
        chkString(row, "Integer.parseInt(x)");
    }

    @Test
    public void testLong() {
        ICRow row = initBuilder(JavaPrimitive.LONG); //converting to LONG

        String expected = "x.longValue()";
        chkOne(row, JavaPrimitive.BYTE, expected);
        chkOne(row, JavaPrimitive.SHORT, expected);
        chkOne(row, JavaPrimitive.INT, expected);
        chkOne(row, JavaPrimitive.LONG, null);
        chkOne(row, JavaPrimitive.FLOAT, expected);
        chkOne(row, JavaPrimitive.DOUBLE, expected);
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, "x.charValue()");
        chkString(row, "Long.parseLong(x)");
    }

    @Test
    public void testFloat() {
        ICRow row = initBuilder(JavaPrimitive.FLOAT); //converting to FLOAT

        String expected = "x.floatValue()";
        chkOne(row, JavaPrimitive.BYTE, expected);
        chkOne(row, JavaPrimitive.SHORT, expected);
        chkOne(row, JavaPrimitive.INT, expected);
        chkOne(row, JavaPrimitive.LONG, expected);
        chkOne(row, JavaPrimitive.FLOAT, null);
        chkOne(row, JavaPrimitive.DOUBLE, expected);
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, "x.charValue()");
        chkString(row, "Float.parseFloat(x)");
    }

    @Test
    public void testDouble() {
        ICRow row = initBuilder(JavaPrimitive.DOUBLE); //converting to DOUBLE

        String expected = "x.doubleValue()";
        chkOne(row, JavaPrimitive.BYTE, expected);
        chkOne(row, JavaPrimitive.SHORT, expected);
        chkOne(row, JavaPrimitive.INT, expected);
        chkOne(row, JavaPrimitive.LONG, expected);
        chkOne(row, JavaPrimitive.FLOAT, expected);
        chkOne(row, JavaPrimitive.DOUBLE, null);
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, "x.charValue()");
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
    public void testChar() {
        ICRow row = initBuilder(JavaPrimitive.CHAR); //converting to CHAR

        String expected = "(char) x.intValue()";
        chkOne(row, JavaPrimitive.BYTE, expected);
        chkOne(row, JavaPrimitive.SHORT, expected);
        chkOne(row, JavaPrimitive.INT, expected);
        chkOne(row, JavaPrimitive.LONG, expected);
        chkOne(row, JavaPrimitive.FLOAT, expected);
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, null);
        chkString(row, "Character.parseChar(x)");
    }

//    @Test
//    public void testString() {
//        initBuilder(JavaPrimitive.CHAR); //prim type doesn't matter, we just need to create
//        ICRow row = currentIcrBuilder.getPrimBuilder().getRowForString();
//
//        chkOne(row, JavaPrimitive.BYTE, "Byte.valueOf(x).toString()");
//        chkOne(row, JavaPrimitive.SHORT, "Short.valueOf(x).toString()");
//        chkOne(row, JavaPrimitive.INT, "Integer.valueOf(x).toString()");
//        chkOne(row, JavaPrimitive.LONG, "Long.valueOf(x).toString()");
//        chkOne(row, JavaPrimitive.FLOAT, "Float.valueOf(x).toString()");
//        chkOne(row, JavaPrimitive.DOUBLE, "Double.valueOf(x).toString()");
//        chkOne(row, JavaPrimitive.BOOLEAN, "Boolean.valueOf(x).toString()");
//        chkOne(row, JavaPrimitive.CHAR, "Character.valueOf(x).toString()");
//        chkString(row, null);
//    }
//
//    //--for debugging only
//    @Test
//    public void testDebug() {
//
//    }
//

    //============
    private ScalarToPrimBuilder currentScalarToPrimBuilder;

    private ICRow initBuilder(JavaPrimitive prim) {
        ImplicitConverterRegistryBuilder icrBuilder = createICRBuilder();
        ScalarBuilder scalarBuilder = new ScalarBuilder();
        additionalBuilders.add(scalarBuilder);
        icrBuilder.buildAdditionalEx(additionalBuilders);

        ScalarToPrimBuilder builder = new ScalarToPrimBuilder();
        builder.init(icrBuilder.getRenderMap(), icrBuilder.getPrimBuilder().getStringFieldTypeInfo());
        builder.build();
        currentScalarToPrimBuilder = builder;

        return currentScalarToPrimBuilder.getRowForPrim(prim);
    }

    private void chkNotSupported(ICRow row, JavaPrimitive prim) {
        String key = getKeyForPrimType(prim);
        doChkNotSupported(row, key);
    }

    private void chkOne(ICRow row, JavaPrimitive prim, String expected) {
        String key = getKeyForPrimType(prim);
        doChkOne(row, key, expected);
    }

    protected String getKeyForPrimType(JavaPrimitive prim) {
        FieldTypeInformation fieldTypeInfo = currentScalarToPrimBuilder.scalarTypeMap.get(prim);
        return fieldTypeInfo.createKey();
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
