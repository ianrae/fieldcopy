//package org.dnal.fieldcopy.registry;
//
//import org.dnal.fieldcopy.Converter;
//import org.dnal.fieldcopy.codegen.gen.Src1ToDest1Converter;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class MyRegistry implements ConverterRegistry {
//    Map<String, Converter> map = new ConcurrentHashMap<>();
//
//    public MyRegistry() {
//        add(new Src1ToDest1Converter());
//        add(new Src1ToDest1Converter(), "otherOne");
//    }
//
//    protected void add(Converter converter) {
//        add(converter, null);
//    }
//
//    protected void add(Converter converter, String converterName) {
//        String key = makeKey(converter, converterName);
//        map.put(key, converter);
//    }
//
//    protected String makeKey(Converter converter, String converterName) {
//        String key = makeRawKey(converter.getSourceClass(), converter.getDestClass(), converterName);
//        return key;
//    }
//
//    protected String makeRawKey(Class<?> srcClass, Class<?> destClass, String converterName) {
//        String key = String.format("%s|%s", srcClass, destClass);
//        if (converterName != null) {
//            key = String.format("%s:%s", key, converterName);
//        }
//        return key;
//    }
//
//    @Override
//    public <S, T> Converter<S, T> find(Class<S> srcClass, Class<T> destClass) {
//        String key = makeRawKey(srcClass, destClass, null);
//        return map.get(key);
//    }
//
//    @Override
//    public <S, T> Converter<S, T> find(Class<S> srcClass, Class<T> destClass, String name) {
//        String key = makeRawKey(srcClass, destClass, name);
//        return map.get(key);
//    }
//
//    @Override
//    public int size() {
//        return map.size();
//    }
//
//    @Override
//    public Map<String, Converter> getMap() {
//        return map;
//    }
//}
