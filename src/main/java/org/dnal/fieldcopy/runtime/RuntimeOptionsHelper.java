package org.dnal.fieldcopy.runtime;

import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;

public class RuntimeOptionsHelper {

    public void loadFromFieldCopyOptions(FieldCopyOptions options, RuntimeOptions runtimeOptions) {
        //copy from json config
        if (options.localDateFormat != null) {
            runtimeOptions.setLocalDateFormat(options.localDateFormat);
        }
        if (options.localTimeFormat != null) {
            runtimeOptions.setLocalTimeFormat(options.localTimeFormat);
        }
        if (options.localDateTimeFormat != null) {
            runtimeOptions.setLocalDateTimeFormat(options.localDateTimeFormat);
        }
        if (options.zonedDateTimeFormat != null) {
            runtimeOptions.setZonedDateTimeFormat(options.zonedDateTimeFormat);
        }
        if (options.utilDateFormat != null) {
            runtimeOptions.setUtilDateFormat(options.utilDateFormat);
        }
    }
}
