package net.lightoze.jooq.postgresql.guava;

import com.google.common.collect.Range;

public class LongRangeBinding extends AbstractRangeBinding<Long> {

    private static final Range<Long> EMPTY = Range.openClosed(0L, 0L);

    public LongRangeBinding() {
        super("int8range");
    }

    @Override
    protected Range<Long> getEmpty() {
        return EMPTY;
    }

    @Override
    protected Long parse(String text) {
        return Long.parseLong(text);
    }

    @Override
    protected String format(Long value) {
        return value.toString();
    }
}
