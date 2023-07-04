package org.dnal.fieldcopy.group;

import org.dnal.fieldcopy.codegen.FieldCopyException;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.util.ReflectionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;

public class ObjectConverterFinder {
    private String packageToUse;

    public ObjectConverterFinder() {
    }

    public ObjectConverterFinder(String packageToUse) {
        this.packageToUse = packageToUse;
    }

    public ObjectConverter<?,?> findOneConverter(String converterName) {
        List<String> names = Arrays.asList(converterName);
        List<ObjectConverter<?,?>> resultL = findConverters(names);
        return resultL.isEmpty() ? null : resultL.get(0);
    }

    public List<ObjectConverter<?,?>> findConverters(List<String> converterNames) {
        List<ObjectConverter<?,?>> resultL = new ArrayList<>();
        ReflectionUtil helper = new ReflectionUtil();

        for (String converterClassName : converterNames) {
            String fullName = buildFullName(converterClassName); //String.format("%s.%s", packageToUse, converterClassName);
//            System.out.println(fullName);
            Class<?> clazz = helper.getClassFromName(fullName);
            if (isNull(clazz)) {
                String msg = String.format("can't find additional converter '%s'", converterClassName);
//                    addError("additional.converter.not.found", msg);
                //TODO later collect all errors and return them as FCErrors
                throw new FieldCopyException(msg);
            } else {
                ObjectConverter<?,?> conv = (ObjectConverter<?,?>) helper.createObj(clazz);
                if (conv == null) {
                    throw new FieldCopyException("failed to create: " + clazz);
                }
                resultL.add(conv);
            }
        }
        return resultL;
    }

    public String buildFullName(String converterClassName) {
        if (converterClassName.contains(".")) {
            return converterClassName; //already has package
        }
        String fullName = String.format("%s.%s", packageToUse, converterClassName);
        return fullName;
    }
}
