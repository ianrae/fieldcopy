package org.dnal.fieldcopy.util;

import org.dnal.fieldcopy.dataclass.AllPrims1;
import org.dnal.fieldcopy.dataclass.Customer;
import org.dnal.fieldcopy.dataclass.OptionalSrc1;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;
import org.dnal.fieldcopy.types.TypeTree;
import org.dnal.fieldcopy.types.TypeTreeBuilder;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReflectionUtilTests {

    public static class SampleClass33 {
        private Optional<List<String>>  list1;
        private List<Optional<String>> list2;

        public Optional<List<String>> getList1() {
            return list1;
        }

        public List<Optional<String>> getList2() {
            return list2;
        }
    }

    @Test
    public void testField() {
        ReflectionUtil helper = new ReflectionUtil();
        Optional<Field> fld = helper.findField(AllPrims1.class, "_int");
        assertEquals(true, fld.isPresent());

        Class<?> type1 = fld.get().getType();
        assertEquals("int", type1.getName());
        Type type2 = fld.get().getGenericType();
        assertEquals("int", type2.getTypeName());

        TypeTree typeTree = buildAndChk(type2, 0);
        chkIsList(typeTree, false);
        chkKey(type1, type2, "int|");
    }

    @Test
    public void testMethod() {
        ReflectionUtil helper = new ReflectionUtil();
        Optional<Method> meth = helper.findGetterMethod(Customer.class, "lastName");
        assertEquals(true, meth.isPresent());

        Class<?> type1 = meth.get().getReturnType();
        assertEquals("java.lang.String", type1.getName());
        Type type2 = meth.get().getGenericReturnType();
        assertEquals("java.lang.String", type2.getTypeName());

        buildAndChk(type2, 0);
        chkIsList(typeTree, false);
        chkKey(type1, type2, "String|");
    }

    @Test
    public void testGenericMethod() {
        ReflectionUtil helper = new ReflectionUtil();
        Optional<Method> meth = helper.findGetterMethod(Customer.class, "roles");
        assertEquals(true, meth.isPresent());

        Class<?> type1 = meth.get().getReturnType();
        assertEquals("java.util.List", type1.getName());
        Type type2 = meth.get().getGenericReturnType();
        assertEquals("java.util.List<java.lang.String>", type2.getTypeName());
        ParameterizedType ptype = (ParameterizedType) type2;
        assertEquals("java.util.List", ptype.getRawType().getTypeName());

        Type actualType = ptype.getActualTypeArguments()[0];
        assertEquals("java.lang.String", actualType.getTypeName());
        Class<?> clazz = (Class<?>) actualType;
        assertEquals("java.lang.String", clazz.getName());

        buildAndChk(type2, 2);
        chkTree(typeTree, 0, "java.util.List");
        chkTree(typeTree, 1, "java.lang.String");
        chkIsList(typeTree, true);
        chkKey(type1, type2, "List|[List,String]");
    }

    @Test
    public void testOptionalGenericMethod() {
        ReflectionUtil helper = new ReflectionUtil();
        Optional<Method> meth = helper.findGetterMethod(SampleClass33.class, "list1");
        assertEquals(true, meth.isPresent());

        Class<?> type1 = meth.get().getReturnType();
        assertEquals("java.util.Optional", type1.getName());
        Type type2 = meth.get().getGenericReturnType();
        assertEquals("java.util.Optional<java.util.List<java.lang.String>>", type2.getTypeName());
        ParameterizedType ptype = (ParameterizedType) type2;
        assertEquals("java.util.Optional", ptype.getRawType().getTypeName());

        Type actualType = ptype.getActualTypeArguments()[0];
        assertEquals("java.util.List<java.lang.String>", actualType.getTypeName());

        ParameterizedType ptype2 = (ParameterizedType) actualType;
        assertEquals("java.util.List", ptype2.getRawType().getTypeName());
        Type actualType2 = ptype2.getActualTypeArguments()[0];
        assertEquals("java.lang.String", actualType2.getTypeName());

        buildAndChk(type2, 4);
        chkTree(typeTree, 0, "java.util.Optional");
        chkTree(typeTree, 1, "java.util.List<java.lang.String>");
        chkTree(typeTree, 2, "java.util.List");
        chkTree(typeTree, 3, "java.lang.String");
        chkIsList(typeTree, false);
        chkKey(type1, type2, "Optional|[Optional,List<String>,Optional,List<String>]");
    }
    @Test
    public void testListOfOptionalMethod() {
        ReflectionUtil helper = new ReflectionUtil();
        Optional<Method> meth = helper.findGetterMethod(SampleClass33.class, "list2");
        assertEquals(true, meth.isPresent());

        Class<?> type1 = meth.get().getReturnType();
        assertEquals("java.util.List", type1.getName());
        Type type2 = meth.get().getGenericReturnType();
        assertEquals("java.util.List<java.util.Optional<java.lang.String>>", type2.getTypeName());
        ParameterizedType ptype = (ParameterizedType) type2;
        assertEquals("java.util.List", ptype.getRawType().getTypeName());

        Type actualType = ptype.getActualTypeArguments()[0];
        assertEquals("java.util.Optional<java.lang.String>", actualType.getTypeName());

        ParameterizedType ptype2 = (ParameterizedType) actualType;
        assertEquals("java.util.Optional", ptype2.getRawType().getTypeName());
        Type actualType2 = ptype2.getActualTypeArguments()[0];
        assertEquals("java.lang.String", actualType2.getTypeName());

        buildAndChk(type2, 4);
        chkTree(typeTree, 0, "java.util.List");
        chkTree(typeTree, 1, "java.util.Optional<java.lang.String>");
        chkTree(typeTree, 2, "java.util.Optional");
        chkTree(typeTree, 3, "java.lang.String");
        chkIsList(typeTree, true);
        chkKey(type1, type2, "List|[List,Optional<String>,List,Optional<String>]");
    }

    @Test
    public void testOptionalMethod() {
        ReflectionUtil helper = new ReflectionUtil();
        Field fld = helper.findField(OptionalSrc1.class, "s2").get();

        Class<?> type1 = fld.getType();
        assertEquals("java.util.Optional", type1.getName());
        Type type2 = fld.getGenericType();
        assertEquals("java.util.Optional<java.lang.String>", type2.getTypeName());

        ParameterizedType ptype = (ParameterizedType) type2;
        assertEquals("java.util.Optional", ptype.getRawType().getTypeName());

        Type actualType = ptype.getActualTypeArguments()[0];
        assertEquals("java.lang.String", actualType.getTypeName());
        Class<?> clazz = (Class<?>) actualType;
        assertEquals("java.lang.String", clazz.getName());

        buildAndChk(type2, 2);
        chkTree(typeTree, 0, "java.util.Optional");
        chkTree(typeTree, 1, "java.lang.String");
        chkIsList(typeTree, false);
        chkKey(type1, type2, "Optional|[Optional,String]");
    }

    //------------
    private TypeTree typeTree;

    private TypeTree buildAndChk(Type type2, int expected) {
        TypeTreeBuilder builder = new TypeTreeBuilder();
        typeTree = builder.build(type2);
        assertEquals(expected, typeTree.size());
        return typeTree;
    }
    private void chkTree(TypeTree typeTree, int i, String expected) {
        String s = typeTree.getIthName(i);
        assertEquals(expected, s);
    }
    private void chkIsList(TypeTree typeTree, boolean expected) {
        FieldTypeInformationImpl myz = new FieldTypeInformationImpl(String.class, null, typeTree); //only need 3rd param
        boolean b = myz.isList();
        assertEquals(expected, b);
    }
    private void chkKey(Class<?> fieldClass, Type genericType, String expected) {
        FieldTypeInformationImpl myz = new FieldTypeInformationImpl(fieldClass, genericType, typeTree);
        String key = myz.createKey();
        assertEquals(expected, key);
    }
}
