package net.lightoze.jooq.postgresql.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import org.jooq.JSONB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JacksonJsonbListConverter<T> extends AbstractJacksonConverter<JSONB[], List<T>, T> {

    public JacksonJsonbListConverter() {
    }

    public JacksonJsonbListConverter(JavaType userType, JavaType elementType) {
        super(userType, elementType);
    }

    @Override
    public List<T> from(JSONB[] array) {
        if (array == null) {
            return null;
        } else {
            ArrayList<T> list = new ArrayList<>(array.length);
            for (JSONB json : array) {
                if (json == null || json.data().equals("null")) {
                    list.add(getNull());
                } else {
                    try {
                        list.add(getObjectReader().readValue(json.data()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return list;
        }
    }

    @Override
    public JSONB[] to(List<T> list) {
        if (list == null) {
            return null;
        } else {
            JSONB[] array = new JSONB[list.size()];
            Iterator<T> it = list.iterator();
            for (int i = 0; i < array.length; i++) {
                T value = it.next();
                if (value == null || isNull(value)) {
                    array[i] = null;
                } else {
                    try {
                        array[i] = JSONB.valueOf(getObjectWriter().writeValueAsString(value));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return array;
        }
    }

    @Override
    public Class<JSONB[]> fromType() {
        return JSONB[].class;
    }

}
