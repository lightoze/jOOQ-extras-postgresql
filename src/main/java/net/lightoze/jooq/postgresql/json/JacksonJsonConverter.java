package net.lightoze.jooq.postgresql.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import org.jooq.JSON;

import java.io.IOException;

public class JacksonJsonConverter<T> extends AbstractJacksonConverter<JSON, T, T> {

    public JacksonJsonConverter() {
    }

    public JacksonJsonConverter(JavaType userType, JavaType elementType) {
        super(userType, elementType);
    }

    @Override
    public T from(JSON json) {
        if (json == null || json.data().equals("null")) {
            return getNull();
        } else {
            try {
                return getObjectReader().readValue(json.data());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public JSON to(T value) {
        if (value == null || isNull(value)) {
            return null;
        } else {
            try {
                return JSON.valueOf(getObjectWriter().writeValueAsString(value));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Class<JSON> fromType() {
        return JSON.class;
    }

}
