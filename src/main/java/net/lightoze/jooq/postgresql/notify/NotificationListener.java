package net.lightoze.jooq.postgresql.notify;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.BooleanSupplier;

public abstract class NotificationListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Getter
    @Setter
    private Configuration configuration = new Configuration();

    /**
     * Obtain a new database connection. The connection should not have a timeout
     * because it will be kept open as long as new notifications are listened for.
     *
     * @return database connection
     * @throws SQLException when database connection failed
     */
    protected abstract Connection getConnection() throws SQLException;

    /**
     * Close database connection and make sure it is not reused by anyone else
     * (e.g. by connection pool) to prevent leaking notification subscriptions.
     *
     * @param connection database connection
     * @throws SQLException when error occurred
     */
    protected void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }

    /**
     * Process incoming notification.
     *
     * @param notification notification
     */
    protected abstract void receiveNotification(PGNotification notification);

    public void run(BooleanSupplier runningSupplier) {
        while (runningSupplier.getAsBoolean()) {
            Connection conn;
            try {
                conn = getConnection();
            } catch (Throwable e) {
                log.error("Could not get a connection", e);
                try {
                    Thread.sleep(configuration.getRetryDelayMillis());
                } catch (InterruptedException ex) {
                    // ignore
                }
                continue;
            }
            try {
                conn.setAutoCommit(true);
                for (String channel : configuration.getChannels()) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("LISTEN " + channel);
                    }
                }
                while (runningSupplier.getAsBoolean()) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.setQueryTimeout(configuration.getFetchTimeoutMillis());
                        stmt.execute("SELECT 1");
                    }
                    PGNotification[] notifications = conn.unwrap(PGConnection.class).getNotifications(configuration.getFetchTimeoutMillis());
                    if (notifications != null) {
                        for (PGNotification notification : notifications) {
                            try {
                                receiveNotification(notification);
                            } catch (Throwable e) {
                                log.error("Error processing notification {} ({}) from {}",
                                        notification.getName(), notification.getParameter(), notification.getPID(), e);
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                log.error("Error while listening for notifications", e);
                try {
                    Thread.sleep(configuration.getRetryDelayMillis());
                } catch (InterruptedException ex) {
                    // ignore
                }
            } finally {
                try {
                    closeConnection(conn);
                } catch (Throwable e) {
                    log.warn("Error closing connection", e);
                }
            }
        }
    }

    @Data
    public static class Configuration {

        private String[] channels = new String[0];
        private long retryDelayMillis = 10000;
        private int validationTimeoutMillis = 3000;
        private int fetchTimeoutMillis = 1000;

        public void setChannels(String... channels) {
            this.channels = channels;
        }

    }

}
