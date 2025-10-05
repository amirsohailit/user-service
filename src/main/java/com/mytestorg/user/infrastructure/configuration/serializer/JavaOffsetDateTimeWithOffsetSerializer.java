package com.mytestorg.user.infrastructure.configuration.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.OffsetDateTime;

import static com.mytestorg.user.infrastructure.configuration.serializer.JavaDateTimeFormatUtil.serializeOffsetDateTimeWithOffset;

@Slf4j
public class JavaOffsetDateTimeWithOffsetSerializer extends JsonSerializer<OffsetDateTime> {

    @Override
    public void serialize(OffsetDateTime value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        try {
            jsonGenerator.writeString(serializeOffsetDateTimeWithOffset(value));
        } catch (Exception e) {
            System.out.println("Error in JavaOffsetDateTimeWithOffsetSerializer" + value + ":" + e.getMessage());
            log.error("JavaOffsetDateTimeWithUtcSerializer error" + value + ":" + e.getMessage());
        }
    }

}
