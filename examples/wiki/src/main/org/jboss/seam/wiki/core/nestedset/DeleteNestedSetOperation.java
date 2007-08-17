/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.nestedset;

import org.hibernate.Query;
import org.hibernate.StatelessSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;

/**
 * Moves the values of all nodes on the right side of a deleted node.
 *
 * @author Christian Bauer
 */
class DeleteNestedSetOperation extends NestedSetOperation {

    private static final Log log = LogFactory.getLog(DeleteNestedSetOperation.class);

    long databaseMoveOffset;

    public DeleteNestedSetOperation(NestedSetNode node) {
        super(node);
    }

    protected void beforeExecution() {
        databaseMoveOffset = node.getNsRight() - node.getNsLeft() + 1;
        log.trace("calculated database offset: " + databaseMoveOffset);
    }

    protected void executeOnDatabase(StatelessSession ss) {

        Query updateLeft =
                ss.createQuery("update " + nodeEntityName + " n set " +
                               " n.nsLeft = n.nsLeft - :offset " +
                               " where n.nsThread = :thread and n.nsLeft > :right");
        updateLeft.setParameter("offset", databaseMoveOffset);
        updateLeft.setParameter("thread", node.getNsThread());
        updateLeft.setParameter("right", node.getNsRight());
        int updateLeftCount = updateLeft.executeUpdate();
        log.trace("updated left values of nested set nodes: " + updateLeftCount);

        Query updateRight =
                ss.createQuery("update " + nodeEntityName + " n set " +
                               " n.nsRight = n.nsRight - :offset " +
                               " where n.nsThread = :thread and n.nsRight > :right");
        updateRight.setParameter("offset", databaseMoveOffset);
        updateRight.setParameter("thread", node.getNsThread());
        updateRight.setParameter("right", node.getNsRight());
        int updateRightCount = updateRight.executeUpdate();
        log.trace("updated right values of nested set nodes: " + updateRightCount);
    }

    protected void executeInMemory(Collection<NestedSetNode> nodesInPersistenceContext) {
        log.trace("updating in memory nodes (flat) in the persistence context: " + nodesInPersistenceContext.size());

        for (NestedSetNode n: nodesInPersistenceContext) {

            if (n.getNsThread().equals(node.getNsThread())
                && n.getNsRight() > node.getNsRight()) {

                n.setNsRight(n.getNsRight() - databaseMoveOffset);
            }

            if (n.getNsThread().equals(node.getNsThread())
                && n.getNsLeft() > node.getNsRight()) {

                n.setNsLeft(n.getNsLeft() - databaseMoveOffset);
            }
        }
    }
}
