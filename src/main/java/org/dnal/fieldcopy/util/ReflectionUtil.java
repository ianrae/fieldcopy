package org.dnal.fieldcopy.util;

import org.apache.commons.lang3.StringUtils;
import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;
import org.dnal.fieldcopy.types.TypeTree;
import org.dnal.fieldcopy.types.TypeTreeBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ReflectionUtil {

    public List<String> getAllFieldNames(Class<?> clazz) {
        List<String> allFieldNames = new ArrayList<>();
        Field[] allFields = clazz.getFields();
        for (Field f : allFields) {
            allFieldNames.add(f.getName());
        }

        Method[] allMethods = clazz.getMethods();
        for (Method m : allMethods) {
            String name = m.getName();
            if (name.equals("getClass")) {
                continue;
            }

            if (!handlePrefix("get", name, m, allFieldNames)) {
                handlePrefix("is", name, m, allFieldNames);
            }
        }

        return allFieldNames;
    }

    private boolean handlePrefix(String prefix, String name, Method meth, List<String> allFieldNames) {
        if (meth.getParameterCount() > 0) {
            return false;
        }
        if (name.startsWith(prefix)) {
            String suffix = StringUtils.substringAfter(name, prefix);
            if (suffix.length() > 0) {
                char ch = suffix.charAt(0);
                if (Character.isUpperCase(ch)) {
                    String fieldName = StringUtil.lowify(suffix);
                    if (!allFieldNames.contains(fieldName)) {
                        allFieldNames.add(fieldName);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //    public boolean isField(Class<?> clazz, String fieldName) {
//        Field f = getField(clazz, fieldName);
//        return f != null;
//    }
//    public Field getField(Class<?> clazz, String fieldName) {
//        Field[] allFields = clazz.getFields();
//
//        for (Field f : allFields) {
//            if (f.getName().equals(fieldName)) {
//                return f;
//            }
//        }
//        return null;
//    }
    public boolean isGetterMethod(Class<?> clazz, String fieldName) {
        Method m = getGetterMethod(clazz, fieldName);
        return m != null;
    }
    public boolean getterStartsWithIs(Class<?> clazz, String fieldName) {
        Method m = getGetterMethod(clazz, fieldName);
        if (m != null) {
            String name = m.getName();
            return name.startsWith("is");
        }
        return false;
    }

    private Method getGetterMethod(Class<?> clazz, String fieldName) {
        Method[] allMethods = clazz.getMethods();
        String target = buildGetterName(fieldName);
        String target2 = buildIsGetterName(fieldName);

        for (Method m : allMethods) {
            if (m.getName().equals(target)) {
                return m;
            }
            if (m.getName().equals(target2)) {
                return m;
            }
        }
        return null;
    }

//    public Class<?> getFieldOrGetterType(Class<?> clazz, String fieldName) {
//        Field f = getField(clazz, fieldName);
//        if (isNull(f)) {
//            Method m = getGetterMethod(clazz, fieldName);
//            if (isNull(m)) {
//                return null;
//            }
//            return m.getReturnType();
//        }
//        return f.getType();
//    }

    public String buildGetterName(String fieldName) {
        String target = String.format("get%s", StringUtil.uppify(fieldName)); //TODO add isXXX later
        return target;
    }

    private String buildIsGetterName(String fieldName) {
        String target = String.format("is%s", StringUtil.uppify(fieldName)); //TODO add isXXX later
        return target;
    }

    public boolean isFieldOrGetter(Class<?> clazz, String fieldName) {
        Optional<Field> opt = findField(clazz, fieldName);
        if (opt.isPresent()) {
            Field field = opt.get(); // TODO handle missing
            return true;
        } else {
            Optional<Method> optMethod = findGetterMethod(clazz, fieldName);
            return optMethod.isPresent();
        }
    }

    public FieldTypeInformation getFieldInformation(Class<?> clazz, String fieldName) {
        Optional<Field> opt = findField(clazz, fieldName);
        if (opt.isPresent()) {
            Field field = opt.get(); // TODO handle missing
            return doGetFieldInformation(field.getType(), field.getGenericType());
        } else {
            Optional<Method> optMethod = findGetterMethod(clazz, fieldName);
            if (!optMethod.isPresent()) {
                String msg = String.format("Can't find field '%s' in class '%s'", fieldName, clazz.getName());
                throw new FieldCopyException(msg);
            }
            return doGetFieldInformation(optMethod.get().getReturnType(), optMethod.get().getGenericReturnType());
        }
    }

    private FieldTypeInformation doGetFieldInformation(Class<?> fieldType, Type genericType) {
        TypeTreeBuilder builder = new TypeTreeBuilder();
        TypeTree typeTree = builder.build(genericType);
        FieldTypeInformationImpl myz = new FieldTypeInformationImpl(fieldType, genericType, typeTree);
        return myz;
    }

    public Optional<Field> findField(Class<?> clazz, String fieldName) {
        Optional<Field> opt = Arrays.stream(clazz.getFields()).filter(x -> x.getName().equals(fieldName)).findAny();
        return opt;
    }


    public Optional<Method> findGetterMethod(Class<?> clazz, String fieldName) {
        final String target = buildGetterName(fieldName);
        Optional<Method> optMethod = Arrays.stream(clazz.getMethods()).filter(x -> x.getName().equals(target)).findAny();
        if (!optMethod.isPresent()) {
            final String target2 = buildIsGetterName(fieldName);
            optMethod = Arrays.stream(clazz.getMethods()).filter(x -> x.getName().equals(target2)).findAny();
        }
        return optMethod;
    }


    public Class<?> getClassFromName(String className) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            String msg = String.format("unknown class: %s", className);
            throw new FieldCopyException(msg);
        }
        return clazz;
    }

    public Object createObj(Class<?> clazz) {
        Object obj = null;
        try {
//                Object obj = groupClass.newInstance();
            obj = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return obj;
    }

}
