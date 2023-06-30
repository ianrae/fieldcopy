package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.TestBase;
import org.dnal.fieldcopy.codegen.FldXBuilder;
import org.dnal.fieldcopy.dataclass.Dest1;
import org.dnal.fieldcopy.dataclass.Src1;
import org.dnal.fieldcopy.fieldspec.FldChain;
import org.dnal.fieldcopy.implicitconverter.sampleclass.SampleClass1;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.types.FieldTypeInformation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 *
 */
public class ImplicitConversionTestBase extends TestBase {
    protected ImplicitConverterRegistryBuilder currentIcrBuilder;
    protected List<RenderMapBuilder> additionalBuilders = new ArrayList<>();
    protected FieldTypeInformation stringFieldTypeInfo;


    protected ImplicitConverterRegistryBuilder createICRBuilder() {
        ImplicitConverterRegistryBuilder icrBuilder = new ImplicitConverterRegistryBuilder();
        icrBuilder.init();
        icrBuilder.startBuild();
        currentIcrBuilder = icrBuilder;
        return icrBuilder;
    }

    protected ConversionInfo buildConvInfo(String srcText, String destText) {
        Class<?> srcClass = Src1.class;
        Class<?> destClass = Dest1.class;
        return doBuildConvInfo(srcClass, destClass, srcText, destText);
    }

    protected ConversionInfo buildConvInfoSample(String srcText, String destText) {
        Class<?> srcClass = SampleClass1.class;
        Class<?> destClass = SampleClass1.class;
        return doBuildConvInfo(srcClass, destClass, srcText, destText);
    }


    protected ConversionInfo doBuildConvInfo(Class<?> srcClass, Class<?> destClass, String srcText, String destText) {
        FldXBuilder fldXBuilder = new FldXBuilder(new FieldCopyOptions());

        FldChain srcFldX = fldXBuilder.buildFldX(srcClass, srcText);
        FldChain destFldX = fldXBuilder.buildFldX(destClass, destText);

        ImplicitConvertService implicitConvertSvc = new ImplicitConvertService();
        ConversionInfo info = implicitConvertSvc.calcConversionInfo(srcFldX, destFldX);
        info.isSupported = true;
        if (info.needsConversion) {
            info.isSupported = currentIcrBuilder.isConversionSupported(info.srcFld, info.destFld);
        }
        //so if not needConversion then is supported (ie. not an error)

        return info;
    }

    protected void doChkNotSupported(ICRow row, String key) {
        ImplicitConverter iconv = row.map.get(key);
        assertEquals(null, iconv);
    }

    protected void doChkOne(ICRow row, String key, String expected) {
        ImplicitConverter iconv = row.map.get(key);

        String src = iconv.gen("x");
        assertEquals(expected, src);
    }

    protected void doChkString(ICRow row, String key, String expected) {
        ImplicitConverter iconv = row.map.get(key);

        String src = iconv.gen("x");
        assertEquals(expected, src);
    }

    protected void chkFromString(ICRow row, String expected) {
        doChkOne(row, stringFieldTypeInfo.createKey(), expected);
    }

}
