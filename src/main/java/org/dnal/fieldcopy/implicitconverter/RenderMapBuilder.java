package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.types.FieldTypeInformation;

import java.util.Map;

public interface RenderMapBuilder {
    public void init(Map<String, ICRow> renderMap, FieldTypeInformation stringFieldTypeInfo);

    public void build();

}
