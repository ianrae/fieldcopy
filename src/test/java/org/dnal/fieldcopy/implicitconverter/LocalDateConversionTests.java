package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.implicitconverter.date.LocalDateBuilder;
import org.dnal.fieldcopy.types.JavaPrimitive;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TODO
 * -reverse  String -> LocalDate!!
 */
public class LocalDateConversionTests extends ImplicitConversionTestBase {

    @Test
    public void testBuild() {
        initBuilder(JavaPrimitive.BYTE);
        Map<String, ICRow> renderMap = currentIcrBuilder.getRenderMap();
        assertEquals(10, renderMap.size()); //9 in prim + 1 for reverse

        String key = currentIcrBuilder.getPrimBuilder().getStringFieldTypeInfo().createKey();
        ICRow row = renderMap.get(key);
        //byte,short,int,long  float,double, boolean, char + STRING = 9
        int n = 9 + 1; //prim types + LocalDate

        assertEquals(n, row.map.size(), "LocalDate");
    }


    //ISO-8601
    //https://stackoverflow.com/questions/54873929/converting-a-string-to-java-date-in-iso-8601-date-time-format
    @Test
    public void test0() {
        LocalDate dt = LocalDate.of(2022, 2, 28);
        String s = dt.toString();
        assertEquals("2022-02-28", s);

        ZoneId zid = ZoneId.systemDefault();
        log(zid.getId());

//        Instant instant = dt.atStartOfDay().toInstant();
//        ZoneOffset zo = zid.getRules().getOffset( instant );

        OffsetDateTime odt = OffsetDateTime.now(zid);
        ZoneOffset zo = odt.getOffset();
        log(odt.toString());

//        Instant ii = dt.atStartOfDay().toInstant(ZoneOffset..UTC);
        Instant ii = dt.atStartOfDay().toInstant(zo);
        long sec = ii.getEpochSecond();
        log(String.format("ii: %d", sec));
        java.util.Date jDate = new java.util.Date();
        sec = jDate.getTime();
        log(String.format("ii: %d", sec));
        sec = sec / 1000;
        log(String.format("ii: %d", sec));

        //https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
        //https://www.baeldung.com/java-datetimeformatter
        DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE;
        s = dtf.format(dt);
        assertEquals("2022-02-28", s);

        LocalDate dt2 = LocalDate.parse("2022-02-28", dtf);
        assertEquals(dt, dt2);
    }

    //TODO: testLong.  LocalDate -> Long. Is there a need for this?

    @Test
    public void testString() {
        // LocalDate -> String
        // src: LocalDate
        // dest: String
        initBuilder(JavaPrimitive.CHAR); //prim type doesn't matter, we just need to create
        ICRow row = currentIcrBuilder.getPrimBuilder().getRowForString();

        chkLocalDate(row, "ctx.dateToString(x)");
    }

    @Test
    public void testLocalDate() {
        // String -> LocalDate
        // src: String
        // dest: LocalDate
        initBuilder(JavaPrimitive.CHAR); //prim type doesn't matter, we just need to create
        ICRow row = localDateBuilder.getRow();

        chkFromString(row, "ctx.toLocalDate(x)");
    }

    //--for debugging only
    @Test
    public void testDebug() {

    }


    //============
    private LocalDateBuilder localDateBuilder;

    private ICRow initBuilder(JavaPrimitive prim) {
        ImplicitConverterRegistryBuilder icrBuilder = createICRBuilder();
        LocalDateBuilder builder = new LocalDateBuilder();
        stringFieldTypeInfo = icrBuilder.getPrimBuilder().getStringFieldTypeInfo();
        builder.init(icrBuilder.getRenderMap(), stringFieldTypeInfo);
        builder.build();
        localDateBuilder = builder;

        return builder.getRowForPrim(prim);
    }

//    private void chkNotSupported(ICRow row, JavaPrimitive prim) {
////        String key = currentIcrBuilder.getPrimBuilder().getKeyForPrim(prim);
////        String key = currentScalarBuilder.getKeyForPrim(prim);
////        doChkNotSupported(row, key);
//    }

    private void chkLocalDate(ICRow row, String expected) {
        doChkOne(row, localDateBuilder.getFieldKey(), expected);
    }

}
