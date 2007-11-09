/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.nestedset;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Utility class that implements the basic {@link NestedSetNode} interface.
 * <p>
 * This class can be used as a superclass for any entity in your domain model that represents
 * a node in a nested set. You will still have to implement the <tt>parent</tt> and
 * <tt>children</tt> relationships and map them to the appropriate database foreign key
 * columns. However, you are free to pick any collection type for the <tt>children</tt>
 * collection.
 * <p>
 * Use this class if you already have an adjacency list model (parent/children
 * relationship mapped with a regular many-to-one property and a one-to-many collection) based
 * on a foreign key. You only need to add this superclass to your persistent entity class and
 * you will be able to execute nested set queries on your trees and have the event listeners
 * update the nested set values of the tree (thread, left, right, of each node) if you add or
 * remove nodes.
 * 
 * @author Christian Bauer
 */
@MappedSuperclass
public abstract class AbstractNestedSetNode<N extends NestedSetNode> implements NestedSetNode<N> {

    @Column(name = "NS_THREAD", nullable = false, updatable = false)
    private Long nsThread = 0l;

    @Column(name = "NS_LEFT", nullable = false, updatable = false)
    private Long nsLeft = 0l;

    @Column(name = "NS_RIGHT", nullable = false,  updatable = false)
    private Long nsRight = 0l;

    protected AbstractNestedSetNode() {}

    protected AbstractNestedSetNode(N original) {
        if (original == null) return;
        this.nsThread = original.getNsThread();
        this.nsLeft = original.getNsLeft();
        this.nsRight = original.getNsRight();
    }

    public boolean vetoNestedSetUpdate() {
        return false;
    }

    public void addChild(N child) {
        if (child.getParent() != null) {
            child.getParent().getChildren().remove(child);
        }
        getChildren().add(child);
        child.setParent(this);
    }

    public N removeChild(N child) {
        getChildren().remove(child);
        child.setParent(null);
        return child;
    }

    public Long getNsThread() {
        return nsThread;
    }

    public void setNsThread(Long nsThread) {
        this.nsThread = nsThread;
    }

    public Long getNsLeft() {
        return nsLeft;
    }

    public void setNsLeft(Long nsLeft) {
        this.nsLeft = nsLeft;
    }

    public Long getNsRight() {
        return nsRight;
    }

    public void setNsRight(Long nsRight) {
        this.nsRight = nsRight;
    }

    public int getDirectChildCount() {
        return getChildren().size();
    }

    public int getTotalChildCount() {
        return (int) Math.floor((getNsRight() - getNsLeft()) / 2);
    }

    public String getTreeSuperclassEntityName() {
        return getClass().getSimpleName();
    }

    public String toString() {
        return "(ID: " + getId() + " THREAD: " + getNsThread() + " LEFT: " + getNsLeft() + " RIGHT: " + getNsRight() +")";
    }

}
