package net.lightoze.jooq.postgresql.guava;

import com.google.common.collect.Range;
import net.lightoze.jooq.postgresql.AbstractDbTest;
import net.lightoze.jooq.postgresql.PgExtraDSL;
import net.lightoze.jooq.postgresql.PgGuavaDSL;
import org.jooq.Condition;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDataType;
import org.jooq.impl.SQLDataType;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;

public class GuavaRangeTest extends AbstractDbTest {

    private <T extends Comparable<? super T>> void runSuite(
            DataType<Range<T>> type, DataType<T> elementType,
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

        Assert.assertTrue(fetchCondition(PgGuavaDSL.rangeContains(field, lower)));
        Assert.assertFalse(fetchCondition(PgGuavaDSL.rangeContains(field, upper)));
        Assert.assertTrue(fetchCondition(PgGuavaDSL.containsRange(field, range)));
        Assert.assertTrue(fetchCondition(PgGuavaDSL.containedByRange(DSL.val(lower), field)));
        Assert.assertTrue(fetchCondition(PgGuavaDSL.rangeContainedBy(field, field)));
        Assert.assertTrue(fetchCondition(PgGuavaDSL.overlapsRange(field, range)));
    }

    @Test
    public void localDateRange() {
        runSuite(
                new DefaultDataType<>(SQLDialect.POSTGRES, SQLDataType.OTHER, "daterange").asConvertedDataType(new LocalDateRangeBinding()),
                SQLDataType.LOCALDATE,
                LocalDate::toString,
                v -> v.plusDays(1), v -> v.minusDays(1),
                LocalDate.parse("2010-01-01"),
                LocalDate.parse("2010-02-15")
        );
    }

    @Test
    public void instantRange() {
        runSuite(
                new DefaultDataType<>(SQLDialect.POSTGRES, SQLDataType.OTHER, "tstzrange").asConvertedDataType(new InstantRangeBinding()),
                SQLDataType.INSTANT,
                Instant::toString,
                v -> v.plusSeconds(1), v -> v.minusSeconds(1),
                Instant.parse("2010-01-01T23:30:00Z"),
                Instant.parse("2010-02-15T00:00:00Z")
        );
    }

    @Test
    public void localDateTimeRange() {
        runSuite(
                new DefaultDataType<>(SQLDialect.POSTGRES, SQLDataType.OTHER, "tsrange").asConvertedDataType(new LocalDateTimeRangeBinding()),
                SQLDataType.LOCALDATETIME,
                LocalDateTime::toString,
                v -> v.plusSeconds(1), v -> v.minusSeconds(1),
                LocalDateTime.parse("2010-01-01T23:30:00"),
                LocalDateTime.parse("2010-02-15T00:00:00")
        );
    }

    @Test
    public void integerRange() {
        this.runSuite(
                new DefaultDataType<>(SQLDialect.POSTGRES, SQLDataType.OTHER, "int4range").asConvertedDataType(new IntegerRangeBinding()),
                SQLDataType.INTEGER,
                Object::toString,
                v -> v + 1, v -> v - 1,
                10, 20
        );
    }

    @Test
    public void longRange() {
        this.runSuite(
                new DefaultDataType<>(SQLDialect.POSTGRES, SQLDataType.OTHER, "int8range").asConvertedDataType(new LongRangeBinding()),
                SQLDataType.BIGINT,
                Object::toString,
                v -> v + 1, v -> v - 1,
                10L, 20L
        );
    }

    @Test
    public void bigDecimalRange() {
        this.runSuite(
                new DefaultDataType<>(SQLDialect.POSTGRES, SQLDataType.OTHER, "numrange").asConvertedDataType(new BigDecimalRangeBinding()),
                SQLDataType.NUMERIC,
                Object::toString,
                v -> v.add(BigDecimal.valueOf(0.01)), v -> v.subtract(BigDecimal.valueOf(0.01)),
                BigDecimal.valueOf(10.23), BigDecimal.valueOf(100)
        );
    }
}
