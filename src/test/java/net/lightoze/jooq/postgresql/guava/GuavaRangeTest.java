package net.lightoze.jooq.postgresql.guava;

import com.google.common.collect.Range;
import net.lightoze.jooq.postgresql.AbstractDbTest;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDataType;
import org.jooq.impl.SQLDataType;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.function.Function;

public class GuavaRangeTest extends AbstractDbTest {

    private <T extends Comparable> void runSuite(
            DataType<Range<T>> type,
            Function<T, String> formatter,
            Function<T, T> increment, Function<T, T> decrement,
            T lower, T upper
    ) {
        Field<Range<T>> field = DSL.field("'[" + formatter.apply(lower) + ", " + formatter.apply(upper) + ")'::" + type.getTypeName(), type);
        Range<T> range = db.select(field).fetchOne(field);

        Assert.assertTrue(range.contains(lower));
        Assert.assertFalse(range.contains(decrement.apply(lower)));
        Assert.assertTrue(range.contains(decrement.apply(upper)));
        Assert.assertFalse(range.contains(upper));

        Field<Range<T>> intersect = DSL.field("? * ?", type, field, DSL.val(Range.atMost(lower), type));
        range = db.select(intersect).fetchOne(intersect);
        Assert.assertTrue(range.contains(lower));
        Assert.assertFalse(range.contains(increment.apply(lower)));
        Assert.assertFalse(range.contains(decrement.apply(lower)));
    }

    @Test
    public void localDate() {
        runSuite(
                new DefaultDataType<>(SQLDialect.POSTGRES, SQLDataType.OTHER, "daterange").asConvertedDataType(new LocalDateRangeBinding()),
                LocalDate::toString,
                v -> v.plusDays(1), v -> v.minusDays(1),
                LocalDate.parse("2010-01-01"),
                LocalDate.parse("2010-02-15")
        );
    }

    @Test
    public void instant() {
        runSuite(
                new DefaultDataType<>(SQLDialect.POSTGRES, SQLDataType.OTHER, "tstzrange").asConvertedDataType(new InstantRangeBinding()),
                Instant::toString,
                v -> v.plusSeconds(1), v -> v.minusSeconds(1),
                Instant.parse("2010-01-01T23:30:00Z"),
                Instant.parse("2010-02-15T00:00:00Z")
        );
    }
}
