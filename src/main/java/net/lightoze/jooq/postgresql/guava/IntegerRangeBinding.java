package net.lightoze.jooq.postgresql.guava;

import com.google.common.collect.Range;

public class IntegerRangeBinding extends AbstractRangeBinding<Integer> {

    private static final Range<Integer> EMPTY = Range.openClosed(0, 0);

    public IntegerRangeBinding() {
        super("int4range");
    }

    @Override
    protected Range<Integer> getEmpty() {
        return EMPTY;
    }

    @Override
    protected Integer parse(String text) {
        return Integer.parseInt(text);
    }

    @Override
    protected String format(Integer value) {
        return value.toString();
    }
}
