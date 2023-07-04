package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.implicitconverter.sampleclass.SampleClass1;
import org.dnal.fieldcopy.implicitconverter.sampleclass.SampleClass2;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.JavaPrimitive;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code int -> Integer. eg Integer x = n;}
 * src: int
 * dest: Integer
 */
public class PrimToScalarBuilder extends RenderMapBuilderBase {
    protected Map<JavaPrimitive, FieldTypeInformation> primTypeMap = new HashMap<>();

    @Override
    public void init(Map<String, ICRow> renderMap, FieldTypeInformation stringFieldTypeInfo) {
        this.renderMap = renderMap;
        this.stringFieldTypeInfo = stringFieldTypeInfo;

        //Scalars
        TypeMapBuilder typeMapBuilder = new TypeMapBuilder();
        typeMapBuilder.buildTypeMap(typeMap, SampleClass2.class); //Scalar
        //Prims
        typeMapBuilder.buildTypeMap(primTypeMap, SampleClass1.class);
    }

    @Override
    public void build() {
        //byte,short,int,long  float,double, boolean, char
        buildByte(JavaPrimitive.BYTE, byte.class);
        buildShort(JavaPrimitive.SHORT, short.class);
        buildInt(JavaPrimitive.INT, int.class);
        buildLong(JavaPrimitive.LONG, long.class);
        buildFloat(JavaPrimitive.FLOAT, float.class);
        buildDouble(JavaPrimitive.DOUBLE, double.class);
        buildBoolean(JavaPrimitive.BOOLEAN, boolean.class);
        buildChar(JavaPrimitive.CHAR, char.class);
        //we've already done String
    }

    private void buildByte(JavaPrimitive prim, Class<?> targetClass) {
        ICRow row = findRow(prim);

        //byte,short,int,long  float,double, boolean, char
        addToRow(row, JavaPrimitive.BYTE, nothingConv);
        addToRowPS(row, JavaPrimitive.SHORT, prim);
        addToRowPS(row, JavaPrimitive.INT, prim);
        addToRowPS(row, JavaPrimitive.LONG, prim);

        addToRowPS(row, JavaPrimitive.FLOAT, prim);
        addToRowPS(row, JavaPrimitive.DOUBLE, prim);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRowPS(row, JavaPrimitive.CHAR, prim);
        addFromString(row, prim);
    }

    private void buildShort(JavaPrimitive prim, Class<?> targetClass) {
        ICRow row = findRow(prim);

        //byte,short,int,long  float,double, boolean, char
        addToRowPS(row, JavaPrimitive.BYTE, prim);
        addToRow(row, JavaPrimitive.SHORT, nothingConv);
        addToRowPS(row, JavaPrimitive.INT, prim);
        addToRowPS(row, JavaPrimitive.LONG, prim);

        addToRowPS(row, JavaPrimitive.FLOAT, prim);
        addToRowPS(row, JavaPrimitive.DOUBLE, prim);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRowPS(row, JavaPrimitive.CHAR, prim);
        addFromString(row, prim);
    }

    private void buildInt(JavaPrimitive prim, Class<?> targetClass) {
        ICRow row = findRow(prim);

        //byte,short,int,long  float,double, boolean, char
        addToRowPS(row, JavaPrimitive.BYTE, prim);
        addToRowPS(row, JavaPrimitive.SHORT, prim);
        addToRow(row, JavaPrimitive.INT, nothingConv);
        addToRowPS(row, JavaPrimitive.LONG, prim);

        addToRowPS(row, JavaPrimitive.FLOAT, prim);
        addToRowPS(row, JavaPrimitive.DOUBLE, prim);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRowPS(row, JavaPrimitive.CHAR, prim);
        addFromString(row, prim);
    }

    private void addToRowPS(ICRow row, JavaPrimitive srcPrim, JavaPrimitive destPrim) {
        PrimToScalarConverter spConv = new PrimToScalarConverter(srcPrim, destPrim);
        addToRow(row, srcPrim, spConv);
    }

    private void buildLong(JavaPrimitive prim, Class<?> targetClass) {
        ICRow row = findRow(prim);

        //byte,short,int,long  float,double, boolean, char
        addToRowPS(row, JavaPrimitive.BYTE, prim);
        addToRowPS(row, JavaPrimitive.SHORT, prim);
        addToRowPS(row, JavaPrimitive.INT, prim);
        addToRow(row, JavaPrimitive.LONG, nothingConv);

        addToRowPS(row, JavaPrimitive.FLOAT, prim);
        addToRowPS(row, JavaPrimitive.DOUBLE, prim);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRowPS(row, JavaPrimitive.CHAR, prim);
        addFromString(row, prim);
    }

    private void buildFloat(JavaPrimitive prim, Class<?> targetClass) {
        ICRow row = findRow(prim);

        //byte,short,int,long  float,double, boolean, char
        addToRowPS(row, JavaPrimitive.BYTE, prim);
        addToRowPS(row, JavaPrimitive.SHORT, prim);
        addToRowPS(row, JavaPrimitive.INT, prim);
        addToRowPS(row, JavaPrimitive.LONG, prim);

        addToRow(row, JavaPrimitive.FLOAT, nothingConv);
        addToRowPS(row, JavaPrimitive.DOUBLE, prim);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRowPS(row, JavaPrimitive.CHAR, prim);
        addFromString(row, prim);
    }

    private void buildDouble(JavaPrimitive prim, Class<?> targetClass) {
        ICRow row = findRow(prim);

        //byte,short,int,long  float,double, boolean, char
        addToRowPS(row, JavaPrimitive.BYTE, prim);
        addToRowPS(row, JavaPrimitive.SHORT, prim);
        addToRowPS(row, JavaPrimitive.INT, prim);
        addToRowPS(row, JavaPrimitive.LONG, prim);

        addToRowPS(row, JavaPrimitive.FLOAT, prim);
        addToRow(row, JavaPrimitive.DOUBLE, nothingConv);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRowPS(row, JavaPrimitive.CHAR, prim);
        addFromString(row, prim);
    }

    private void buildBoolean(JavaPrimitive prim, Class<?> targetClass) {
        ICRow row = findRow(prim);
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
    }

    private void buildChar(JavaPrimitive prim, Class<?> targetClass) {
        ICRow row = findRow(prim);

        //byte,short,int,long  float,double, boolean, char
        addToRowPS(row, JavaPrimitive.BYTE, prim);
        addToRowPS(row, JavaPrimitive.SHORT, prim);
        addToRowPS(row, JavaPrimitive.INT, prim);
        addToRowPS(row, JavaPrimitive.LONG, prim);

        addToRowPS(row, JavaPrimitive.FLOAT, prim);
        addToRowPS(row, JavaPrimitive.DOUBLE, prim);
        //JavaPrimitive.BOOLEAN not supported  TODO: later support a BoolToInt plugin
        addToRow(row, JavaPrimitive.CHAR, nothingConv);
        addFromString(row, prim);
    }

    //there is no buildString needed

    private ICRow findRow(JavaPrimitive prim) {
        FieldTypeInformation fieldTypeInfo = typeMap.get(prim);
        ICRow row = renderMap.get(fieldTypeInfo.createKey());
        return row;
    }

    protected void addToRow(ICRow row, JavaPrimitive prim, ImplicitConverter iconv) {
        FieldTypeInformation fieldTypeInfo = primTypeMap.get(prim);
        String key = fieldTypeInfo.createKey();
        row.map.put(key, iconv);
    }
}
