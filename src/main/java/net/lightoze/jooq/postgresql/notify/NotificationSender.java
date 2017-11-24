package net.lightoze.jooq.postgresql.notify;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

public class NotificationSender {

    public static void notify(DSLContext db, String channel) {
        notify(db, channel, null);
    }

    public static void notify(DSLContext db, String channel, String parameter) {
        if (parameter == null) {
            db.execute("NOTIFY {0}", DSL.name(channel));
        } else {
            db.execute("NOTIFY {0}, {1}", DSL.name(channel), DSL.inline(parameter));
        }
    }
}
