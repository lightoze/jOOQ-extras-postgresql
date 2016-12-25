package net.lightoze.jooq.postgresql.guava;

import com.google.common.collect.Range;

import java.time.LocalDate;

public class LocalDateRangeBinding extends AbstractRangeBinding<LocalDate> {

    private static final LocalDate ZERO = LocalDate.of(0, 1, 1);

    protected LocalDateRangeBinding(String type) {
        super("daterange");
    }

    @Override
    Range<LocalDate> getEmpty() {
        return Range.openClosed(ZERO, ZERO);
    }

    @Override
    LocalDate parse(String text) {
        return LocalDate.parse(text);
    }

    @Override
    String format(LocalDate value) {
        return value.toString();
    }
}
