package net.lightoze.jooq.postgresql.guava;

import com.google.common.collect.Range;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class LocalDateTimeRangeBinding extends AbstractRangeBinding<LocalDateTime> {

    private static final LocalDateTime EPOCH = LocalDateTime.parse("1970-01-01T00:00:00");
    private static final Range<LocalDateTime> EMPTY = Range.openClosed(EPOCH, EPOCH);
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendLiteral('"')
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(DateTimeFormatter.ISO_LOCAL_TIME)
            .appendLiteral('"')
            .parseStrict()
            .toFormatter();

    public LocalDateTimeRangeBinding() {
        super("tsrange");
    }

    @Override
    protected Range<LocalDateTime> getEmpty() {
        return EMPTY;
    }

    @Override
    protected LocalDateTime parse(String text) {
        return LocalDateTime.parse(text, FORMATTER);
    }

    @Override
    protected String format(LocalDateTime value) {
        return FORMATTER.format(value);
    }
}
