package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.implicitconverter.date.DateBuilder;
import org.dnal.fieldcopy.implicitconverter.date.LocalDateTimeBuilder;
import org.dnal.fieldcopy.types.JavaPrimitive;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class DateConversionTests extends ImplicitConversionTestBase {

    @Test
    public void testBuild() {
        initBuilder(JavaPrimitive.BYTE);
        Map<String, ICRow> renderMap = currentIcrBuilder.getRenderMap();
        assertEquals(10, renderMap.size()); //9 in prim + 1 for reverse

        String key = currentIcrBuilder.getPrimBuilder().getStringFieldTypeInfo().createKey();
        ICRow row = renderMap.get(key);
        //byte,short,int,long  float,double, boolean, char + STRING = 9
        int n = 9 + 1; //prim types + LocalDateTime

        assertEquals(n, row.map.size(), "Date");
    }


    //ISO-8601
    //https://stackoverflow.com/questions/54873929/converting-a-string-to-java-date-in-iso-8601-date-time-format
    @Test
    public void test0() {
    }

    //TODO: testLong.  LocalDateTime -> Long. Is there a need for this?

    @Test
    public void testString() {
        // LocalTime -> String
        // src: LocalTime
        // dest: String
        initBuilder(JavaPrimitive.CHAR); //prim type doesn't matter, we just need to create
        ICRow row = currentIcrBuilder.getPrimBuilder().getRowForString();

        chkLocalTime(row, "ctx.dateToString(x)");
    }
    @Test
    public void testDate() {
        // String -> LocalTime
        initBuilder(JavaPrimitive.CHAR); //prim type doesn't matter, we just need to create
        ICRow row = dateBuilder.getRow();

        chkFromString(row, "ctx.toDate(x)");
    }

    //--for debugging only
    @Test
    public void testDebug() {

    }


    //============
    private DateBuilder dateBuilder;

    private ICRow initBuilder(JavaPrimitive prim) {
        ImplicitConverterRegistryBuilder icrBuilder = createICRBuilder();
        DateBuilder builder = new DateBuilder();
        stringFieldTypeInfo = icrBuilder.getPrimBuilder().getStringFieldTypeInfo();
        builder.init(icrBuilder.getRenderMap(), stringFieldTypeInfo);
        builder.build();
        dateBuilder = builder;

        return builder.getRowForPrim(prim);
    }

//    private void chkNotSupported(ICRow row, JavaPrimitive prim) {
////        String key = currentIcrBuilder.getPrimBuilder().getKeyForPrim(prim);
////        String key = currentScalarBuilder.getKeyForPrim(prim);
////        doChkNotSupported(row, key);
//    }

    private void chkLocalTime(ICRow row, String expected) {
        doChkOne(row, dateBuilder.getFieldKey(), expected);
    }
}
