//package org.dnal.fieldcopy.bdd.core;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.dnal.fieldcopy.codegen.FieldCopyException;
//import org.dnal.fieldcopy.log.FieldCopyLog;
//import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
//import java.io.IOException;
//
///**
// *
// */
//public class ObjParser {
//    private FieldCopyLog log;
//    private ObjectMapper mapper = new ObjectMapper();
//
//    public ObjParser(FieldCopyLog log) {
//        this.log = log;
//        //need this to parse into Optional fields https://www.baeldung.com/jackson-optional
//        mapper.registerModule(new Jdk8Module());
//    }
//
//    public Object parse(String json, Class<?> clazz) {
//        Object obj = null;
//        try {
//            obj = mapper.readValue(json, clazz);
//        } catch (JsonProcessingException e) {
//            throw new FieldCopyException(e.getMessage());
//        } catch (IOException e) {
//            throw new FieldCopyException(e.getMessage());
//        }
//        return obj;
//    }
//
//}
