package org.dnal.fieldcopy.parser;

import org.dnal.fieldcopy.TestBase;
import org.dnal.fieldcopy.error.FCError;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyJsonParser;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.parser.fieldcopyjson.ParserResults;
import org.dnal.fieldcopy.util.TextFileReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class ParserTestBase extends TestBase {

    protected void chkNoErrors(ParserResults res) {
        for (FCError err : res.errors) {
            log(err.toString());
        }
        assertEquals(0, res.errors.size());
    }

    protected ParserResults loadFile(String fileName) {
        TextFileReader r = new TextFileReader();
        String path = buildPath(fileName);

        String json = r.readFileAsSingleString(path);
        FieldCopyJsonParser parser = createParser();

        FieldCopyOptions options = new FieldCopyOptions();
        ParserResults res = parser.parse(json, options);
        return res;
    }

    protected FieldCopyJsonParser createParser() {
        FieldCopyJsonParser parser = new FieldCopyJsonParser(log);
        return parser;
    }

}
