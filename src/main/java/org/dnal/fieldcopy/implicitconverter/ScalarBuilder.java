package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.implicitconverter.sampleclass.SampleClass2;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.JavaPrimitive;

import java.util.Map;

public class ScalarBuilder extends RenderMapBuilderBase {

    public ScalarBuilder() {
    }

    @Override
    public void init(Map<String, ICRow> renderMap, FieldTypeInformation stringFieldTypeInfo) {
        this.renderMap = renderMap;
        this.stringFieldTypeInfo = stringFieldTypeInfo;

        TypeMapBuilder typeMapBuilder = new TypeMapBuilder();
        typeMapBuilder.buildTypeMap(typeMap, SampleClass2.class);
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
        buildString(String.class);
    }

    private void buildByte(ICRow row, JavaPrimitive prim, Class<?> targetClass) {
        ScalarToScalarConverter ssConv = new ScalarToScalarConverter(prim);

        //byte,short,int,long  float,double, boolean, char
        addToRow(row, JavaPrimitive.BYTE, nothingConv);
        addToRow(row, JavaPrimitive.SHORT, ssConv);
        addToRow(row, JavaPrimitive.INT, ssConv);
        addToRow(row, JavaPrimitive.LONG, ssConv);

        addToRow(row, JavaPrimitive.FLOAT, ssConv);
        addToRow(row, JavaPrimitive.DOUBLE, ssConv);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRow(row, JavaPrimitive.CHAR, new CharacterToScalarConverter(prim));
        addFromString(row, prim);

        addToRenderMap(row, prim);
    }

    private void buildShort(ICRow row, JavaPrimitive prim, Class<?> targetClass) {
        ScalarToScalarConverter ssConv = new ScalarToScalarConverter(prim);

        //byte,short,int,long  float,double, boolean, char
        addToRow(row, JavaPrimitive.BYTE, ssConv);
        addToRow(row, JavaPrimitive.SHORT, nothingConv);
        addToRow(row, JavaPrimitive.INT, ssConv);
        addToRow(row, JavaPrimitive.LONG, ssConv);

        addToRow(row, JavaPrimitive.FLOAT, ssConv);
        addToRow(row, JavaPrimitive.DOUBLE, ssConv);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRow(row, JavaPrimitive.CHAR, new CharacterToScalarConverter(prim));
        addFromString(row, prim);

        addToRenderMap(row, prim);
    }

    private void buildInt(ICRow row, JavaPrimitive prim, Class<?> targetClass) {
        ScalarToScalarConverter ssConv = new ScalarToScalarConverter(prim);

        //byte,short,int,long  float,double, boolean, char
        addToRow(row, JavaPrimitive.BYTE, ssConv);
        addToRow(row, JavaPrimitive.SHORT, ssConv);
        addToRow(row, JavaPrimitive.INT, nothingConv);
        addToRow(row, JavaPrimitive.LONG, ssConv);

        addToRow(row, JavaPrimitive.FLOAT, ssConv);
        addToRow(row, JavaPrimitive.DOUBLE, ssConv);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRow(row, JavaPrimitive.CHAR, new CharacterToScalarConverter(prim));
        addFromString(row, prim);

        addToRenderMap(row, prim);
    }

    private void buildLong(ICRow row, JavaPrimitive prim, Class<?> targetClass) {
        ScalarToScalarConverter ssConv = new ScalarToScalarConverter(prim);

        //byte,short,int,long  float,double, boolean, char
        addToRow(row, JavaPrimitive.BYTE, ssConv);
        addToRow(row, JavaPrimitive.SHORT, ssConv);
        addToRow(row, JavaPrimitive.INT, ssConv);
        addToRow(row, JavaPrimitive.LONG, nothingConv);

        addToRow(row, JavaPrimitive.FLOAT, ssConv);
        addToRow(row, JavaPrimitive.DOUBLE, ssConv);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRow(row, JavaPrimitive.CHAR, new CharacterToScalarConverter(prim));
        addFromString(row, prim);

        addToRenderMap(row, prim);
    }

    private void buildFloat(ICRow row, JavaPrimitive prim, Class<?> targetClass) {
        ScalarToScalarConverter ssConv = new ScalarToScalarConverter(prim);

        //byte,short,int,long  float,double, boolean, char
        addToRow(row, JavaPrimitive.BYTE, ssConv);
        addToRow(row, JavaPrimitive.SHORT, ssConv);
        addToRow(row, JavaPrimitive.INT, ssConv);
        addToRow(row, JavaPrimitive.LONG, ssConv);

        addToRow(row, JavaPrimitive.FLOAT, nothingConv);
        addToRow(row, JavaPrimitive.DOUBLE, ssConv);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRow(row, JavaPrimitive.CHAR, new CharacterToScalarConverter(prim));
        addFromString(row, prim);

        addToRenderMap(row, prim);
    }

    private void buildDouble(ICRow row, JavaPrimitive prim, Class<?> targetClass) {
        ScalarToScalarConverter ssConv = new ScalarToScalarConverter(prim);

        //byte,short,int,long  float,double, boolean, char
        addToRow(row, JavaPrimitive.BYTE, ssConv);
        addToRow(row, JavaPrimitive.SHORT, ssConv);
        addToRow(row, JavaPrimitive.INT, ssConv);
        addToRow(row, JavaPrimitive.LONG, ssConv);

        addToRow(row, JavaPrimitive.FLOAT, ssConv);
        addToRow(row, JavaPrimitive.DOUBLE, nothingConv);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRow(row, JavaPrimitive.CHAR, new CharacterToScalarConverter(prim));
        addFromString(row, prim);

        addToRenderMap(row, prim);
    }

    private void buildBoolean(ICRow row, JavaPrimitive prim, Class<?> targetClass) {
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
        ImplicitConverter scConv = new ScalarToCharacterConverterConverter(prim);

        //byte,short,int,long  float,double, boolean, char
        addToRow(row, JavaPrimitive.BYTE, scConv);
        addToRow(row, JavaPrimitive.SHORT, scConv);
        addToRow(row, JavaPrimitive.INT, scConv);
        addToRow(row, JavaPrimitive.LONG, scConv);

        addToRow(row, JavaPrimitive.FLOAT, scConv);
        addToRow(row, JavaPrimitive.DOUBLE, scConv);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRow(row, JavaPrimitive.CHAR, nothingConv);
        addFromString(row, prim);

        addToRenderMap(row, prim);
    }

    private void buildString(Class<?> targetClass) {
        ICRow row = renderMap.get(stringFieldTypeInfo.createKey()); //add to existing String row

        ImplicitConverter castConv = new ObjToStringConverter();
        //Note. String is done in primBuilder so we add to its map

        //byte,short,int,long  float,double, boolean, char
        addToRow(row, JavaPrimitive.BYTE, castConv);
        addToRow(row, JavaPrimitive.SHORT, castConv);
        addToRow(row, JavaPrimitive.INT, castConv);
        addToRow(row, JavaPrimitive.LONG, castConv);

        addToRow(row, JavaPrimitive.FLOAT, castConv);
        addToRow(row, JavaPrimitive.DOUBLE, castConv);
        addToRow(row, JavaPrimitive.BOOLEAN, castConv);
        addToRow(row, JavaPrimitive.CHAR, castConv);
        //STRING already done in Prim

        //addToRenderMap(row, stringFieldTypeInfo);
    }

}
