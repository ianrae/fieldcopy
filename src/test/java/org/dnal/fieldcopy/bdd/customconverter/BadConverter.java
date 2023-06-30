package org.dnal.fieldcopy.bdd.customconverter;

import org.dnal.fieldcopy.runtime.ConverterContext;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;
import org.dnal.fieldcopy.types.TypeTree;

import java.util.Optional;

//class without default constructor
public class BadConverter implements ObjectConverter {
    private FieldTypeInformation srcInfo;
    private FieldTypeInformation destInfo;

    public BadConverter(double dd) {
        TypeTree typeTree = new TypeTree();
        typeTree.addPair(Optional.class, String.class);
        srcInfo = new FieldTypeInformationImpl(Optional.class, String.class, typeTree);

        typeTree = new TypeTree();
        typeTree.addPair(Optional.class, Integer.class);
        destInfo = new FieldTypeInformationImpl(Optional.class, Integer.class, typeTree);
    }

    @Override
    public FieldTypeInformation getSourceFieldTypeInfo() {
        return srcInfo;
    }

    @Override
    public FieldTypeInformation getDestinationFieldTypeInfo() {
        return destInfo;
    }

    @Override
    public Object convert(Object src, Object dest, ConverterContext ctx) {
        return null;
    }
}
