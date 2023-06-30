package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.implicitconverter.sampleclass.SampleClass1;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.JavaPrimitive;

import java.util.Map;

public class PrimBuilder extends RenderMapBuilderBase {

    public PrimBuilder() {
    }

    @Override
    public void init(Map<String, ICRow> renderMap, FieldTypeInformation notUsed) {
        this.renderMap = renderMap;
        TypeMapBuilder typeMapBuilder = new TypeMapBuilder();
        typeMapBuilder.buildTypeMap(typeMap, SampleClass1.class);

        //and string
        stringFieldTypeInfo = typeMapBuilder.buildForString();
    }

    @Override
    public void build() {
        //byte,short,int,long  float,double, boolean, char
        buildByte(new ICRow(), JavaPrimitive.BYTE, byte.class);
        buildShort(new ICRow(), JavaPrimitive.SHORT, short.class);
        buildInt(new ICRow(), JavaPrimitive.INT, int.class);
        buildLong(new ICRow(), JavaPrimitive.LONG, long.class);

        buildFloat(new ICRow(), JavaPrimitive.FLOAT, float.class);
        buildDouble(new ICRow(), JavaPrimitive.DOUBLE, double.class);
        buildBoolean(new ICRow(), JavaPrimitive.BOOLEAN, boolean.class);
        buildChar(new ICRow(), JavaPrimitive.CHAR, char.class);
        buildString(new ICRow(), String.class);
    }

    public FieldTypeInformation getStringFieldTypeInfo() {
        return stringFieldTypeInfo;
    }

    private void buildByte(ICRow row, JavaPrimitive prim, Class<?> targetClass) {
        ImplicitConverter castConv = createCastCon(targetClass);

        //byte,short,int,long  float,double, boolean, char
        addToRow(row, JavaPrimitive.BYTE, nothingConv);
        addToRow(row, JavaPrimitive.SHORT, castConv);
        addToRow(row, JavaPrimitive.INT, castConv);
        addToRow(row, JavaPrimitive.LONG, castConv);

        //TODO for now cast float to int. truncates. later add a Rounder plugin
        addToRow(row, JavaPrimitive.FLOAT, castConv);
        addToRow(row, JavaPrimitive.DOUBLE, castConv);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRow(row, JavaPrimitive.CHAR, nothingConv);
        addFromString(row, prim);

        addToRenderMap(row, prim);
    }

    private void buildShort(ICRow row, JavaPrimitive prim, Class<?> targetClass) {
        ImplicitConverter castConv = createCastCon(targetClass);

        //byte,short,int,long  float,double, boolean, char
        addToRow(row, JavaPrimitive.BYTE, nothingConv);
        addToRow(row, JavaPrimitive.SHORT, nothingConv);
        addToRow(row, JavaPrimitive.INT, castConv);
        addToRow(row, JavaPrimitive.LONG, castConv);

        //TODO for now cast float to int. truncates. later add a Rounder plugin
        addToRow(row, JavaPrimitive.FLOAT, castConv);
        addToRow(row, JavaPrimitive.DOUBLE, castConv);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRow(row, JavaPrimitive.CHAR, nothingConv);
        addFromString(row, prim);

        addToRenderMap(row, prim);
    }

    private void buildInt(ICRow row, JavaPrimitive prim, Class<?> targetClass) {
        ImplicitConverter castConv = createCastCon(targetClass);

        //byte,short,int,long  float,double, boolean, char
        addToRow(row, JavaPrimitive.BYTE, nothingConv);
        addToRow(row, JavaPrimitive.SHORT, nothingConv);
        addToRow(row, JavaPrimitive.INT, nothingConv);
        addToRow(row, JavaPrimitive.LONG, castConv);

        //TODO for now cast float to int. truncates. later add a Rounder plugin
        addToRow(row, JavaPrimitive.FLOAT, castConv);
        addToRow(row, JavaPrimitive.DOUBLE, castConv);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRow(row, JavaPrimitive.CHAR, nothingConv);
        addFromString(row, prim);

        addToRenderMap(row, prim);
    }

    private void buildLong(ICRow row, JavaPrimitive prim, Class<?> targetClass) {
        ImplicitConverter castConv = createCastCon(targetClass);

        //byte,short,int,long  float,double, boolean, char
        addToRow(row, JavaPrimitive.BYTE, nothingConv);
        addToRow(row, JavaPrimitive.SHORT, nothingConv);
        addToRow(row, JavaPrimitive.INT, nothingConv);
        addToRow(row, JavaPrimitive.LONG, nothingConv);

        //TODO for now cast float to int. truncates. later add a Rounder plugin
        addToRow(row, JavaPrimitive.FLOAT, castConv);
        addToRow(row, JavaPrimitive.DOUBLE, castConv);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRow(row, JavaPrimitive.CHAR, nothingConv);
        addFromString(row, prim);

        addToRenderMap(row, prim);
    }

    private void buildFloat(ICRow row, JavaPrimitive prim, Class<?> targetClass) {
        ImplicitConverter castConv = createCastCon(targetClass);

        //byte,short,int,long  float,double, boolean, char
        addToRow(row, JavaPrimitive.BYTE, nothingConv);
        addToRow(row, JavaPrimitive.SHORT, nothingConv);
        addToRow(row, JavaPrimitive.INT, nothingConv);
        addToRow(row, JavaPrimitive.LONG, nothingConv);

        addToRow(row, JavaPrimitive.FLOAT, nothingConv);
        //TODO for now cast float to double. truncates. later add a Rounder plugin
        addToRow(row, JavaPrimitive.DOUBLE, castConv);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRow(row, JavaPrimitive.CHAR, nothingConv);
        addFromString(row, prim);

        addToRenderMap(row, prim);
    }

    private void buildDouble(ICRow row, JavaPrimitive prim, Class<?> targetClass) {
        ImplicitConverter castConv = createCastCon(targetClass);

        //byte,short,int,long  float,double, boolean, char
        addToRow(row, JavaPrimitive.BYTE, nothingConv);
        addToRow(row, JavaPrimitive.SHORT, nothingConv);
        addToRow(row, JavaPrimitive.INT, nothingConv);
        addToRow(row, JavaPrimitive.LONG, nothingConv);

        addToRow(row, JavaPrimitive.FLOAT, nothingConv);
        addToRow(row, JavaPrimitive.DOUBLE, nothingConv);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRow(row, JavaPrimitive.CHAR, nothingConv);
        addFromString(row, prim);

        addToRenderMap(row, prim);
    }

    private void buildBoolean(ICRow row, JavaPrimitive prim, Class<?> targetClass) {
        ImplicitConverter castConv = createCastCon(targetClass);

        //byte,short,int,long  float,double, boolean, char
        //TODO: later add plugin for boolean
//not-supported            addToRow(row, JavaPrimitive.BYTE, iconvNothing);
//not-supported            addToRow(row, JavaPrimitive.SHORT, iconvNothing);
//not-supported            addToRow(row, JavaPrimitive.INT, iconvNothing);
//not-supported            addToRow(row, JavaPrimitive.LONG, iconvNothing);

//not-supported            addToRow(row, JavaPrimitive.FLOAT, iconvCast);
//not-supported            addToRow(row, JavaPrimitive.DOUBLE, iconvCast);
        addToRow(row, JavaPrimitive.BOOLEAN, nothingConv);
//not-supported            addToRow(row, JavaPrimitive.CHAR, iconvNothing);
        addFromString(row, prim);

        addToRenderMap(row, prim);
    }

    private void buildChar(ICRow row, JavaPrimitive prim, Class<?> targetClass) {
        ImplicitConverter castConv = createCastCon(targetClass);

        //byte,short,int,long  float,double, boolean, char
        addToRow(row, JavaPrimitive.BYTE, castConv);
        addToRow(row, JavaPrimitive.SHORT, castConv);
        addToRow(row, JavaPrimitive.INT, castConv);
        addToRow(row, JavaPrimitive.LONG, castConv);

        addToRow(row, JavaPrimitive.FLOAT, castConv);
        addToRow(row, JavaPrimitive.DOUBLE, castConv);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRow(row, JavaPrimitive.CHAR, nothingConv);
        addFromString(row, prim);

        addToRenderMap(row, prim);
    }

    private void buildString(ICRow row, Class<?> targetClass) {
        ImplicitConverter castConv = createCastCon(targetClass);

        //byte,short,int,long  float,double, boolean, char
        addToRow(row, JavaPrimitive.BYTE, new PrimToStringConverter(JavaPrimitive.BYTE));
        addToRow(row, JavaPrimitive.SHORT, new PrimToStringConverter(JavaPrimitive.SHORT));
        addToRow(row, JavaPrimitive.INT, new PrimToStringConverter(JavaPrimitive.INT));
        addToRow(row, JavaPrimitive.LONG, new PrimToStringConverter(JavaPrimitive.LONG));

        addToRow(row, JavaPrimitive.FLOAT, new PrimToStringConverter(JavaPrimitive.FLOAT));
        addToRow(row, JavaPrimitive.DOUBLE, new PrimToStringConverter(JavaPrimitive.DOUBLE));
        addToRow(row, JavaPrimitive.BOOLEAN, new PrimToStringConverter(JavaPrimitive.BOOLEAN));
        addToRow(row, JavaPrimitive.CHAR, new PrimToStringConverter(JavaPrimitive.CHAR));

        String key = stringFieldTypeInfo.createKey();
        row.map.put(key, nothingConv);
        addToRenderMap(row, stringFieldTypeInfo);
    }
}
