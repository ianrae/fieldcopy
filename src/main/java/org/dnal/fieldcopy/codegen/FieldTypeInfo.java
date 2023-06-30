//package org.dnal.fieldcopy.codegen;
//
//import org.dnal.fieldcopy.util.ClassNameUtil;
//import org.dnal.fieldcopy.util.StrCreator;
//
//import java.util.Optional;
//
//import static java.util.Objects.isNull;
//  replaced by FieldTypeInformation
//public class FieldTypeInfo {
//    public Class<?> fieldType;
//    public Optional<Class<?>> rawElementType; //for List or key of Map
//    public Optional<Class<?>> rawMapValueType;
//    public Optional<Class<?>> rawOptionalType;
//    public Optional<FieldTypeInfo> elementType; //in case list of list
//    public Optional<FieldTypeInfo> mapT;
//    public Optional<FieldTypeInfo> optT;
//
//    public FieldTypeInfo() {
//        rawElementType = Optional.empty();
//        rawMapValueType = Optional.empty();
//        rawOptionalType = Optional.empty();
//    }
//
//    public FieldTypeInfo(Class<?> fieldType, Optional<Class<?>> rawElementType, Optional<Class<?>> rawMapValueType) {
//        this.fieldType = fieldType;
//        this.rawElementType = rawElementType;
//        this.rawMapValueType = rawMapValueType;
//        this.rawOptionalType = Optional.empty();
//    }
//
//    public boolean isList() {
//        return rawElementType.isPresent() && !rawMapValueType.isPresent();
//    }
//
//    public boolean isMap() {
//        return rawElementType.isPresent() && rawMapValueType.isPresent();
//    }
//
//    public boolean isOptional() {
//        return rawOptionalType.isPresent();
//    }
//
//    public boolean isEqual(FieldTypeInfo other) {
//        if (fieldType != other.fieldType) {
//            return false;
//        }
//        if (!sameClass(rawElementType, other.rawElementType)) {
//            return false;
//        }
//
//        return true;
//    }
//
//    private boolean sameClass(Optional<Class<?>> elementType, Optional<Class<?>> elementType2) {
//        Class<?> clazz1 = elementType.orElse(null);
//        Class<?> clazz2 = elementType2.orElse(null);
//
//        if (isNull(clazz1) && isNull(clazz2)) {
//            return true;
//        } else if (isNull(clazz1) && !isNull(clazz2)) {
//            return false;
//        } else if (!isNull(clazz1) && isNull(clazz2)) {
//            return false;
//        } else {
//            return clazz1.equals(clazz2);
//        }
//    }
//
//    //unique key so can use in Map
//    public String createKey() {
//        StrCreator sc = new StrCreator();
//
//        String s = fieldType.getName();
//        s = ClassNameUtil.renderClassName(s);
//
//        sc.o("%s|", s);
//        sc.o("el:%s|", formatOpt(rawElementType));
//        sc.o("map:%s|", formatOpt(rawMapValueType));
//        sc.o("opt:%s", formatOpt(rawOptionalType));
//        return sc.toString();
//    }
//
//    private String formatOpt(Optional<Class<?>> opt) {
//        if (opt.isPresent()) {
//            String s = ClassNameUtil.renderClassName(opt.get().getName());
//            return s;
//        } else {
//            return "";
//        }
//    }
//
//    public String getListElClassName() {
//        String srcEl = elementType.get().fieldType.getName();
//        return ClassNameUtil.renderClassName(srcEl);
//    }
//
//}
