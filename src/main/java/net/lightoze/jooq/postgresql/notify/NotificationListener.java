package net.lightoze.jooq.postgresql.notify;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class NotificationListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Getter
    private String[] channels = new String[0];
    @Getter
    @Setter
    private long retryDelayMillis = 5000;
    @Getter
    @Setter
    private int fetchTimeoutMillis = 1000;
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private volatile boolean running;

    public void setChannels(String... channels) {
        this.channels = channels;
    }

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
    protected abstract void closeConnection(Connection connection) throws SQLException;

    /**
     * Process incoming notification.
     *
     * @param notification notification
     */
    protected abstract void receiveNotification(PGNotification notification);

    protected void run() {
        while (isRunning()) {
            Connection conn;
            try {
                conn = getConnection();
            } catch (Throwable e) {
                log.error("Could not get a connection", e);
                try {
                    Thread.sleep(retryDelayMillis);
                } catch (InterruptedException ex) {
                    // ignore
                }
                continue;
            }
            try {
                conn.setAutoCommit(true);
                for (String channel : channels) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("LISTEN " + channel);
                    }
                }
                while (isRunning()) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("SELECT 1");
                    }
                    PGNotification[] notifications = conn.unwrap(PGConnection.class).getNotifications(fetchTimeoutMillis);
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
                    Thread.sleep(retryDelayMillis);
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
}
