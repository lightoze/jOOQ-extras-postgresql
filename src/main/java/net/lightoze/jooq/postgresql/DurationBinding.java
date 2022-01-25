package net.lightoze.jooq.postgresql;

import net.lightoze.jooq.AbstractObjectBinding;
import org.jooq.Converter;
import org.postgresql.util.PGInterval;

import java.time.Duration;

public class DurationBinding extends AbstractObjectBinding<Duration> {

    @Override
    public Converter<Object, Duration> converter() {
        return new Converter<>() {
            @Override
            public Duration from(Object object) {
                if (object == null) {
                    return null;
                }
                PGInterval interval = (PGInterval) object;
                if (interval.getYears() != 0) {
                    throw new IllegalArgumentException("Duration cannot handle years");
                }
                if (interval.getMonths() != 0) {
                    throw new IllegalArgumentException("Duration cannot handle months");
                }
                long floor = (long) interval.getSeconds();
                return Duration.ofSeconds(
                        ((interval.getDays() * 24 + interval.getHours()) * 60 + interval.getMinutes()) * 60 + floor,
                        (long) ((interval.getSeconds() - floor) * 1E9)
                );
            }

            @Override
            public PGInterval to(Duration duration) {
                if (duration == null) {
                    return null;
                }
                long seconds = duration.getSeconds();
                int hours = Math.toIntExact(seconds / 3600);
                int minutes = (int) ((seconds / 60) % 60);
                seconds %= 60;
                return new PGInterval(0, 0, 0, hours, minutes, 1E-9 * duration.getNano() + seconds);
            }

            @Override
            public Class<Object> fromType() {
                return Object.class;
            }

            @Override
            public Class<Duration> toType() {
                return Duration.class;
            }
        };
    }
}
