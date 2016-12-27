package net.lightoze.jooq.postgresql.guava;

import com.google.common.base.Preconditions;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import net.lightoze.jooq.AbstractObjectBinding;
import org.jooq.Converter;
import org.postgresql.util.PGobject;

import java.sql.SQLException;

public abstract class AbstractRangeBinding<T extends Comparable> extends AbstractObjectBinding<Range<T>> {

    private final String type;

    protected AbstractRangeBinding(String type) {
        this.type = type;
    }

    protected abstract Range<T> getEmpty();

    protected abstract T parse(String text);

    protected abstract String format(T value);

    @Override
    public Converter<Object, Range<T>> converter() {
        return new Converter<Object, Range<T>>() {
            @Override
            public Range<T> from(Object object) {
                if (object == null) {
                    return null;
                }
                PGobject o = (PGobject) object;
                Preconditions.checkArgument(type.equals(o.getType()));
                String str = o.getValue();
                if (str.equals("empty")) {
                    return getEmpty();
                }
                BoundType lowerBound = str.charAt(0) == '[' ? BoundType.CLOSED : BoundType.OPEN;
                BoundType upperBound = str.charAt(str.length() - 1) == ']' ? BoundType.CLOSED : BoundType.OPEN;
                int separator = str.indexOf(',');
                T lower = parseImpl(str.substring(1, separator));
                T upper = parseImpl(str.substring(separator + 1, str.length() - 1));
                if (lower == null) {
                    if (upper == null) {
                        return Range.all();
                    } else {
                        return Range.upTo(upper, upperBound);
                    }
                } else if (upper == null) {
                    return Range.downTo(lower, lowerBound);
                } else {
                    return Range.range(lower, lowerBound, upper, upperBound);
                }
            }

            private T parseImpl(String text) {
                return text.isEmpty() ? null : parse(text);
            }

            @Override
            public Object to(Range<T> range) {
                if (range == null) {
                    return null;
                }
                String str;
                if (range.isEmpty()) {
                    str = "empty";
                } else {
                    str = range.hasLowerBound() && range.lowerBoundType() == BoundType.CLOSED ? "[" : "(";
                    if (range.hasLowerBound()) {
                        str += format(range.lowerEndpoint());
                    }
                    str += ',';
                    if (range.hasUpperBound()) {
                        str += format(range.upperEndpoint());
                    }
                    str += range.hasUpperBound() && range.upperBoundType() == BoundType.CLOSED ? ']' : ')';
                }
                PGobject object = new PGobject();
                object.setType(type);
                try {
                    object.setValue(str);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return object;
            }

            @Override
            public Class<Object> fromType() {
                return Object.class;
            }

            @Override
            public Class<Range<T>> toType() {
                //noinspection unchecked
                return (Class) Range.class;
            }
        };
    }

}
