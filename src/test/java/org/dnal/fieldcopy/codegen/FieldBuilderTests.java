package org.dnal.fieldcopy.codegen;

import org.dnal.fieldcopy.TestBase;
import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.dataclass.OptionalSrc1;
import org.dnal.fieldcopy.dataclass.Src1;
import org.dnal.fieldcopy.fieldspec.FldChain;
import org.dnal.fieldcopy.fieldspec.SingleFld;
import org.dnal.fieldcopy.parser.fieldcopyjson.FieldCopyOptions;
import org.dnal.fieldcopy.util.ClassNameUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FieldBuilderTests extends TestBase {

    @Test
    public void test() {
        Class<?> srcClass = Src1.class;
        chkField(srcClass, "n1", "int", false, false, false);
        chkField(srcClass, "s2", "String", false, false, false);
        chkField(srcClass, "inner1", "org.dnal.fieldcopy.dataclass.Inner1", false, false, false);
        SingleFld fld = chkField(srcClass, "col1", "org.dnal.fieldcopy.dataclass.Color", false, false, false);
        assertEquals(0, fld.fieldTypeInfo.getTypeTree().size());
    }

    @Test
    public void testOptional() {
        Class<?> srcClass = OptionalSrc1.class;
        chkField(srcClass, "s2", "String", true, false, false);
        chkField(srcClass, "inner1", "org.dnal.fieldcopy.dataclass.Inner1", true, false, false);
        SingleFld fld = chkField(srcClass, "col1", "org.dnal.fieldcopy.dataclass.Color", true, false, false);
        chkClassName("org.dnal.fieldcopy.dataclass.Color", fld.fieldTypeInfo.getFirstActual());
    }

    @Test
    public void testList() {
        Class<?> srcClass = Customer.class;
        SingleFld fld = chkField(srcClass, "roles", "List", false, true, false);
        chkClassName("String", fld.fieldTypeInfo.getFirstActual());
    }
    //TODO map, array, set

    //-------------------------------
    private SingleFld chkField(Class<?> srcClass, String fieldName, String fieldTypeStr, boolean isOptional, boolean isList, boolean isMap) {
        FldXBuilder fldXBuilder = new FldXBuilder(new FieldCopyOptions());
        FldChain srcFldX = fldXBuilder.buildFldX(srcClass, fieldName);
        assertEquals(1, srcFldX.size());
        SingleFld fld = srcFldX.getFirst();
        assertEquals(fieldName, fld.fieldName);

        chkClassName(fieldTypeStr, fld.fieldType);
        chkFieldInfo(fld, fieldTypeStr, isOptional, isList, isMap);
        return fld;
    }

    private void chkFieldInfo(SingleFld fld, String expectedType, boolean isOptional, boolean isList, boolean isMap) {
        String className = ClassNameUtil.renderClassName(fld.fieldTypeInfo.getFieldType().getName());
        if (isOptional) {
            expectedType = "Optional";
        }
        if (isList) {
            expectedType = "List";
        }
        if (isMap) {
            expectedType = "Map";
        }

        assertEquals(expectedType, className);
        assertEquals(isOptional, fld.fieldTypeInfo.isOptional());
        assertEquals(isList, fld.fieldTypeInfo.isList());
        assertEquals(isMap, fld.fieldTypeInfo.isMap());
    }

    private void chkClassName(String expectedType, Class<?> clazz) {
        String className = ClassNameUtil.renderClassName(clazz.getName());
        assertEquals(expectedType, className);
    }
}
