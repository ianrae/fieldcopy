package org.dnal.fieldcopy.bdd.core;

import java.util.ArrayList;
import java.util.List;

public class BDDFeature {
    public String name;
    public int startLineIndex; //where tests start
    public List<BDDSnippet> backgroundsL = new ArrayList<>();
    public List<BDDTest> testsL = new ArrayList<>();
}
