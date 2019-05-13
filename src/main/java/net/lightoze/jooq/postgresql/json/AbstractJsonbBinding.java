package net.lightoze.jooq.postgresql.json;

import com.fasterxml.jackson.core.type.TypeReference;

public abstract class AbstractJsonbBinding<T> extends AbstractJsonBinding<T> {

    protected AbstractJsonbBinding(Class<T> type) {
        super("jsonb", type);
    }

    protected AbstractJsonbBinding(TypeReference<T> type) {
        super("jsonb", type);
    }

}
