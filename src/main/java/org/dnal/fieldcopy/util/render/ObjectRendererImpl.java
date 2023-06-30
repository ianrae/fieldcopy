package org.dnal.fieldcopy.util.render;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.dnal.fieldcopy.codegen.FieldCopyException;

import java.io.IOException;

public class ObjectRendererImpl implements ObjectRenderer {
    private ObjectMapper mapper;
    private boolean prettyPrint;

    public ObjectRendererImpl() {
        this(false);
    }

    public ObjectRendererImpl(boolean prettyPrint) {
        //Note. need JavaTimeModule in order to be able to parse/render Java 8 date/time types
        //https://stackoverflow.com/questions/50108812/jackson-javatimemodule-not-found-even-after-adding-jackson-modules-java8-depende
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());

        //and render java.util.Date in IS8601 format https://www.baeldung.com/jackson-serialize-dates
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // StdDateFormat is ISO8601 since jackson 2.9
        mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));

        //optional rendering
//        SimpleModule module = new SimpleModule();
//        module.addSerializer(Optional.class, new CustomOptionalSerializer());
//        mapper.registerModule(module);

        this.prettyPrint = prettyPrint;
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @Override
    public String render(Object obj) {
        String json = null;
        try {
            if (prettyPrint) {
                json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            } else {
                json = mapper.writeValueAsString(obj);
            }
        } catch (JsonProcessingException e) {
            System.out.println("OBJRENDERFAIL: " + e.getMessage());
        }
        return json;
    }

    public Object parse(String json, Class<?> clazz) {
        Object obj = null;
        try {
            obj = mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new FieldCopyException(e.getMessage());
        } catch (IOException e) {
            throw new FieldCopyException(e.getMessage());
        }
        return obj;
    }


}