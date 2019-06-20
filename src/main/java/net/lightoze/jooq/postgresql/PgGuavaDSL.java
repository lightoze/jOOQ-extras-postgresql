package net.lightoze.jooq.postgresql;

import com.google.common.collect.Range;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

public interface PgGuavaDSL {

    static <T extends Comparable<? super T>> Condition rangeContains(Field<Range<T>> field, Field<T> element) {
        return DSL.condition("{0} @> {1}", field, element);
    }

    static <T extends Comparable<? super T>> Condition rangeContains(Field<Range<T>> field, T element) {
        return rangeContains(field, DSL.val(element));
    }

    static <T extends Comparable<? super T>> Condition containsRange(Field<Range<T>> field, Field<Range<T>> element) {
        return DSL.condition("{0} @> {1}", field, element);
    }

    static <T extends Comparable<? super T>> Condition containsRange(Field<Range<T>> field, Range<T> element) {
        return containsRange(field, DSL.val(element, field.getDataType()));
    }

    static <T extends Comparable<? super T>> Condition containedByRange(Field<T> field, Field<Range<T>> element) {
        return DSL.condition("{0} <@ {1}", field, element);
    }

    static <T extends Comparable<? super T>> Condition rangeContainedBy(Field<Range<T>> field, Field<Range<T>> element) {
        return DSL.condition("{0} <@ {1}", field, element);
    }

    static <T extends Comparable<? super T>> Condition rangeContainedBy(Field<Range<T>> field, Range<T> element) {
        return rangeContainedBy(field, DSL.val(element, field.getDataType()));
    }

    static <T extends Comparable<? super T>> Condition overlapsRange(Field<Range<T>> field, Field<Range<T>> element) {
        return DSL.condition("{0} && {1}", field, element);
    }

    static <T extends Comparable<? super T>> Condition overlapsRange(Field<Range<T>> field, Range<T> element) {
        return overlapsRange(field, DSL.val(element, field.getDataType()));
    }

}
