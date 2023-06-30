package org.dnal.fieldcopy.parser.fieldcopyjson;

import org.dnal.fieldcopy.log.FieldCopyLog;
import org.dnal.fieldcopy.log.SimpleLog;
import org.dnal.fieldcopy.util.TextFileReader;

public class FileLoader {

    public ParserResults loadAndParseFile(String path) {
        return loadAndParseFile(path, new FieldCopyOptions());
    }
    public ParserResults loadAndParseFile(String path, FieldCopyOptions options) {
        String json = loadFile(path);
        FieldCopyLog log = new SimpleLog();
        FieldCopyJsonParser parser = new FieldCopyJsonParser(log);

        ParserResults res = parser.parse(json, options);
        return res;
    }
    public ParserResults parseString(String json, FieldCopyOptions options) {
        FieldCopyLog log = new SimpleLog();
        FieldCopyJsonParser parser = new FieldCopyJsonParser(log);

        ParserResults res = parser.parse(json, options);
        return res;
    }

    public String loadFile(String path) {
        TextFileReader r = new TextFileReader();
        String contents = r.readFileAsSingleString(path);
        return contents;
    }
}
