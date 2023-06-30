package org.dnal.fieldcopy.bdd.core;


import org.dnal.fieldcopy.error.FCError;
import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.parser.fieldcopyjson.ParserResults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BDDSnippetResult {
    public boolean ok;
    public List<FCError> errors = new ArrayList<>();

    public BDDSnippetResult() {
    }
    public BDDSnippetResult(boolean ok) {
        this.ok = ok;
    }

    //codegen
    public ParserResults parseRes;
    public List<CopySpec> specs = new ArrayList<>();

    //convert
    public Object convDestObj;


    public Map<String, String> nameHintMap = new HashMap<>(); //Address.cust=>addr
}
