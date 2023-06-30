package org.dnal.fieldcopy.runtime;

import org.dnal.fieldcopy.FieldCopy;
import org.dnal.fieldcopy.parser.ParserTestBase;
import org.dnal.fieldcopy.parser.fieldcopyjson.ConfigJsonParser;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.util.TextFileReader;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class RuntimeOptionsTests extends ParserTestBase {

    @Test
    public void test() {
        FieldCopy fc = FieldCopy.with(FieldCopyTests.MyGroup.class).build();
        RuntimeOptions options = fc.getOptions();
        assertEquals(null, options.localDateFormatter);
    }

    @Test
    public void testSetOptions() {
        RuntimeOptions initialOptions = new RuntimeOptions();
        initialOptions.localDateFormatter = DateTimeFormatter.ofPattern("yyyy/mm/dd");

        FieldCopy fc = FieldCopy.with(FieldCopyTests.MyGroup.class).options(initialOptions).build();
        RuntimeOptions options = fc.getOptions();
        assertEquals(true, options.localDateFormatter != null);
    }

    @Test
    public void testFieldCopyOptions() {
        TextFileReader r = new TextFileReader();
        String path = buildPath("parser/convlang-fmt1.json");
        String json = r.readFileAsSingleString(path);

        ConfigJsonParser configParser = new ConfigJsonParser();
        FieldCopyOptions configOptions = configParser.parseConfig(json);

        FieldCopy fc = FieldCopy.with(FieldCopyTests.MyGroup.class).loadOptionsFromConfig(configOptions).build();
        RuntimeOptions options = fc.getOptions();
        assertEquals(true, options.localDateFormatter != null);
    }

    //============
}
