package net.lightoze.jooq.postgresql.guava;

import com.google.common.collect.Range;

import java.math.BigDecimal;

public class BigDecimalRangeBinding extends AbstractRangeBinding<BigDecimal> {

    private static final Range<BigDecimal> EMPTY = Range.openClosed(BigDecimal.ZERO, BigDecimal.ZERO);

    public BigDecimalRangeBinding() {
        super("numrange");
    }

    @Override
    protected Range<BigDecimal> getEmpty() {
        return EMPTY;
    }

    @Override
    protected BigDecimal parse(String text) {
        return new BigDecimal(text);
    }

    @Override
    protected String format(BigDecimal value) {
        return value.toString();
    }
}
