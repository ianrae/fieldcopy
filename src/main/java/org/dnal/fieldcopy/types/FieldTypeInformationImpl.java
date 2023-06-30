package org.dnal.fieldcopy.types;

import org.dnal.fieldcopy.util.ClassNameUtil;
import org.dnal.fieldcopy.util.StrCreator;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FieldTypeInformationImpl implements FieldTypeInformation {
    private Class<?> fieldType;
    private Type genericType; //not used
    private TypeTree typeTree;

    public FieldTypeInformationImpl(Class<?> fieldType, Type genericType, TypeTree typeTree) {
        this.fieldType = fieldType;
        this.genericType = genericType;
        this.typeTree = typeTree;
    }

    public FieldTypeInformationImpl(Class<?> fieldType) {
        this.fieldType = fieldType;
        this.genericType = null;
        this.typeTree = new TypeTree();
    }

    public static FieldTypeInformation create(Class<?> srcClass) {
        TypeTree typeTree = new TypeTree();
        return new FieldTypeInformationImpl(srcClass, srcClass, typeTree);
    }

    public static FieldTypeInformation createForList(Class<?> srcClass) {
        TypeTree typeTree = new TypeTree();
        typeTree.addPair(List.class, srcClass);
        return new FieldTypeInformationImpl(List.class, srcClass, typeTree);
    }

    @Override
    public FieldTypeInformation createNonOptional() {
        if (isOptional()) {
            //extract the actual type and create info w/o optional
            //TODO fix for lists,etc
            Class<?> actualType = getFirstActual();
            TypeTree emptyTree = new TypeTree();
            return new FieldTypeInformationImpl(actualType, actualType, emptyTree);
        } else {
            return new FieldTypeInformationImpl(fieldType, genericType, typeTree); //return identical copy
        }
    }

    @Override
    public Class<?> getFieldType() {
        return fieldType;
    }

    @Override
    public boolean isList() {
        //we are only determining if field or getter's type is list
        //not whether part of the type is list. eg Optional<List<String>> would be false
        if (typeTree.size() > 0) {
            Class<?> clazz = getFirstRawAsClass();
            if (Collection.class.isAssignableFrom(clazz)) {
                return true;
            }
        }

        return false;
    }

    private Class<?> getFirstRawAsClass() {
        Type rawType = typeTree.getFirstRaw();
        Class<?> clazz = (Class<?>) rawType; //TODO is this always safe?
        return clazz;
    }

    @Override
    public boolean isMap() {
        //we are only determining if field or getter's type is map
        //not whether part of the type is list. eg Optional<Map<String,Customer>> would be false
        if (typeTree.size() > 0) {
            Class<?> clazz = getFirstRawAsClass();
            if (Map.class.isAssignableFrom(clazz)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isOptional() {
        //we are only determining if field or getter's type is optional
        //not whether part of the type is list. eg Optional<Map<String,Customer>> would be false
        if (typeTree.size() > 0) {
            Class<?> clazz = getFirstRawAsClass();
            if (Optional.class.isAssignableFrom(clazz)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isEqual(FieldTypeInformation other) {
        String key = createKey();
        String key2 = other.createKey();
        return key.equals(key2);
    }

    @Override
    public String createKey() {
        StrCreator sc = new StrCreator();
        String s = fieldType.getName();
        s = ClassNameUtil.renderClassName(s);

        sc.o("%s|", s);
        if (typeTree.size() > 0) {
            sc.addStr("[");
            for (int iPair = 0; iPair < typeTree.size() / 2; iPair++) {
                Type rawType = typeTree.getIthRaw(iPair);
                Type actualType = typeTree.getIthActual(iPair);
                String s1 = ClassNameUtil.renderClassName(rawType.getTypeName());
                s1 = s1.replace("java.lang.", "");
                String s2 = ClassNameUtil.renderClassName(actualType.getTypeName());
                s2 = s2.replace("java.lang.", "");
                if (iPair != 0) {
                    sc.addStr(",");
                }
                sc.o("%s,%s", s1, s2);
            }
            sc.addStr("]");
        }
        return sc.toString();
    }

    @Override
    public String getJavaClassName() {
        return null;
    } //TODO is this needed?

    @Override
    public TypeTree getTypeTree() {
        return typeTree;
    }

    @Override
    public Class<?> getFirstActual() {
        Type actualType = getTypeTree().getFirstActual();
        Class<?> clazz = (Class<?>) actualType; //TODO is this safe?
        return clazz;
    }

    @Override
    public Class<?> getEffectiveType() {
        if (isList()) {
            return getFirstActual();
        }
        Class<?> clazz = isOptional() ? getFirstActual() : getFieldType();
        return clazz;
    }
}
