package net.lightoze.jooq.postgresql.json;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.AccessLevel;
import lombok.Getter;
import org.jooq.Converter;

/**
 * @param <O> The database type
 * @param <T> The user type
 * @param <E> The element type for collections, otherwise the same as {@code <T>}
 */
public abstract class AbstractJacksonConverter<O, T, E> implements Converter<O, T> {

    @Getter(value = AccessLevel.PROTECTED, lazy = true)
    private final ObjectWriter objectWriter = createObjectWriter();
    @Getter(value = AccessLevel.PROTECTED, lazy = true)
    private final ObjectReader objectReader = createObjectReader();
    private final JavaType userType;
    private final JavaType elementType;

    public AbstractJacksonConverter() {
        this.userType = resolveType(1);
        this.elementType = resolveType(2);
    }

    public AbstractJacksonConverter(JavaType userType, JavaType elementType) {
        this.userType = userType;
        this.elementType = elementType;
    }

    protected ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper;
    }

    protected E getNull() {
        return null;
    }

    protected boolean isNull(E value) {
        return false;
    }

    protected ObjectWriter createObjectWriter() {
        return getMapper().writerFor(elementType);
    }

    protected ObjectReader createObjectReader() {
        return getMapper().readerFor(elementType);
    }

    @Override
    public Class<T> toType() {
        //noinspection unchecked
        return (Class<T>) userType.getRawClass();
    }

    private JavaType resolveType(int index) {
        JavaType selfType = getMapper().constructType(getClass());
        JavaType[] typeParameters = selfType.findTypeParameters(AbstractJacksonConverter.class);
        if (typeParameters.length == 3) {
            return typeParameters[index];
        } else {
            throw new UnsupportedOperationException("Could not resolve value type for converter " + getClass().getName());
        }
    }

}
