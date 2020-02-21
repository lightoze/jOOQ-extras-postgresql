package net.lightoze.jooq.postgresql.json;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.AccessLevel;
import lombok.Getter;
import org.jooq.Converter;

public abstract class AbstractJacksonConverter<O, T> implements Converter<O, T> {

    @Getter(value = AccessLevel.PROTECTED, lazy = true)
    private final ObjectWriter objectWriter = createObjectWriter();
    @Getter(value = AccessLevel.PROTECTED, lazy = true)
    private final ObjectReader objectReader = createObjectReader();
    private final JavaType type;

    public AbstractJacksonConverter() {
        this.type = resolveType();
    }

    public AbstractJacksonConverter(JavaType type) {
        this.type = type;
    }

    protected ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper;
    }

    protected T getNull() {
        return null;
    }

    protected boolean isNull(T value) {
        return false;
    }

    protected ObjectWriter createObjectWriter() {
        return getMapper().writerFor(type);
    }

    protected ObjectReader createObjectReader() {
        return getMapper().readerFor(type);
    }

    @Override
    public Class<T> toType() {
        //noinspection unchecked
        return (Class<T>) type.getRawClass();
    }

    private JavaType resolveType() {
        JavaType selfType = getMapper().constructType(getClass());
        JavaType[] typeParameters = selfType.findTypeParameters(AbstractJacksonConverter.class);
        if (typeParameters.length == 2) {
            return typeParameters[1];
        } else {
            throw new UnsupportedOperationException("Could not resolve value type for converter " + getClass().getName());
        }
    }

}
