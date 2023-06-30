package org.dnal.fieldcopy.converter;

import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.registry.ConverterRegistryBase;

import java.util.List;
import java.util.Map;

public class FCRegistry extends ConverterRegistryBase {

    public FCRegistry() {
        super();
    }
    public FCRegistry(Map<String, ObjectConverter> map) {
        super(map);
    }

    public void add(ObjectConverter converter) {
        super.add(converter);
    }

    public void addAll(List<ObjectConverter> converters) {
        for(ObjectConverter conv: converters) {
            super.add(conv);
        }
    }
    public void addNamed(ObjectConverter converter, String name) {
        add(converter, name);
    }
    public void addNamedFromOtherRegistry(FCRegistry registry) {
        for(String key: registry.getMap().keySet()) {
            ObjectConverter converter = registry.getMap().get(key);
            String converterName = registry.parseConverterName(key);
            this.addNamed(converter, converterName);
        }
    }
}
