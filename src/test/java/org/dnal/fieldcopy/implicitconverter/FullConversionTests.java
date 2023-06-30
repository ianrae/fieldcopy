package org.dnal.fieldcopy.implicitconverter;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class FullConversionTests extends ImplicitConversionTestBase {


    @Test
    public void test() {
        initBuilder();
        Map<String, ICRow> renderMap = currentIcrBuilder.getRenderMap();
        assertEquals(17, renderMap.size()); //9 + 8 scalars


//        for (JavaPrimitive prim : JavaPrimitive.values()) {
//            String key = currentScalarBuilder.getKeyForPrim(prim);
//            ICRow row = renderMap.get(key);
//            //byte,short,int,long  float,double, boolean, char + STRING = 9
//            int n = 9 - 1; //most types don't have conversion for bool
//            switch (prim) {
//                case BOOLEAN:
//                    n = 2;
//                    break;
//                default:
//                    break;
//            }
//            assertEquals(n, row.map.size(), prim.name());
//        }

    }


    //============
    private ScalarBuilder currentScalarBuilder;

    private void initBuilder() {
        ImplicitConverterRegistryBuilder icrBuilder = createICRBuilder(); //does Prim build
        currentScalarBuilder = new ScalarBuilder();
        additionalBuilders.add(currentScalarBuilder);
        icrBuilder.buildAdditionalEx(additionalBuilders);
    }

}
