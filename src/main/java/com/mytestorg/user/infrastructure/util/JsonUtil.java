package com.mytestorg.user.infrastructure.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@AllArgsConstructor
public class JsonUtil {
    private final ObjectMapper mapper;

    public <T> T toObject(String jsonInString, Class<T> aClass) {

        T object = null;
        try {
            object = mapper.readValue(jsonInString.getBytes(), aClass);
        } catch (IOException e) {
            log.error("Can not construct object from json {}", e);
        }
        return object;
    }

    public String toJson(Object object) {
        String string = null;
        try {
            string = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Can not construct string from object {}", e);
        }
        return string;
    }
}
