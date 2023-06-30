package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.fieldspec.SingleFld;
import org.dnal.fieldcopy.implicitconverter.date.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

public class ImplicitConverterRegistryBuilder {
    private Map<String, ICRow> renderMap = new HashMap<>(); //row per dest type.
    private PrimBuilder primBuilder = new PrimBuilder();

    public void init() {
        primBuilder.init(renderMap, null); //stringFieldTypeInfo not available yet when we build Prim
    }

    public void startBuild() {
        primBuilder.build();
    }

    public void buildAdditional() {
        List<RenderMapBuilder> additionalBuilders = new ArrayList<>();
        ScalarBuilder scalarBuilder = new ScalarBuilder();
        additionalBuilders.add(scalarBuilder);
        PrimToScalarBuilder builder2 = new PrimToScalarBuilder();
        ScalarToPrimBuilder builder3 = new ScalarToPrimBuilder();
        additionalBuilders.add(builder2);
        additionalBuilders.add(builder3);

        LocalDateBuilder dateBuilder = new LocalDateBuilder();
        LocalTimeBuilder timeBuilder = new LocalTimeBuilder();
        LocalDateTimeBuilder dateTimeBuilder = new LocalDateTimeBuilder();
        ZonedDateTimeBuilder zonedBuilder = new ZonedDateTimeBuilder();
        DateBuilder utilDateBuilder = new DateBuilder();
        additionalBuilders.add(dateBuilder);
        additionalBuilders.add(timeBuilder);
        additionalBuilders.add(dateTimeBuilder);
        additionalBuilders.add(zonedBuilder);
        additionalBuilders.add(utilDateBuilder);

        buildAdditionalEx(additionalBuilders);
    }

    //for unit tests only
    public void buildAdditionalEx(List<RenderMapBuilder> additionalBuilders) {
        for(RenderMapBuilder builder: additionalBuilders) {
            builder.init(renderMap, primBuilder.getStringFieldTypeInfo());
            builder.build();
        }
    }

    public PrimBuilder getPrimBuilder() {
        return primBuilder;
    }

    public Map<String, ICRow> getRenderMap() {
        return renderMap;
    }

    public boolean isConversionSupported(SingleFld srcFld, SingleFld destFld) {
        String key1 = srcFld.fieldTypeInfo.createKey();
        String key2 = destFld.fieldTypeInfo.createKey();

        //renderMap is rows for dest type
        ICRow row = renderMap.get(key2);
        if (isNull(row)) {
            return false;
        }

        //each row contains a converter for a src type
        ImplicitConverter iconv = row.map.get(key1);
        return iconv != null;
    }
}
