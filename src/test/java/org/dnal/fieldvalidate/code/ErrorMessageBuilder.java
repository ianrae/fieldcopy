package org.dnal.fieldvalidate.code;

import org.dnal.fieldvalidate.code.FieldError;

public interface ErrorMessageBuilder {
    String buildMessage(FieldError err);
}
