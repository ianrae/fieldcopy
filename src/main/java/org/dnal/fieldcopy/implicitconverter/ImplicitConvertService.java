package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.fieldspec.FldChain;
import org.dnal.fieldcopy.fieldspec.SingleFld;
import org.dnal.fieldcopy.types.FieldTypeInformation;

import java.util.List;

public class ImplicitConvertService {
    public ConversionInfo calcConversionInfo(FldChain srcFldX, FldChain destFldX) {
        SingleFld srcFld = srcFldX.flds.get(0);
        SingleFld destFld = destFldX.flds.get(0);

        ConversionInfo convInfo = new ConversionInfo(srcFld, destFld);
        convInfo.needsConversion = !srcFld.fieldTypeInfo.isEqual(destFld.fieldTypeInfo);
        return convInfo;
    }

    /**
     * ImplicityConvRegistry doesn't know about Optional, so we need to strip out any optional-ness in src and dest
     * before checking if conversion is supported.
     *
     * @param implicitConvRegistry
     * @param srcFld
     * @param destFld
     * @return true if conversion is supported
     */
    public boolean isConversionSupported(ImplicitConvRegistry implicitConvRegistry, SingleFld srcFld, SingleFld destFld, List<ImplicitConverter> convL) {
        if (implicitConvRegistry.isConversionSupported(srcFld, destFld, convL)) {
            return true;
        }

        //try non-optional versions. if fieldTypeInfo is not optional then createNonOptional() returns identical copy
        FieldTypeInformation srcWithoutOptional = srcFld.fieldTypeInfo.createNonOptional();
        FieldTypeInformation destWithoutOptional = destFld.fieldTypeInfo.createNonOptional();
        if (implicitConvRegistry.isConversionSupported(srcWithoutOptional, destWithoutOptional, convL)) {
            return true;
        }
        return false;
    }

    /**
     * Are these the same types (ignoring whether they are optional)
     *
     * @return true if are same underlying type
     */
    public boolean areSameTypes(FieldTypeInformation ftiSrc, FieldTypeInformation ftiDest, List<ImplicitConverter> convL) {
        if (convL.size() > 0) {
            ImplicitConverter conv = convL.get(0);
            if (conv instanceof DoNothingImplicitConverter) {
                return true;
            }
        }

        //try non-optional versions. if fieldTypeInfo is not optional then createNonOptional() returns identical copy
        FieldTypeInformation srcWithoutOptional = ftiSrc.createNonOptional();
        FieldTypeInformation destWithoutOptional = ftiDest.createNonOptional();
        String key1 = srcWithoutOptional.createKey();
        String key2 = destWithoutOptional.createKey();
        return key1.equals(key2);
    }

    public ICRow getRowForDestType(ImplicitConvRegistry implicitConvRegistry, SingleFld destFld) {
        FieldTypeInformation destWithoutOptional = destFld.fieldTypeInfo.createNonOptional();
        ICRow row = implicitConvRegistry.getRowForDestType(destWithoutOptional);
        return row;
    }

    public ImplicitConverter getConverterForSrc(SingleFld srcFld, ICRow row) {
        FieldTypeInformation srcWithoutOptional = srcFld.fieldTypeInfo.createNonOptional();
        String srcKey = srcWithoutOptional.createKey();
        ImplicitConverter iconv = row.map.get(srcKey);
        return iconv;
    }


}
