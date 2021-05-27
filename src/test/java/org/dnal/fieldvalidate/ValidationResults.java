package org.dnal.fieldvalidate;

import org.dnal.fieldvalidate.code.FieldError;

import java.util.ArrayList;
import java.util.List;

public class ValidationResults {
    public List<FieldError> errL = new ArrayList<>();

    public boolean hasNoErrors() {
        return errL.isEmpty();
    }
}
