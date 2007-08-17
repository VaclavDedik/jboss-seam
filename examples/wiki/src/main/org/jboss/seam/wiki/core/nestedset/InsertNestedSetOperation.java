/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.nestedset;

import org.hibernate.StatelessSession;
import org.hibernate.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;

/**
 * Moves the values of all nodes on the right side of an inserted node.
 *
 * @author Christian Bauer
 */
class InsertNestedSetOperation extends NestedSetOperation {

    private static final Log log = LogFactory.getLog(InsertNestedSetOperation.class);

    long spaceNeeded = 2l;
    long parentThread;
    long newLeft;
    long newRight;

    public InsertNestedSetOperation(NestedSetNode node) {
        super(node);
    }

    protected void beforeExecution() {
        if (node.getParent() == null) {
            // Root node of a tree, new thread
            parentThread = node.getId();
            newLeft = 1l;
            newRight = 2l;
        } else {
            // Child node of a parent
            parentThread = node.getParent().getNsThread();
            newLeft = node.getParent().getNsRight();
            newRight = newLeft + spaceNeeded -1;
        }
        log.trace("calculated the thread: " + parentThread + " left: " + newLeft + " right: " + newRight);
    }

    protected void executeOnDatabase(StatelessSession ss) {

        Query updateLeft =
                ss.createQuery("update " + nodeEntityName + " n set " +
                               " n.nsLeft = n.nsLeft + :spaceNeeded " +
                               " where n.nsThread = :thread and n.nsLeft > :right");
        updateLeft.setParameter("spaceNeeded", spaceNeeded);
        updateLeft.setParameter("thread", parentThread);
        updateLeft.setParameter("right", newLeft);
        int updateLeftCount = updateLeft.executeUpdate();
        log.trace("updated left values of nested set nodes: " + updateLeftCount);

        Query updateRight =
                ss.createQuery("update " + nodeEntityName + " n set " +
                               " n.nsRight = n.nsRight + :spaceNeeded " +
                               " where n.nsThread = :thread and n.nsRight >= :right");
        updateRight.setParameter("spaceNeeded", spaceNeeded);
        updateRight.setParameter("thread", parentThread);
        updateRight.setParameter("right", newLeft);
        int updateRightCount = updateRight.executeUpdate();
        log.trace("updated right values of nested set nodes: " + updateRightCount);

        log.trace("updating the newly inserted row with thread, left, and right values");
        Query updateNode =
                ss.createQuery("update " + nodeEntityName + " n set " +
                               " n.nsLeft = :left, n.nsRight = :right, n.nsThread = :thread " +
                               " where n.id = :nid");
        updateNode.setParameter("thread", parentThread);
        updateNode.setParameter("left", newLeft);
        updateNode.setParameter("right", newRight );
        updateNode.setParameter("nid", node.getId());
        updateNode.executeUpdate();
    }

    protected void executeInMemory(Collection<NestedSetNode> nodesInPersistenceContext) {
        log.trace("updating in memory nodes (flat) in the persistence context: " + nodesInPersistenceContext.size());

        for (NestedSetNode n : nodesInPersistenceContext) {
            if (n.getNsThread().equals(parentThread) && n.getNsLeft() > newLeft) {
                n.setNsLeft(n.getNsLeft() + spaceNeeded);
            }
            if (n.getNsThread().equals(parentThread) && n.getNsRight() >= newLeft) {
                n.setNsRight(n.getNsRight() + spaceNeeded);
            }
        }

    }

    protected void afterExecution() {
        // Set the values of the "read-only" properties
        node.setNsThread(parentThread);
        node.setNsLeft(newLeft);
        node.setNsRight(newRight);
    }
}
