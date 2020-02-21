package net.lightoze.jooq.postgresql;

import org.jooq.Converter;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * @deprecated Available from jOOQ 3.12
 */
@Deprecated
public class InstantConverter implements Converter<OffsetDateTime, Instant> {
    @Override
    public Instant from(OffsetDateTime dateTime) {
        return dateTime == null ? null : dateTime.toInstant();
    }

    @Override
    public OffsetDateTime to(Instant instant) {
        return instant == null ? null : OffsetDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    @Override
    public Class<OffsetDateTime> fromType() {
        return OffsetDateTime.class;
    }

    @Override
    public Class<Instant> toType() {
        return Instant.class;
    }
}
