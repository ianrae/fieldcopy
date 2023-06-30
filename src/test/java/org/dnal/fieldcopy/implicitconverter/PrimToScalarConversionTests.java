package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.JavaPrimitive;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * int -> Integer. eg int Integer n3 = Integer.valueOf(n);
 * src: int
 * dest: Intger
 * #3. SATURDAY
 */
public class PrimToScalarConversionTests extends ImplicitConversionTestBase {

    @Test
    public void testBuild() {
        initBuilder(JavaPrimitive.BYTE);
        Map<String, ICRow> renderMap = currentIcrBuilder.getRenderMap();
        assertEquals(17, renderMap.size()); //9 + 8 scalars

        for (JavaPrimitive prim : JavaPrimitive.values()) {
            String key = currentPrimToScalarBuilder.getKeyForPrim(prim);
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
        byte bb = 5;
        int n = 7;
        Integer nn = n;
        nn = Integer.valueOf(bb);
        char ch = 'a';

        short sh = 5;
        nn = Integer.valueOf(sh);
        long ll = 55;
        nn = Math.toIntExact(ll);
        float ff = 55.6f;
        nn = Integer.valueOf((int) ff);
        assertEquals(55, nn.intValue());
        double dd = 56.6;
        nn = Integer.valueOf((int) dd);
        assertEquals(56, nn.intValue());
        nn = Integer.valueOf(ch);

        nn = Integer.valueOf(ch);
        assertEquals(97, nn.intValue());

        Float f = Float.valueOf(n);
        f = ff;
        f = Float.valueOf((float) dd);

        Double d3 = Double.valueOf((double) ff);

        n = 98;
        Character c3 = ch;
        c3 = Character.valueOf((char) n);
        assertEquals('b', c3.charValue());
        c3 = Character.valueOf((char) dd);
        Short s3 = Short.valueOf((short) dd);
        Byte b3 = Byte.valueOf((byte) n);
        assertEquals(98, b3.byteValue());
    }

    @Test
    public void testByte() {
        ICRow row = initBuilder(JavaPrimitive.BYTE); //converting to BYTE

        String expected = "Byte.valueOf((byte)x)";
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

        String expected = "Short.valueOf(x)";
        chkOne(row, JavaPrimitive.BYTE, expected);
        chkOne(row, JavaPrimitive.SHORT, null);
        chkOne(row, JavaPrimitive.INT, expected);
        chkOne(row, JavaPrimitive.LONG, expected);
        chkOne(row, JavaPrimitive.FLOAT, "Short.valueOf((short)x)");
        chkOne(row, JavaPrimitive.DOUBLE, "Short.valueOf((short)x)");
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, expected);
        chkString(row, "Short.parseShort(x)");
    }

    @Test
    public void testInteger() {
        ICRow row = initBuilder(JavaPrimitive.INT); //converting to INT

        chkOne(row, JavaPrimitive.BYTE, "Integer.valueOf(x)");
        chkOne(row, JavaPrimitive.SHORT, "Integer.valueOf(x)");
        chkOne(row, JavaPrimitive.INT, null);
        chkOne(row, JavaPrimitive.LONG, "Integer.valueOf(x)");
        chkOne(row, JavaPrimitive.FLOAT, "Integer.valueOf((int)x)");
        chkOne(row, JavaPrimitive.DOUBLE, "Integer.valueOf((int)x)");
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, "Integer.valueOf(x)"); //        c3 = Character.valueOf((char)n);

        chkString(row, "Integer.parseInt(x)");
    }

    @Test
    public void testLong() {
        ICRow row = initBuilder(JavaPrimitive.LONG); //converting to LONG

        chkOne(row, JavaPrimitive.BYTE, "Long.valueOf(x)");
        chkOne(row, JavaPrimitive.SHORT, "Long.valueOf(x)");
        chkOne(row, JavaPrimitive.INT, "Long.valueOf(x)");
        chkOne(row, JavaPrimitive.LONG, null);
        chkOne(row, JavaPrimitive.FLOAT, "Long.valueOf((long)x)");
        chkOne(row, JavaPrimitive.DOUBLE, "Long.valueOf((long)x)");
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, "Long.valueOf(x)");
        chkString(row, "Long.parseLong(x)");
    }

    @Test
    public void testFloat() {
        ICRow row = initBuilder(JavaPrimitive.FLOAT); //converting to FLOAT

        chkOne(row, JavaPrimitive.BYTE, "Float.valueOf(x)");
        chkOne(row, JavaPrimitive.SHORT, "Float.valueOf(x)");
        chkOne(row, JavaPrimitive.INT, "Float.valueOf(x)");
        chkOne(row, JavaPrimitive.LONG, "Float.valueOf(x)");
        chkOne(row, JavaPrimitive.FLOAT, null);
        chkOne(row, JavaPrimitive.DOUBLE, "Float.valueOf((float)x)");
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, "Float.valueOf(x)");
        chkString(row, "Float.parseFloat(x)");
    }

    @Test
    public void testDouble() {
        ICRow row = initBuilder(JavaPrimitive.DOUBLE); //converting to DOUBLE

        String expected = "Double.valueOf(x)";
        chkOne(row, JavaPrimitive.BYTE, expected);
        chkOne(row, JavaPrimitive.SHORT, expected);
        chkOne(row, JavaPrimitive.INT, expected);
        chkOne(row, JavaPrimitive.LONG, expected);
        chkOne(row, JavaPrimitive.FLOAT, "Double.valueOf((double)x)");
        chkOne(row, JavaPrimitive.DOUBLE, null);
        chkNotSupported(row, JavaPrimitive.BOOLEAN);
        chkOne(row, JavaPrimitive.CHAR, expected);
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

        String expected = "Character.valueOf((char)x)";
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

    //--for debugging only
    @Test
    public void testDebug() {

    }


    //============
    private PrimToScalarBuilder currentPrimToScalarBuilder;

    private ICRow initBuilder(JavaPrimitive prim) {
        ImplicitConverterRegistryBuilder icrBuilder = createICRBuilder();
        ScalarBuilder scalarBuilder = new ScalarBuilder();
        additionalBuilders.add(scalarBuilder);
        icrBuilder.buildAdditionalEx(additionalBuilders);

        PrimToScalarBuilder builder = new PrimToScalarBuilder();
        builder.init(icrBuilder.getRenderMap(), icrBuilder.getPrimBuilder().getStringFieldTypeInfo());
        builder.build();
        currentPrimToScalarBuilder = builder;

        return currentPrimToScalarBuilder.getRowForPrim(prim);
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
        FieldTypeInformation fieldTypeInfo = currentPrimToScalarBuilder.primTypeMap.get(prim);
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
