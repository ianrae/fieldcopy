package org.dnal.fieldcopy.rtests;

import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 *
 */

public class R900DateFormatTests extends RTestBase {


    //-- date --
    @Test
    public void testLocalDate() {
        List<String> lines = buildValueAndGenForDate("'2022-02-28'", "date");
        String[] ar = {
                "String tmp1 = \"2022-02-28\";",
                "LocalDate tmp2 = ctx.toLocalDate(tmp1);",
                "dest.setDate(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.time.LocalDate");
    }

    @Test
    public void testLocalDateFail() {
        options.validateDateAndTimeValues = true;
        FieldCopyException thrown = Assertions.assertThrows(FieldCopyException.class, () -> {
            List<String> lines = buildValueAndGenForDate("'202222-02-28'", "date");
        });
        chkException(thrown, "LocalDate: Text '202222-02-28' could not be parsed ");
    }

    @Test
    public void testUtilDateFail() {
        options.validateDateAndTimeValues = true;
        FieldCopyException thrown = Assertions.assertThrows(FieldCopyException.class, () -> {
            List<String> lines = buildValueAndGenForDate("'202222-02-28'", "utilDate");
        });
        chkException(thrown, "Date: Text '202222-02-28' could not be parsed ");
    }

    @Test
    public void testValidationDisabled() {
        options.validateDateAndTimeValues = false;
        List<String> lines = buildValueAndGenForDate("'202222-02-28'", "date");
        String[] ar = {
                "String tmp1 = \"202222-02-28\";",
                "LocalDate tmp2 = ctx.toLocalDate(tmp1);",
                "dest.setDate(tmp2);"};
        chkLines(lines, ar);
        chkImports(currentSrcSpec, "java.time.LocalDate");
    }


    //--for debugging only
    @Test
    public void testDebug() {
    }


    //============

}
