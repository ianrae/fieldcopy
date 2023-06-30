package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.implicitconverter.date.ZonedDateTimeBuilder;
import org.dnal.fieldcopy.types.JavaPrimitive;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class ZonedDateTimeConversionTests extends ImplicitConversionTestBase {

    @Test
    public void testBuild() {
        initBuilder();
        Map<String, ICRow> renderMap = currentIcrBuilder.getRenderMap();
        assertEquals(10, renderMap.size()); //9 in prim + 1 for reverse

        String key = currentIcrBuilder.getPrimBuilder().getStringFieldTypeInfo().createKey();
        ICRow row = renderMap.get(key);
        //byte,short,int,long  float,double, boolean, char + STRING = 9
        int n = 9 + 1; //prim types + ZonedDateTime

        assertEquals(n, row.map.size(), "ZonedDateTime");
    }


    //ISO-8601
    //https://stackoverflow.com/questions/54873929/converting-a-string-to-java-date-in-iso-8601-date-time-format
    @Test
    public void test0() {
        ZoneId zid = ZoneId.systemDefault();
        log(zid.getId());

        LocalDateTime ldt = LocalDateTime.of(2023, 02, 28, 18, 30, 55);
        ZonedDateTime dt = ZonedDateTime.of(ldt, zid);
        String s = dt.toString();
        assertEquals("2023-02-28T18:30:55-05:00[America/New_York]", s);

        OffsetDateTime odt = OffsetDateTime.now(zid);
        ZoneOffset zo = odt.getOffset();
        log(odt.toString());

        DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
        s = dtf.format(dt);
        assertEquals("2023-02-28T18:30:55-05:00[America/New_York]", s); //TODO: fix. this depends on EST
    }

    //TODO: testLong.  ZonedDateTime -> Long. Is there a need for this?

    @Test
    public void testString() {
        // LocalTime -> String
        // src: LocalTime
        // dest: String
        initBuilder();
        ICRow row = currentIcrBuilder.getPrimBuilder().getRowForString();

        chkLocalTime(row, "ctx.zonedDateTimeToString(x)");
    }
    @Test
    public void testZonedDateTime() {
        // String -> LocalTime
        initBuilder();
        ICRow row = zonedDateTimeBuilder.getRow();

        chkFromString(row, "ctx.toZonedDateTime(x)");
    }

    //--for debugging only
    @Test
    public void testDebug() {

    }


    //============
    private ZonedDateTimeBuilder zonedDateTimeBuilder;

    private void initBuilder() {
        initBuilder(JavaPrimitive.CHAR); //prim type doesn't matter, we just need to create the builder
    }

    private ICRow initBuilder(JavaPrimitive prim) {
        ImplicitConverterRegistryBuilder icrBuilder = createICRBuilder();
        ZonedDateTimeBuilder builder = new ZonedDateTimeBuilder();
        stringFieldTypeInfo = icrBuilder.getPrimBuilder().getStringFieldTypeInfo();
        builder.init(icrBuilder.getRenderMap(), stringFieldTypeInfo);
        builder.build();
        zonedDateTimeBuilder = builder;

        return builder.getRowForPrim(prim);
    }

//    private void chkNotSupported(ICRow row, JavaPrimitive prim) {
////        String key = currentIcrBuilder.getPrimBuilder().getKeyForPrim(prim);
////        String key = currentScalarBuilder.getKeyForPrim(prim);
////        doChkNotSupported(row, key);
//    }

    private void chkLocalTime(ICRow row, String expected) {
        doChkOne(row, zonedDateTimeBuilder.getFieldKey(), expected);
    }
}
