package net.lightoze.jooq.postgresql.guava;

import com.google.common.collect.Range;

import java.time.Instant;

public class InstantRangeBinding extends AbstractRangeBinding<Instant> {

    private static final Instant ZERO = Instant.EPOCH;

    protected InstantRangeBinding(String type) {
        super("tstzrange");
    }

    @Override
    Range<Instant> getEmpty() {
        return Range.openClosed(ZERO, ZERO);
    }

    @Override
    Instant parse(String text) {
        return Instant.parse(text);
    }

    @Override
    String format(Instant value) {
        return value.toString();
    }
}
