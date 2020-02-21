package net.lightoze.jooq.postgresql.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.lightoze.jooq.AbstractObjectBinding;
import org.jooq.Converter;
import org.postgresql.util.PGobject;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @deprecated Use {@link JacksonJsonConverter} instead since jOOQ 3.12
 */
@Deprecated
public abstract class AbstractJsonBinding<T> extends AbstractObjectBinding<T> {

    private final String pgType;
    private final JavaType type;

    protected AbstractJsonBinding(String pgType, Class<T> type) {
        this.pgType = pgType;
        this.type = getMapper().getTypeFactory().constructType(type);
    }

    protected AbstractJsonBinding(String pgType, TypeReference<T> type) {
        this.pgType = pgType;
        this.type = getMapper().getTypeFactory().constructType(type);
    }

    protected AbstractJsonBinding(Class<T> type) {
        this("json", type);
    }

    protected AbstractJsonBinding(TypeReference<T> type) {
        this("json", type);
    }

    protected abstract ObjectMapper getMapper();

    protected T getNull() {
        return null;
    }

    protected boolean isNull(T value) {
        return false;
    }

    @Override
    public Converter<Object, T> converter() {
        return new Converter<Object, T>() {
            @Override
            public T from(Object object) {
                try {
                    PGobject obj = (PGobject) object;
                    if (obj == null || obj.getValue().equals("null")) {
                        return getNull();
                    } else {
                        return getMapper().readValue(obj.getValue(), type);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Object to(T value) {
                if (value == null || isNull(value)) return null;
                PGobject obj = new PGobject();
                obj.setType(pgType);
                try {
                    obj.setValue(getMapper().writeValueAsString(value));
                } catch (SQLException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                return obj;
            }

            @Override
            public Class<Object> fromType() {
                return Object.class;
            }

            @Override
            @SuppressWarnings("unchecked")
            public Class<T> toType() {
                return (Class<T>) type.getRawClass();
            }
        };
    }

}
