package net.lightoze.jooq;

import org.jooq.*;

import java.sql.*;

public abstract class AbstractArrayBinding<O, T> implements Binding<O[], T> {

    protected abstract String typeName();

    @Override
    public void sql(BindingSQLContext<T> ctx) throws SQLException {
        ctx.render().sql("?");
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

    @Override
    public void set(BindingSetSQLOutputContext<T> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void get(BindingGetSQLInputContext<T> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
