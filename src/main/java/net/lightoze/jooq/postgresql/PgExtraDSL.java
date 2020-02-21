package net.lightoze.jooq.postgresql;

import org.jooq.Condition;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDataType;
import org.postgresql.util.PGInterval;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;

public interface PgExtraDSL {

    DataType<Duration> DURATION = new DefaultDataType<>(SQLDialect.POSTGRES, PGInterval.class, "interval day to second")
            .asConvertedDataType(new DurationBinding());


    static <T> Condition contains(Field<? extends Collection<T>> field, Field<? extends Collection<T>> inner) {
        return DSL.condition("{0} @> {1}", field, inner);
    }

    static <T> Condition contains(Field<? extends Collection<T>> field, Collection<T> values) {
        return contains(field, DSL.val(values, field.getDataType()));
    }

    @SafeVarargs
    static <T> Condition contains(Field<? extends Collection<T>> field, T... values) {
        return contains(field, Arrays.asList(values));
    }

    static <T> Condition overlaps(Field<? extends Collection<T>> field, Field<? extends Collection<T>> other) {
        return DSL.condition("{0} && {1}", field, other);
    }

    static <T> Condition overlaps(Field<? extends Collection<T>> field, Collection<T> values) {
        return overlaps(field, DSL.val(values, field.getDataType()));
    }

    @SafeVarargs
    static <T> Condition overlaps(Field<? extends Collection<T>> field, T... values) {
        return overlaps(field, Arrays.asList(values));
    }


    static Field<String> jsonText(Field<?> base, String... path) {
        if (path.length == 1) {
            return DSL.field("({0} ->> {1})", String.class, base, DSL.inline(path[0]));
        } else {
            return DSL.field("({0} #>> {1})", String.class, base, DSL.inline(path));
        }
    }

    static Condition jsonContains(Field<?> outer, Field<?> inner) {
        return DSL.condition("{0} @> {1}", outer, inner);
    }

    static Condition jsonContains(Field<?> outer, Object inner) {
        return jsonContains(outer, DSL.val(inner, outer.getDataType()));
    }

    static Condition jsonContainsKey(Field<?> field, String key) {
        return DSL.condition("{0} {??} {1}", field, DSL.inline(key));
    }

    static Condition jsonContainsAllKeys(Field<?> field, String... keys) {
        return DSL.condition("{0} {??&} {1}", field, DSL.inline(keys));
    }

    static Condition jsonContainsAnyKey(Field<?> field, String... keys) {
        return DSL.condition("{0} {??|} {1}", field, DSL.inline(keys));
    }

}
