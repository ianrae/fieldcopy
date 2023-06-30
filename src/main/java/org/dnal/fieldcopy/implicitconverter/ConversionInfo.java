package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.fieldspec.SingleFld;

public class ConversionInfo {
    public boolean isSupported; //if false then no implicit conversion (or plugin) is available
    public boolean needsConversion; //false means no conversion is necessary
    public SingleFld srcFld;
    public SingleFld destFld;

    public ConversionInfo(SingleFld srcFld, SingleFld destFld) {
        this.srcFld = srcFld;
        this.destFld = destFld;
    }
}
