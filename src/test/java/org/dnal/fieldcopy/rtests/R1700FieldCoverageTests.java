package org.dnal.fieldcopy.rtests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.dataclass.Address;
import org.dnal.fieldcopy.dataclass.ZoneAddress;
import org.dnal.fieldcopy.types.ClassTypeHelper;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;
import org.dnal.fieldcopy.util.ReflectionUtil;
import org.dnal.fieldcopy.util.render.ObjectRenderer;
import org.dnal.fieldcopy.util.render.ObjectRendererImpl;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
     R1700  codegen
     -fieldcopy picks the converter class name
     -explicit converter name
 */
public class R1700FieldCoverageTests extends RTestBase {

    /**
     * Compares two struct objects
     * -ignores primitive fields like int or boolean
     */
    public static class FieldCoverageHelper {
        private ObjectMapper mapper = new ObjectMapper();

        public boolean checkConversion(Object src, Object destInitial, Object destFinal) {
            Class<?> srcClass = src.getClass();
            Class<?> destClass = destInitial.getClass();
            chkDestHasSameClassAsDestFinal(destInitial, destFinal);
            chkIsStruct(src);
            chkIsStruct(destInitial);

            ReflectionUtil helper = new ReflectionUtil();
            List<String> allSrcFieldNames = helper.getAllFieldNames(srcClass);
            List<String> allDestFieldNames = helper.getAllFieldNames(destClass);

            Map<String, Object> srcValueMap = buildValueMap(src, allSrcFieldNames);
            Map<String, Object> destValueMap = buildValueMap(destFinal, allDestFieldNames);

            boolean ok = true;
            for(String fieldName: srcValueMap.keySet()) {
                if (! destValueMap.containsKey(fieldName)) {
                    log(String.format("src field '%s' not in dest", fieldName));
                    ok = false;
                }
            }

            return ok;
        }

        private Map<String, Object> buildValueMap(Object src, List<String> allFieldNames) {
            ObjectRenderer renderer = new ObjectRendererImpl();
            String json = renderer.render(src);
            Map<String, Object> srcValueMap = parseJson(json);
            Map<String, Object> map2 = parseJson(json);
            for (String key : srcValueMap.keySet()) {
                if (allFieldNames.contains(key)) {
                    Object val = srcValueMap.get(key);
                    map2.put(key, val);
                }
            }
            return map2;
        }

        private void chkIsStruct(Object src) {
            //assume src not optional!
            FieldTypeInformation fti = FieldTypeInformationImpl.create(src.getClass());
            if (!ClassTypeHelper.isStructType(fti)) {
                throwError("%s is not a struct type. Only struct types all supported", src.getClass().getSimpleName());
            }
        }

        private Map<String, Object> parseJson(String json) {
            Map<String, Object> map = null;
            try {
                Object obj = mapper.readValue(json, Map.class);
                map = (Map<String, Object>) obj;
            } catch (JsonProcessingException e) {
                throwError("invalid JSON: %s", e.getMessage());
            } catch (IOException e) {
                throwError("invalid JSON: %s", e.getMessage());
            }
            return map;
        }

        private void chkDestHasSameClassAsDestFinal(Object destInitial, Object destFinal) {
            Class<?> destClass = destInitial.getClass();
            Class<?> destFinalClass = destFinal.getClass();

            if (!destClass.isAssignableFrom(destFinalClass)) {
                throwError("destInitial and destFinal must have the same class: '%s', '%s'", destClass.getName(), destFinalClass.getName());
            }
        }

        private void throwError(String fmt, String... args) {
            String msg = String.format(fmt, args);
            throw new FieldCopyException(msg);
        }
        private void log(String s) {
            System.out.println(s);
        }
    }

    @Test
    public void test() {
        Address addr = createAddress(true);
        Address addrDest = createAddress(true);
        Address addrDest2 = createAddress(true);
        FieldCoverageHelper helper = new FieldCoverageHelper();

        boolean b = helper.checkConversion(addr, addrDest, addrDest2);
        assertEquals(true, b);
    }

    @Test
    public void test2() {
        Address addr = createAddress(false);
        Address addrDest = createAddress(true);
        Address addrDest2 = createAddress(true);
        FieldCoverageHelper helper = new FieldCoverageHelper();

        boolean b = helper.checkConversion(addr, addrDest, addrDest2);
        assertEquals(false, b);
    }

    @Test
    public void test3() {
        ZoneAddress addr = createZoneAddress(false);
        Address addrDest = createAddress(true);
        Address addrDest2 = createAddress(true);
        FieldCoverageHelper helper = new FieldCoverageHelper();

        boolean b = helper.checkConversion(addr, addrDest, addrDest2);
        assertEquals(false, b);
    }

    //----
    private Address createAddress(boolean isPartial) {
        Address obj = new Address();
        obj.setCity("kingston");
        obj.setBackRef(null);
        if (! isPartial) {
            obj.setStreet1("main");
            obj.setFlag1(true);
        }
        return obj;
    }

    private ZoneAddress createZoneAddress(boolean isPartial) {
        ZoneAddress obj = new ZoneAddress();
        obj.setCity("kingston");
        obj.setBackRef(null);
        if (! isPartial) {
            obj.setStreet1("main");
            obj.setFlag1(true);
        }
        obj.setZone("abc");
        return obj;
    }
}
