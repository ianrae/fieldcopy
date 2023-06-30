package org.dnal.fieldcopy.bdd.core;


import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.log.FieldCopyLog;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyJsonParser;
import org.dnal.fieldcopy.parser.fieldcopyjson.ParsedConverterSpec;
import org.dnal.fieldcopy.parser.fieldcopyjson.ParserResults;
import org.dnal.fieldcopy.util.StringUtil;


public class NoOpSnippetRunner extends ConvLangSnippetRunner {

    public NoOpSnippetRunner(FieldCopyLog log) {
        super(log);
    }

    @Override
    public BDDSnippetResult execute(BDDSnippet snippet, BDDSnippetResult previousRes, SnippetContext ctx) {
        BDDSnippetResult res = new BDDSnippetResult();

        String convLangSrc = StringUtil.convertToSingleString(snippet.lines);
        ParserResults parseRes = loadFile(convLangSrc); //parser.parse(json, options);
//        dumpObj("actions:", res.converters);
//        chkNoErrors(res);
        res.parseRes = parseRes;

        if (parseRes.hasErrors()) {
            res.errors.addAll(parseRes.errors);
            res.ok = false;
            return res;
        }

        FieldCopyJsonParser parser = createParser();
        for (ParsedConverterSpec action : parseRes.converters) {
            CopySpec spec = parser.buildSpecFromAction(action, parseRes.options);
            action.nameStr = buildConverterName(spec, ctx);
            res.specs.add(spec);
        }

        //We don't do codegen here
        res.ok = true;
        return res;
    }

}
