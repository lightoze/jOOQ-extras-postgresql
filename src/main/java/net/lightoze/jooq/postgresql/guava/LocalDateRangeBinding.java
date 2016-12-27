package net.lightoze.jooq.postgresql.guava;

import com.google.common.collect.Range;

import java.time.LocalDate;

public class LocalDateRangeBinding extends AbstractRangeBinding<LocalDate> {

    private static final LocalDate EPOCH = LocalDate.parse("1970-01-01");
    private static final Range<LocalDate> EMPTY = Range.openClosed(EPOCH, EPOCH);

    public LocalDateRangeBinding() {
        super("daterange");
    }

    @Override
    protected Range<LocalDate> getEmpty() {
        return EMPTY;
    }

    @Override
    protected LocalDate parse(String text) {
        return LocalDate.parse(text);
    }

    @Override
    protected String format(LocalDate value) {
        return value.toString();
    }
}
