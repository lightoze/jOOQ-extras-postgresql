package net.lightoze.jooq.postgresql.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import org.jooq.JSONB;

import java.io.IOException;

public class JacksonJsonbConverter<T> extends AbstractJacksonConverter<JSONB, T> {

    public JacksonJsonbConverter() {
    }

    public JacksonJsonbConverter(JavaType type) {
        super(type);
    }

    @Override
    public T from(JSONB json) {
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
    public JSONB to(T value) {
        if (value == null || isNull(value)) {
            return null;
        } else {
            try {
                return JSONB.valueOf(getObjectWriter().writeValueAsString(value));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Class<JSONB> fromType() {
        return JSONB.class;
    }

}
