package org.jboss.seam.wiki.core.nestedset.listener;

import org.hibernate.event.EventSource;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.TimeUnit;

/**
 * An alternative to table locking, we serialize nested set insert/updates in memory.
 *
 * <p>
 * Any nested set tree modification potentially updates all rows in a database table. This
 * requires several <tt>UPDATE</tt> statements, and also <tt>INSERT</tt> and <tt>DELETE</tt>.
 * Any concurrent commit to the rows betwen <tt>UPDATE</tt> statements would be fatal and
 * corrupt the tree information. The usual solution is to lock the whole table(s). Because
 * MySQL has a compleltey unusable locking system (locking a table commits the current transaction, you
 * need to lock all tables you are going to use from that point on, etc.), and because portability is
 * a concern of this Nested Set implementation, we work around the problem with an in-memory exclusive lock.
 * </p>
 * <p>
 * <b>NOTE:</b> This does NOT work if several applications modify the nested set
 * tree in the same tables!
 * </p>
 *
 * @author Christian Bauer
 */
public class NestedSetMonitor {

    private static final int LOCK_TIMEOUT_SECONDS = 10;

    private static final Lock lock = new ReentrantLock(true);

    public static void executeOperation(NestedSetOperation operation, EventSource session) {
        try {
            if (lock.tryLock(LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                try {
                    operation.execute(session);
                } finally {
                    lock.unlock();
                }
            } else {
                throw new RuntimeException("Could not aquire lock to update nested set tree");
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException("Current thread could not aquire lock, has been interrupted");
        }
    }
}
