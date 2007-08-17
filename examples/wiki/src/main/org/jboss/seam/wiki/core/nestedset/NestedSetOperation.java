/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.nestedset;

import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.event.EventSource;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.util.LazyIterator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * The contract of a nested set operation sequence as executed in a Hibernate event listener.
 * <p>
 * Guarantees that first the database tree nodes are updated, then the in-memory nodes
 * currently managed by the persistence context.
 * </p>
 * To access the database, an operation uses a <tt>StatelessSession</tt> of Hibernate, and
 * it obtains a JDBC connection using the Hibernate connection provider. If run in an
 * application server with JTS/JTA, the <tt>getConnection()</tt> method
 * returns the same connection handle that is already used inside the current transaction.
 * This means we run on the same connection and transaction as the rest of the Hibernate flush event
 * that executes the <tt>NestedSetOperation</tt>. However, if you run this outside of a managed
 * environment, a new JDBC connection might be obtained from the JDBC connection pool.
 * In that case, you should enable auto-commit mode in your Hibernate configuration. Or,
 * if you want the database tree updates to be atomic and isolated (a good idea), you can
 * override the <tt>beforeExecution()</tt> and <tt>afterExecution()</tt> methods and begin
 * and commit a database transaction manually. Note that this still would be outside the
 * initial connection and transaction, and therefore not be atomic with the overall tree
 * manipulation. This can be improved as soon as Hibernate implements a new contract
 * for the deprecated <tt>Session#connection()</tt> method.
 * </p>
 *
 * TODO: We should lock the rows we are about to update before the updates run!
 *
 * @author Christian Bauer
 */
public class NestedSetOperation {

    protected NestedSetNode node;
    protected String nodeEntityName;

    public NestedSetOperation(NestedSetNode node) {
        this.node = node;
        this.nodeEntityName = node.getTreeSuperclassEntityName();
    }

    // The main sequence of the operation, override to implement your operation

    protected void beforeExecution() {}
    protected void executeOnDatabase(StatelessSession statelessSession) {}
    protected void executeInMemory(Collection<NestedSetNode> inMemoryState) {}
    protected void afterExecution() {}

    // The procedure that executes the sequence of the operation

    public void execute(EventSource session) {
        StatelessSession ss = null;
        Connection jdbcConnection = null;
        try {
            jdbcConnection = getConnection(session);
            ss = session.getSessionFactory().openStatelessSession(jdbcConnection);

            beforeExecution();
            executeOnDatabase(ss);

            // Find all NestedSetNode instances in the persistence context
            Collection<NestedSetNode> nodesInPersistenceContext = new HashSet<NestedSetNode>();
            Iterator contextIterator = new LazyIterator( session.getPersistenceContext().getEntitiesByKey() );
            while (contextIterator.hasNext()) {
                Object o = contextIterator.next();
                if (o instanceof NestedSetNode) nodesInPersistenceContext.add((NestedSetNode)o);
            }

            executeInMemory(nodesInPersistenceContext);
            afterExecution();

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            if (ss != null) {
                try {
                    jdbcConnection.close();
                    ss.close();
                } catch(SQLException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
    }

    protected Connection getConnection(Session session) throws Exception {
        // We do not use session.connection() because it conflicts with Hibernates aggressive collection release
        return ((SessionFactoryImpl)session.getSessionFactory()).getConnectionProvider().getConnection();
    }

}
