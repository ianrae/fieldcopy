package org.dnal.fieldcopy.implicitconverter.date;

import org.dnal.fieldcopy.implicitconverter.ICRow;
import org.dnal.fieldcopy.implicitconverter.ImplicitConverter;
import org.dnal.fieldcopy.implicitconverter.RenderMapBuilderBase;
import org.dnal.fieldcopy.implicitconverter.TypeMapBuilder;
import org.dnal.fieldcopy.implicitconverter.sampleclass.SampleClass2;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;

import java.util.Map;

public abstract class DateBuilderBase extends RenderMapBuilderBase {
    protected FieldTypeInformation fieldTypeInfo;
    protected String fieldKey;
    protected Class<?> fieldClazz;

    public DateBuilderBase(Class<?> clazz) {
        fieldClazz = clazz;
    }

    @Override
    public void init(Map<String, ICRow> renderMap, FieldTypeInformation stringFieldTypeInfo) {
        this.renderMap = renderMap;
        this.stringFieldTypeInfo = stringFieldTypeInfo;

        TypeMapBuilder typeMapBuilder = new TypeMapBuilder();
        typeMapBuilder.buildTypeMap(typeMap, SampleClass2.class);

        fieldTypeInfo = new FieldTypeInformationImpl(fieldClazz);
        fieldKey = fieldTypeInfo.createKey();
    }

    @Override
    public void build() {
        //byte,short,int,long  float,double, boolean, char
        //only LocalDate -> String is supported
        buildString(String.class);
        buildDateOrTime(new ICRow());
    }

    protected void buildDateOrTime(ICRow row) {
        StringToDateOrTimeConverter conv = new StringToDateOrTimeConverter(fieldClazz);
        row.map.put(stringFieldTypeInfo.createKey(), conv);
        addToRenderMap(row, fieldTypeInfo);
    }

    protected void buildString(Class<?> targetClass) {
        ICRow row = getRowForString(); //add to existing String row

        ImplicitConverter conv = createConverter();
        row.map.put(fieldKey, conv);
    }

    protected abstract ImplicitConverter createConverter();

    public FieldTypeInformation getFieldTypeInfo() {
        return fieldTypeInfo;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public ICRow getRow() {
        ICRow row = renderMap.get(fieldKey);
        return row;
    }
}
