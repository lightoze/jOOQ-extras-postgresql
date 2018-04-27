package net.lightoze.jooq;

import org.jooq.Converter;
import org.jooq.DataType;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public abstract class PrimitiveListBinding<T> extends AbstractArrayBinding<T, List<T>> {

    private final DataType<T> type;
    private final T[] emptyArray;

    @SuppressWarnings("unchecked")
    public PrimitiveListBinding(DataType<T> type) {
        this.type = type;
        emptyArray = (T[]) Array.newInstance(type.getType(), 0);
    }

    @Override
    protected String typeName() {
        return type.getTypeName();
    }

    @Override
    public Converter<T[], List<T>> converter() {
        return new Converter<T[], List<T>>() {
            @Override
            public List<T> from(T[] arr) {
                if (arr == null) {
                    return null;
                } else {
                    return Arrays.asList(arr);
                }
            }

            @Override
            public T[] to(List<T> list) {
                return list.toArray(emptyArray);
            }

            @Override
            public Class<T[]> fromType() {
                return type.getArrayType();
            }

            @Override
            @SuppressWarnings("unchecked")
            public Class<List<T>> toType() {
                return (Class<List<T>>) ((Class<?>) List.class);
            }
        };
    }
}
