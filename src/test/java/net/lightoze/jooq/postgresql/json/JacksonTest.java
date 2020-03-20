package net.lightoze.jooq.postgresql.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.lightoze.jooq.postgresql.AbstractDbTest;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class JacksonTest extends AbstractDbTest {

    @Test
    public void json() {
        DataType<Obj> type = SQLDataType.JSON.asConvertedDataType(new JacksonJsonConverter<Obj>() {});
        Field<Obj> field = DSL.field("'{\"x\": \"xx\", \"y\": 10}'::json", type);
        Assert.assertEquals(new Obj("xx", 10), fetch(field));
    }

    @Test
    public void jsonb() {
        DataType<Obj> type = SQLDataType.JSONB.asConvertedDataType(new JacksonJsonbConverter<Obj>() {});
        Field<Obj> field = DSL.field("'{\"x\": \"xx\", \"y\": 10}'::jsonb", type);
        Assert.assertEquals(new Obj("xx", 10), fetch(field));
    }

    @Test
    public void jsonList() {
        DataType<List<Obj>> type = SQLDataType.JSON.getArrayDataType().asConvertedDataType(new JacksonJsonListConverter<Obj>() {});
        Field<List<Obj>> field = DSL.field("'{\"{\\\"x\\\": \\\"xx\\\", \\\"y\\\": 10}\", \"{\\\"x\\\": \\\"xxx\\\", \\\"y\\\": 12}\"}'::json[]", type);
        Assert.assertEquals(Arrays.asList(new Obj("xx", 10), new Obj("xxx", 12)), fetch(field));
    }

    @Test
    public void jsonbList() {
        DataType<List<Obj>> type = SQLDataType.JSONB.getArrayDataType().asConvertedDataType(new JacksonJsonbListConverter<Obj>() {});
        Field<List<Obj>> field = DSL.field("'{\"{\\\"x\\\": \\\"xx\\\", \\\"y\\\": 10}\", \"{\\\"x\\\": \\\"xxx\\\", \\\"y\\\": 12}\"}'::jsonb[]", type);
        Assert.assertEquals(Arrays.asList(new Obj("xx", 10), new Obj("xxx", 12)), fetch(field));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Obj {

        private String x;
        private int y;

    }

}
