package org.dnal.fieldcopy.types;

public interface FieldTypeInformation {
    Class<?> getFieldType();

    boolean isList();

    boolean isMap();

    boolean isOptional();

    boolean isEqual(FieldTypeInformation other);

    String createKey();

    String getJavaClassName();

    TypeTree getTypeTree();

    Class<?> getFirstActual(); //TODO extend later to use whole type tree
    Class<?> getEffectiveType();

    FieldTypeInformation createNonOptional();
}
