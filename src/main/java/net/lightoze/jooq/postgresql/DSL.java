package net.lightoze.jooq.postgresql;

import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultDataType;
import org.jooq.impl.SQLDataType;
import org.jooq.util.postgres.PostgresDSL;
import org.postgresql.util.PGInterval;

import java.time.Duration;
import java.time.Instant;

public class DSL extends PostgresDSL {

    public static final DataType<Instant> INSTANT = SQLDataType.OFFSETDATETIME.asConvertedDataType(new InstantConverter());
    public static final DataType<Duration> DURATION = new DefaultDataType<>(SQLDialect.POSTGRES, PGInterval.class, "interval day to second")
            .asConvertedDataType(new DurationBinding());

    public static Field<Instant> currentInstant() {
        return currentTimestamp().cast(INSTANT);
    }
}
