package net.lightoze.jooq.postgresql.guava;

import com.google.common.collect.Range;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class InstantRangeBinding extends AbstractRangeBinding<Instant> {

    private static final Range<Instant> EMPTY = Range.openClosed(Instant.EPOCH, Instant.EPOCH);
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendLiteral('"')
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(DateTimeFormatter.ISO_LOCAL_TIME)
            .appendOffset("+HH:mm", "Z")
            .appendLiteral('"')
            .parseStrict()
            .toFormatter();

    public InstantRangeBinding() {
        super("tstzrange");
    }

    @Override
    protected Range<Instant> getEmpty() {
        return EMPTY;
    }

    @Override
    protected Instant parse(String text) {
        return OffsetDateTime.parse(text, FORMATTER).toInstant();
    }

    @Override
    protected String format(Instant value) {
        return FORMATTER.format(value.atZone(ZoneId.systemDefault()));
    }
}
