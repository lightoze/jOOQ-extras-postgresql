package net.lightoze.jooq;

import org.jooq.*;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;

public abstract class AbstractObjectBinding<T> implements Binding<Object, T> {

    @Override
    public void sql(BindingSQLContext<T> ctx) throws SQLException {
        ctx.render().sql("?");
    }

    @Override
    public void register(BindingRegisterContext<T> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), Types.JAVA_OBJECT);
    }

    @Override
    public void set(BindingSetStatementContext<T> ctx) throws SQLException {
        ctx.statement().setObject(ctx.index(), ctx.convert(converter()).value());
    }

    @Override
    public void get(BindingGetResultSetContext<T> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.resultSet().getObject(ctx.index()));
    }

    @Override
    public void get(BindingGetStatementContext<T> ctx) throws SQLException {
        ctx.convert(converter()).value(ctx.statement().getObject(ctx.index()));
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
