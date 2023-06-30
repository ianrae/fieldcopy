package org.dnal.fieldcopy.registry;

import org.apache.commons.lang3.StringUtils;
import org.dnal.fieldcopy.runtime.ObjectConverter;
import org.dnal.fieldcopy.types.FieldTypeInformation;
import org.dnal.fieldcopy.types.FieldTypeInformationImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

public class ConverterRegistryBase implements ConverterRegistry {
    Map<String, ObjectConverter> map;

    public ConverterRegistryBase() {
        this.map = new ConcurrentHashMap<>();
    }

    public ConverterRegistryBase(Map<String, ObjectConverter> map) {
        this.map = map;
    }

    protected void add(ObjectConverter converter) {
        add(converter, null);
    }

    protected void add(ObjectConverter converter, String converterName) {
        if (isNull(converterName)) {
            String key = makeKey(converter, converterName);
            map.put(key, converter);
        } else {
            String key = makeRawKeyForName(converterName);
            map.put(key, converter);
        }
    }

    protected String makeKey(ObjectConverter converter, String converterName) {
        String key = makeRawKey(converter.getSourceFieldTypeInfo(), converter.getDestinationFieldTypeInfo(), converterName);
        return key;
    }

    protected String makeRawKey(FieldTypeInformation srcInfo, FieldTypeInformation destInfo, String converterName) {
        String key = String.format("%s$%s", srcInfo.createKey(), destInfo.createKey());
        if (converterName != null) {
            key = String.format("%s:%s", key, converterName);
        }
        return key;
    }

    protected String makeRawKeyForName(String converterName) {
        String key = String.format("%s$%s", "_", "_");
        if (converterName != null) {
            key = String.format("%s:%s", key, converterName);
        }
        return key;
    }

    @Override
    public <S, T> ObjectConverter<S, T> find(Class<S> srcClass, Class<T> destClass) {
        FieldTypeInformation srcInfo = new FieldTypeInformationImpl(srcClass);
        FieldTypeInformation destInfo = new FieldTypeInformationImpl(destClass);
        return find(srcInfo, destInfo);
    }

    @Override
    public boolean exists(String converterName) {
        String key = makeRawKeyForName(converterName);
        return map.containsKey(key);
    }
    @Override
    public <S, T> ObjectConverter<S, T> find(Class<S> srcClass, Class<T> destClass, String converterName) {
        String key = makeRawKeyForName(converterName);
        return map.get(key);
    }

    @Override
    public <S, T> ObjectConverter<S, T> find(FieldTypeInformation srcInfo, FieldTypeInformation destInfo) {
        String key = makeRawKey(srcInfo, destInfo, null);
//        for(String s: map.keySet()) {
//            System.out.println(s);;
//        }
//        System.out.println(key);

        return map.get(key);
    }

    @Override
    public <S, T> ObjectConverter<S, T> find(Class<S> srcClass, Class<T> destClass, FieldTypeInformation srcInfo, FieldTypeInformation destInfo, String name) {
        String key = makeRawKey(srcInfo, destInfo, name);
        return map.get(key);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Map<String, ObjectConverter> getMap() {
        return map;
    }

    public String parseConverterName(String key) {
        String convName = StringUtils.substringAfterLast(key, ":");
        return convName;
    }
}
