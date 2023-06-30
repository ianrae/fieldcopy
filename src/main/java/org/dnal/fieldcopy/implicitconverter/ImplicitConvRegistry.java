package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.fieldspec.SingleFld;
import org.dnal.fieldcopy.types.FieldTypeInformation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

public class ImplicitConvRegistry {
    private final FieldTypeInformation stringFieldTypeInfo;
    private Map<String, ICRow> renderMap = new HashMap<>(); //row per dest type.

    public ImplicitConvRegistry(Map<String, ICRow> renderMap, FieldTypeInformation stringFieldTypeInfo) {
        this.renderMap = renderMap;
        this.stringFieldTypeInfo = stringFieldTypeInfo;
    }

    //if returns true then convL contains the converter that will be used
    //if returns false then convL will have DoNothingConverter
    public boolean isConversionSupported(SingleFld srcFld, SingleFld destFld, List<ImplicitConverter> convL) {
        return isConversionSupported(srcFld.fieldTypeInfo, destFld.fieldTypeInfo, convL);
    }
    public boolean isConversionSupported(FieldTypeInformation srcFieldInfo, FieldTypeInformation destFieldInfo, List<ImplicitConverter> convL) {
        String key1 = srcFieldInfo.createKey();
        String key2 = destFieldInfo.createKey();

        //renderMap is rows for dest type
        ICRow row = renderMap.get(key2);
        if (isNull(row)) {
            return false;
        }

        //each row contains a converter for a src type
        ImplicitConverter iconv = row.map.get(key1);
        if (iconv != null) {
            convL.add(iconv);
        }

        if (iconv instanceof DoNothingImplicitConverter) {
            return false;
        }

        return iconv != null;
    }

    public ICRow getRowForDestType(SingleFld destFld) {
        return getRowForDestType(destFld.fieldTypeInfo);
    }
    public ICRow getRowForDestType(FieldTypeInformation ftiDest) {
        String key = ftiDest.createKey();
        ICRow row = renderMap.get(key);
        return row;
    }

    public FieldTypeInformation getStringFieldTypeInfo() {
        return stringFieldTypeInfo;
    }

    //    public void addRowEx(String key, ICRow row) {
//        renderMap.put(key, row);
//    }
    public boolean isStringField(SingleFld destFld) {
        return isStringField(destFld.fieldTypeInfo);
    }
    public boolean isStringField(FieldTypeInformation fti) {
        String key1 = stringFieldTypeInfo.createKey();
        String key2 = fti.createKey();
        return key1.equals(key2);
    }

    public void addToRenderMap(ICRow row, FieldTypeInformation fieldTypeInfo) {
        String key = fieldTypeInfo.createKey();
        renderMap.put(key, row);
    }
}
