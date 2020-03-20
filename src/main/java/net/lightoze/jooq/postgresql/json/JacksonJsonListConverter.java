package net.lightoze.jooq.postgresql.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import org.jooq.JSON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JacksonJsonListConverter<T> extends AbstractJacksonConverter<JSON[], List<T>, T> {

    public JacksonJsonListConverter() {
    }

    public JacksonJsonListConverter(JavaType userType, JavaType elementType) {
        super(userType, elementType);
    }

    @Override
    public List<T> from(JSON[] array) {
        if (array == null) {
            return null;
        } else {
            ArrayList<T> list = new ArrayList<>(array.length);
            for (JSON json : array) {
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
    public JSON[] to(List<T> list) {
        if (list == null) {
            return null;
        } else {
            JSON[] array = new JSON[list.size()];
            Iterator<T> it = list.iterator();
            for (int i = 0; i < array.length; i++) {
                T value = it.next();
                if (value == null || isNull(value)) {
                    array[i] = null;
                } else {
                    try {
                        array[i] = JSON.valueOf(getObjectWriter().writeValueAsString(value));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return array;
        }
    }

    @Override
    public Class<JSON[]> fromType() {
        return JSON[].class;
    }

}
