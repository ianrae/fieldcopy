package org.dnal.fieldcopy.runtime;

import org.dnal.fieldcopy.parser.ParserTestBase;
import org.dnal.fieldcopy.parser.fieldcopyjson.ConfigJsonParser;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.util.TextFileReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigJsonParserTests extends ParserTestBase {

    @Test
    public void testPreParse() {
        TextFileReader r = new TextFileReader();
        String path = buildPath("parser/convlang4b.json");
        String json = r.readFileAsSingleString(path);

        ConfigJsonParser configParser = new ConfigJsonParser();
        FieldCopyOptions options = configParser.parseConfig(json);

        assertEquals("org.dnal.fieldcopy.dataclass", options.defaultSourcePackage);
    }

}
