package org.dnal.fieldcopy.parser.fieldcopyjson;


import org.dnal.fieldcopy.error.FCError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParserResults {
    public boolean ok;
    public List<FCError> errors = new ArrayList<>();
    public List<ParsedConverterSpec> converters = new ArrayList<>();
    public List<Map<String, Object>> parsedItems;
    public FieldCopyOptions options;

    public boolean hasErrors() {
        return errors.size() > 0;
    }
}
