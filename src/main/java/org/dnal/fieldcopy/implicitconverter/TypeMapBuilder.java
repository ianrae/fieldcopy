package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.codegen.FldXBuilder;
import org.dnal.fieldcopy.fieldspec.SingleFld;
import org.dnal.fieldcopy.implicitconverter.sampleclass.SampleClass1;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.JavaPrimitive;

import java.util.Map;

public class TypeMapBuilder {

    private FieldCopyOptions options = new FieldCopyOptions(); //not really needed by xBuilder.setFieldType
    //TODO maybe later we will need the real options object

    public void buildTypeMap(Map<JavaPrimitive, FieldTypeInformation> typeMap, Class<?> sampleClazz) {
        //TODO for now we need a struct class (such as SampleClass1) to build these. Later build w/o needing struct class
        FldXBuilder xBuilder = new FldXBuilder(options);
        for (JavaPrimitive prim : JavaPrimitive.values()) {
            String fieldName = String.format("_%s", JavaPrimitive.lowify(prim));
            SingleFld fld = new SingleFld(fieldName);
            xBuilder.setFieldType(fld, sampleClazz, fieldName);

            typeMap.put(prim, fld.fieldTypeInfo);
        }

    }

    public FieldTypeInformation buildForString() {
        //TODO for now we need a struct class (such as SampleClass1) to build these. Later build w/o needing struct class
        FldXBuilder xBuilder = new FldXBuilder(options);
        String fieldName = "_string";
        SingleFld fld = new SingleFld(fieldName);
        xBuilder.setFieldType(fld, SampleClass1.class, fieldName);
        return fld.fieldTypeInfo;
    }
}
