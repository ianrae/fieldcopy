package org.dnal.fieldcopy.types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeTreeBuilder {
    public TypeTree build(Type genericType) {
        TypeTree typeTree = new TypeTree();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType) genericType;

            boolean done = false;
            while (!done) { //TODO do we need runaway detector
                Type rawType = ptype.getRawType();
                Type actualType = ptype.getActualTypeArguments()[0];
                typeTree.addPair(rawType, actualType);

                if (ptype.getActualTypeArguments().length > 1) {
                    Type mapValueType = ptype.getActualTypeArguments()[1];
                    typeTree.setMapValueType(mapValueType);
                }

                if (actualType instanceof ParameterizedType) {
                    ptype = (ParameterizedType) actualType;
                } else {
                    done = true;
                }
            }
            return typeTree;
        } else {
            return typeTree; //empty
        }
    }
}
