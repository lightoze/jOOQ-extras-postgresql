package net.lightoze.jooq;

import org.jooq.SQLDialect;
import org.jooq.impl.DefaultDataType;
import org.jooq.impl.SQLDataType;

public class TextListBinding extends PrimitiveListBinding<String> {

    public TextListBinding() {
        super(new DefaultDataType<>(SQLDialect.POSTGRES, SQLDataType.CLOB, "text"));
    }

}
