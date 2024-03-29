package net.lightoze.jooq.postgresql;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import pl.domzal.junit.docker.rule.DockerRule;
import pl.domzal.junit.docker.rule.RestartPolicy;
import pl.domzal.junit.docker.rule.WaitFor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class AbstractDbTest {

    @ClassRule
    public static DockerRule dbRule = DockerRule.builder()
            .imageName("postgres:11.7")
            .name("jooq-extras-test")
            .restartPolicy(RestartPolicy.always())
            .expose("5432")
            .env("POSTGRES_USER", "jooq-test")
            .env("POSTGRES_PASSWORD", "jooq-test")
            .waitFor(WaitFor.logMessageSequence("database system is ready to accept connections", "database system is ready to accept connections"))
            .waitFor(WaitFor.tcpPort(5432))
            .build();

    protected DSLContext db;

    protected Connection createConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:" + dbRule.getExposedContainerPort("5432") + "/jooq-test";
        return DriverManager.getConnection(url, "jooq-test", "jooq-test");
    }

    protected <T> T fetch(Field<T> field) {
        return db.select(field).fetchOne(field);
    }

    protected boolean fetchCondition(Condition condition) {
        return fetch(DSL.field(condition));
    }

    @Before
    public void initConnection() throws SQLException {
        db = DSL.using(createConnection(), SQLDialect.POSTGRES);
    }

    @After
    public void closeConnection() {
        if (db != null) {
            try {
                db.connection(Connection::close);
            } finally {
                db = null;
            }
        }
    }
}
