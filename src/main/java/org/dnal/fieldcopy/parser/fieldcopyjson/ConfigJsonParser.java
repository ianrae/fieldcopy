package org.dnal.fieldcopy.parser.fieldcopyjson;

import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.error.FCError;
import org.dnal.fieldcopy.log.FieldCopyLog;
import org.dnal.fieldcopy.log.SimpleLog;

public class ConfigJsonParser {
    private FieldCopyLog log;

    public ConfigJsonParser(FieldCopyLog log) {
        this.log = log;
    }

    public FieldCopyOptions parseConfig(String json) {
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
