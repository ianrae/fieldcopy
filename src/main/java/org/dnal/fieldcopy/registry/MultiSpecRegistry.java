package org.dnal.fieldcopy.registry;

import org.dnal.fieldcopy.fieldspec.CopySpec;
import org.dnal.fieldcopy.runtime.ObjectConverter;

public class MultiSpecRegistry extends ConverterRegistryBase {
    public MultiSpecRegistry() {
    }

    public void addForSpec(CopySpec spec) {
        ObjectConverter<?, ?> conv = new PlaceholderConverter(spec.srcClass, spec.destClass);
        if (spec.converterNameForUsing.isPresent()) {
            add(conv, spec.converterNameForUsing.get());
        } else {
            add(conv);
        }
    }

    public void addAdditional(ObjectConverter converter) {
        String key = makeKey(converter, null);
        map.put(key, converter);
    }

    public void addAdditionalNamed(ObjectConverter converter, String name) {
        add(converter, name);
    }

}
