package org.dnal.fieldcopy.newcodegen;

import org.dnal.fieldcopy.fieldspec.NormalFieldSpec;
import org.dnal.fieldcopy.fieldspec.SingleFld;
import org.dnal.fieldcopy.fieldspec.SingleValue;
import org.dnal.fieldcopy.implicitconverter.EnumToStringConverter;
import org.dnal.fieldcopy.implicitconverter.ICRow;
import org.dnal.fieldcopy.implicitconverter.ImplicitConvRegistry;
import org.dnal.fieldcopy.implicitconverter.StringToEnumConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;

import static java.util.Objects.isNull;

public class EnumHandler {

    private final ImplicitConvRegistry implicitConvRegistry;

    public EnumHandler(ImplicitConvRegistry implicitConvRegistry) {
        this.implicitConvRegistry = implicitConvRegistry;
    }

    public void generateEnumImplicitConverters(NormalFieldSpec nspec) {
        if (isNull(implicitConvRegistry)) return;

        if (isNull(nspec.srcText) || isNull(nspec.destText)) {
            return;
        }

        SingleFld srcFld = nspec.srcFldX.getLast(); //eg Color
        //if src is value then treat src as string
        if (srcFld instanceof SingleValue) {
            srcFld = new SingleValue((SingleValue) srcFld); //copy-ctor
            srcFld.fieldType = implicitConvRegistry.getStringFieldTypeInfo().getFieldType();
            srcFld.fieldTypeInfo = implicitConvRegistry.getStringFieldTypeInfo();
        }

        Class<?> srcClazz = srcFld.fieldType; //srcFld.fieldTypeInfo.fieldType;
        SingleFld destFld = nspec.destFldX.getLast(); //eg colorName
        Class<?> destClazz = destFld.fieldType; //srcFld.fieldTypeInfo.fieldType;


        //handle optional by removing it while we build a converter
        FieldTypeInformation srcWithoutOptional = srcFld.fieldTypeInfo.createNonOptional();
        FieldTypeInformation destWithoutOptional = destFld.fieldTypeInfo.createNonOptional();
        addConverterIfNeeded(srcClazz, destClazz, srcWithoutOptional, destWithoutOptional);
    }

    private void addConverterIfNeeded(Class<?> srcClazz, Class<?> destClazz, FieldTypeInformation srcWithoutOptional, FieldTypeInformation destWithoutOptional) {
        if (srcClazz.isEnum()) {
            //generate a specific converter for srcClass -> string
            //User can still override using an explicit Converter
            String key1 = srcWithoutOptional.createKey();
            if (implicitConvRegistry.isStringField(destWithoutOptional)) {
                ICRow row = implicitConvRegistry.getRowForDestType(destWithoutOptional);
                EnumToStringConverter conv = new EnumToStringConverter(srcClazz);
                row.map.put(key1, conv);
            }
        } else if (implicitConvRegistry.isStringField(srcWithoutOptional) && destClazz.isEnum()) {
            ICRow row = implicitConvRegistry.getRowForDestType(destWithoutOptional);
            if (isNull(row)) {
                row = new ICRow();
                implicitConvRegistry.addToRenderMap(row, destWithoutOptional); //TODO is this thead-safe?
            }

            //generate a specific converter for string -> srcClass
            //User can still override using an explicit Converter
            String key1 = srcWithoutOptional.createKey();
            StringToEnumConverter conv = new StringToEnumConverter(destClazz);
            row.map.put(key1, conv);
        }
    }

}
