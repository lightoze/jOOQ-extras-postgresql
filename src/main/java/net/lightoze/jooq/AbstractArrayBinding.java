package net.lightoze.jooq;

import org.jooq.*;
import org.jooq.impl.AbstractBinding;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public abstract class AbstractArrayBinding<O, T> extends AbstractBinding<O[], T> {

    protected abstract String typeName();

    @Override
    public void sql(BindingSQLContext<T> ctx) throws SQLException {
        ctx.render().sql(ctx.variable());
    }

    @Override
    public void register(BindingRegisterContext<T> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), Types.ARRAY);
    }

    @Override
    public void set(BindingSetStatementContext<T> ctx) throws SQLException {
        PreparedStatement st = ctx.statement();
        st.setArray(ctx.index(), st.getConnection().createArrayOf(typeName(), ctx.convert(converter()).value()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void get(BindingGetResultSetContext<T> ctx) throws SQLException {
        Array arr = ctx.resultSet().getArray(ctx.index());
        ctx.convert(converter()).value(arr == null ? null : (O[]) arr.getArray());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void get(BindingGetStatementContext<T> ctx) throws SQLException {
        Array arr = ctx.statement().getArray(ctx.index());
        ctx.convert(converter()).value(arr == null ? null : (O[]) arr.getArray());
    }
}
