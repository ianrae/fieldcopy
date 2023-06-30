package org.dnal.fieldcopy.parser.fieldcopyjson;

import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.error.FCError;
import org.dnal.fieldcopy.log.SimpleLog;

public class ConfigJsonParser {

    public FieldCopyOptions parseConfig(String json) {
        SimpleLog log = new SimpleLog();
        FieldCopyJsonParser parser = new FieldCopyJsonParser(log);
        FieldCopyOptions options = new FieldCopyOptions();

        ParserResults res = parser.parse(json, options);
        if (!res.ok) {
            for (FCError err : res.errors) {
                log.logError("%s", err.toString());
            }
            throw new FieldCopyException("Failed to load config data from JSON");
        }
        return res.options;
    }
}
