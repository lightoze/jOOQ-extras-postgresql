package net.lightoze.jooq.postgresql;

import net.lightoze.jooq.AbstractObjectBinding;
import org.jooq.Converter;
import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultDataType;
import org.locationtech.jts.geom.Coordinate;
import org.postgresql.geometric.PGpoint;

public class CoordinateBinding extends AbstractObjectBinding<Coordinate> {

    public static final DataType<Coordinate> TYPE = new DefaultDataType<>(SQLDialect.POSTGRES, PGpoint.class, "point")
            .asConvertedDataType(new CoordinateBinding());

    @Override
    public Converter<Object, Coordinate> converter() {
        return new Converter<>() {
            @Override
            public Coordinate from(Object object) {
                if (object == null) {
                    return null;
                }
                PGpoint point = (PGpoint) object;
                return new Coordinate(point.x, point.y);
            }

            @Override
            public Object to(Coordinate coordinate) {
                if (coordinate == null) {
                    return null;
                }
                if (coordinate.z != Coordinate.NULL_ORDINATE) {
                    throw new IllegalArgumentException("Z-ordinate is not supported by point type");
                }
                return new PGpoint(coordinate.x, coordinate.y);
            }

            @Override
            public Class<Object> fromType() {
                return Object.class;
            }

            @Override
            public Class<Coordinate> toType() {
                return Coordinate.class;
            }
        };
    }
}
