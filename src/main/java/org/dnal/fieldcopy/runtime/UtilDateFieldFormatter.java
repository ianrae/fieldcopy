package org.dnal.fieldcopy.runtime;

import org.dnal.fieldcopy.codegen.FieldCopyException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilDateFieldFormatter {

    private final SimpleDateFormat sdf;

    public UtilDateFieldFormatter(SimpleDateFormat sdf) {
        this.sdf = sdf;
    }

    public String format(Date dt) {
        String s = sdf.format(dt);
        return s;
    }

    public Date parse(String s) {
        Date dt = null;
        try {
            dt = sdf.parse(s);
        } catch (ParseException e) {
            String msg = String.format("failed to parse java.util.date: '%s'", s);
            throw new FieldCopyException(msg);
        }
        return dt;
    }
}
