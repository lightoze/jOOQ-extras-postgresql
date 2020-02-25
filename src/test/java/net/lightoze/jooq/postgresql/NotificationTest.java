package net.lightoze.jooq.postgresql;

import com.google.common.util.concurrent.Uninterruptibles;
import net.lightoze.jooq.postgresql.notify.NotificationListener;
import net.lightoze.jooq.postgresql.notify.NotificationSender;
import org.apache.commons.lang3.StringUtils;
import org.jooq.impl.DSL;
import org.junit.Assert;
import org.junit.Test;
import org.postgresql.PGNotification;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class NotificationTest extends AbstractDbTest {

    @Test
    public void notification() throws InterruptedException {
        List<String> messages = new ArrayList<>();

        TestNotificationListener listener = new TestNotificationListener(messages);
        listener.getConfiguration().setChannels("a");
        listener.start();
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

        NotificationSender.notify(db, "a");

        try {
            // test notifying in failed transaction
            db.transaction(conf -> {
                NotificationSender.notify(DSL.using(conf), "a", "x");
                throw new RuntimeException();
            });
            Assert.fail();
        } catch (RuntimeException e) {
            // ignore
        }

        for (int i = 1; i < 10; i++) {
            Uninterruptibles.sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
            NotificationSender.notify(db, "a", "i" + i);
            NotificationSender.notify(db, "b", "i" + i);
        }

        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        listener.stop();

        Assert.assertEquals(Arrays.asList("a", "a:i1", "a:i2", "a:i3", "a:i4", "a:i5", "a:i6", "a:i7", "a:i8", "a:i9"), messages);
    }

    private class TestNotificationListener extends NotificationListener {

        private final List<String> messages;
        private AtomicBoolean running = new AtomicBoolean(false);
        private volatile Thread thread;

        public TestNotificationListener(List<String> messages) {
            this.messages = messages;
        }

        @Override
        protected Connection getConnection() throws SQLException {
            return createConnection();
        }

        @Override
        protected void closeConnection(Connection connection) throws SQLException {
            connection.close();
        }

        @Override
        protected void receiveNotification(PGNotification notification) {
            String str = notification.getName();
            if (StringUtils.isNotEmpty(notification.getParameter())) {
                str += ":" + notification.getParameter();
            }
            messages.add(str);
        }

        public void start() {
            running.set(true);
            thread = new Thread(() -> run(running::get));
            thread.setName("testNotificationListener");
            thread.start();
        }

        public void stop() throws InterruptedException {
            running.set(false);
            thread.join();
        }

    }

}
