package org.dnal.fieldcopy.codegen;

import org.dnal.fieldcopy.codegen.VarNameGenerator;

//used during codegen
public class ConversionContext {
    public VarNameGenerator varNameGenerator;
    public boolean doListCopy;
    public String listVar;

    public ConversionContext(VarNameGenerator varNameGenerator) {
        this.varNameGenerator = varNameGenerator;
    }
}
