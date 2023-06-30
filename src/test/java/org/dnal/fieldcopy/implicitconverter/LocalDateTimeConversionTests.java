package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.implicitconverter.date.LocalDateTimeBuilder;
import org.dnal.fieldcopy.types.JavaPrimitive;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class LocalDateTimeConversionTests extends ImplicitConversionTestBase {

    @Test
    public void testBuild() {
        initBuilder(JavaPrimitive.BYTE);
        Map<String, ICRow> renderMap = currentIcrBuilder.getRenderMap();
        assertEquals(10, renderMap.size()); //9 in prim + 1 for reverse

        String key = currentIcrBuilder.getPrimBuilder().getStringFieldTypeInfo().createKey();
        ICRow row = renderMap.get(key);
        //byte,short,int,long  float,double, boolean, char + STRING = 9
        int n = 9 + 1; //prim types + LocalDateTime

        assertEquals(n, row.map.size(), "LocalDateTime");
    }


    //ISO-8601
    //https://stackoverflow.com/questions/54873929/converting-a-string-to-java-date-in-iso-8601-date-time-format
    @Test
    public void test0() {
        LocalDateTime dt = LocalDateTime.of(2023, 02, 28, 18, 30, 55);
        String s = dt.toString();
        assertEquals("2023-02-28T18:30:55", s);

        ZoneId zid = ZoneId.systemDefault();
        log(zid.getId());

        OffsetDateTime odt = OffsetDateTime.now(zid);
        ZoneOffset zo = odt.getOffset();
        log(odt.toString());

        DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
        s = dtf.format(dt);
        assertEquals("2023-02-28T18:30:55", s); //TODO: fix. this depends on EST
    }

    //TODO: testLong.  LocalDateTime -> Long. Is there a need for this?

    @Test
    public void testString() {
        // LocalTime -> String
        // src: LocalTime
        // dest: String
        initBuilder(JavaPrimitive.CHAR); //prim type doesn't matter, we just need to create
        ICRow row = currentIcrBuilder.getPrimBuilder().getRowForString();

        chkLocalTime(row, "ctx.dateTimeToString(x)");
    }
    @Test
    public void testLocalDateTime() {
        // String -> LocalTime
        initBuilder(JavaPrimitive.CHAR); //prim type doesn't matter, we just need to create
        ICRow row = localDateTimeBuilder.getRow();

        chkFromString(row, "ctx.toLocalDateTime(x)");
    }

    //--for debugging only
    @Test
    public void testDebug() {

    }


    //============
    private LocalDateTimeBuilder localDateTimeBuilder;

    private ICRow initBuilder(JavaPrimitive prim) {
        ImplicitConverterRegistryBuilder icrBuilder = createICRBuilder();
        LocalDateTimeBuilder builder = new LocalDateTimeBuilder();
        stringFieldTypeInfo = icrBuilder.getPrimBuilder().getStringFieldTypeInfo();
        builder.init(icrBuilder.getRenderMap(), stringFieldTypeInfo);
        builder.build();
        localDateTimeBuilder = builder;

        return builder.getRowForPrim(prim);
    }

//    private void chkNotSupported(ICRow row, JavaPrimitive prim) {
////        String key = currentIcrBuilder.getPrimBuilder().getKeyForPrim(prim);
////        String key = currentScalarBuilder.getKeyForPrim(prim);
////        doChkNotSupported(row, key);
//    }

    private void chkLocalTime(ICRow row, String expected) {
        doChkOne(row, localDateTimeBuilder.getFieldKey(), expected);
    }
}
