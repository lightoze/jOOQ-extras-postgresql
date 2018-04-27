package net.lightoze.jooq.postgresql;

import org.jooq.Field;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;

import java.time.Duration;
import java.time.Instant;

public class PrimitivesTest extends AbstractDbTest {

    @Test
    public void instant() {
        Instant now = Instant.now().minusSeconds(1);
        Field<Instant> field = DSL.currentInstant();
        Assert.assertTrue(db.select(field).fetchOne(field).isAfter(now));
    }

    @Test
    public void duration() {
        Field<Duration> field = DSL.field("interval 'P1DT44H'", DSL.DURATION);
        Assert.assertEquals(Duration.ofDays(1).plusHours(44), db.select(field).fetchOne(field));
    }

    @Test
    public void coordinate(){
        Field<Coordinate> field = DSL.field("point(12,34)", CoordinateBinding.TYPE);
        Assert.assertEquals(new Coordinate(12, 34), db.select(field).fetchOne(field));
    }
}
