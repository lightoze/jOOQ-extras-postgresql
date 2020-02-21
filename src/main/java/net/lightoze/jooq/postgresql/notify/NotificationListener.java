package net.lightoze.jooq.postgresql.notify;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class NotificationListener extends AbstractExecutionThreadService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private String[] channels = new String[0];
    private long retryDelayMillis = 5000;
    private long fetchIntervalMillis = 500;

    public String[] getChannels() {
        return channels;
    }

    public void setChannels(String[] channels) {
        this.channels = channels;
    }

    public long getRetryDelayMillis() {
        return retryDelayMillis;
    }

    public void setRetryDelayMillis(long retryDelayMillis) {
        this.retryDelayMillis = retryDelayMillis;
    }

    public long getFetchIntervalMillis() {
        return fetchIntervalMillis;
    }

    public void setFetchIntervalMillis(long fetchIntervalMillis) {
        this.fetchIntervalMillis = fetchIntervalMillis;
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

    @Override
    protected void run() throws Exception {
        while (isRunning()) {
            Connection conn;
            try {
                conn = getConnection();
                conn.setAutoCommit(true);
            } catch (Throwable e) {
                log.error("Could not get a connection", e);
                Thread.sleep(retryDelayMillis);
                continue;
            }
            try {
                for (String channel : channels) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("LISTEN " + channel);
                    }
                }
                while (isRunning()) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("SELECT 1");
                    }

                    PGNotification[] notifications = conn.unwrap(PGConnection.class).getNotifications();
                    if (notifications == null || notifications.length == 0) {
                        Thread.sleep(fetchIntervalMillis);
                    } else {
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
                Thread.sleep(retryDelayMillis);
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
