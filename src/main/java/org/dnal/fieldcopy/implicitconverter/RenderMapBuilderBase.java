package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.JavaPrimitive;

import java.util.HashMap;
import java.util.Map;

public abstract class RenderMapBuilderBase implements RenderMapBuilder {
    protected Map<String, ICRow> renderMap;
    protected Map<JavaPrimitive, FieldTypeInformation> typeMap = new HashMap<>();
    protected FieldTypeInformation stringFieldTypeInfo;
    protected ImplicitConverter nothingConv = new DoNothingImplicitConverter();

    @Override
    public abstract void init(Map<String, ICRow> renderMap, FieldTypeInformation notUsed);

    @Override
    public abstract void build();

    public FieldTypeInformation getStringFieldTypeInfo() {
        return stringFieldTypeInfo;
    }

    protected void addFromString(ICRow row, JavaPrimitive prim) {
        String key = stringFieldTypeInfo.createKey();
        row.map.put(key, new StringToPrimConverter(prim));
    }

    protected ImplicitConverter createCastCon(Class<?> targetClass) {
        ImplicitConverter iconvCast = new CastConverter(targetClass);
        return iconvCast;
    }

    protected void addToRenderMap(ICRow row, JavaPrimitive prim) {
        FieldTypeInformation fieldTypeInfo = typeMap.get(prim);
        addToRenderMap(row, fieldTypeInfo);
    }

    protected void addToRenderMap(ICRow row, FieldTypeInformation fieldTypeInfo) {
        String key = fieldTypeInfo.createKey();
        renderMap.put(key, row);
    }

    protected void addToRow(ICRow row, JavaPrimitive prim, ImplicitConverter iconv) {
        FieldTypeInformation fieldTypeInfo = typeMap.get(prim);
        String key = fieldTypeInfo.createKey();
        row.map.put(key, iconv);
    }

    protected ICRow getRowForPrim(JavaPrimitive prim) {
        FieldTypeInformation fieldTypeInfo = typeMap.get(prim);
        return getRowForPrim(fieldTypeInfo);
    }

    public ICRow getRowForPrim(FieldTypeInformation fieldTypeInfo) {
        String key = fieldTypeInfo.createKey();
        return renderMap.get(key);
    }

    public ICRow getRowForString() {
        String key = stringFieldTypeInfo.createKey();
        return renderMap.get(key);
    }

    public String getKeyForPrim(JavaPrimitive prim) {
        FieldTypeInformation fieldTypeInfo = typeMap.get(prim);
        return fieldTypeInfo.createKey();
    }

//    public Map<JavaPrimitive, FieldTypeInfo> getPrimMap() {
//        return primMap;
//    }
}
