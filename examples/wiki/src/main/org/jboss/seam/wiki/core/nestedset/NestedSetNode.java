/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.nestedset;

import java.util.Collection;

/**
 * Interface implemented by domain model classes that represent a node in a nested set.
 *
 * @author Christian Bauer
 */
public interface NestedSetNode<N extends NestedSetNode> {

    /**
     * Every node in a nested set needs a stable identifier, the primary key. This currently
     * is limited to numeric (long) values because the nested set model uses the identifier
     * of a root node (a node with no parent) to also identify a particular tree (the thread).
     *
     * @return the stable primary key of the nested set node
     */
    public Long getId();

    /**
     * A node can veto any nested set updates.
     * <p>
     * This is useful if a particular instance doesn't really belong into the tree but has
     * the same type. For example, if you have <tt>FileSystemNode</tt> implements
     * <tt>NestedSetNode</tt> and <tt>HistoricalFileSystemNode</tt> (for audit logging, saved
     * to a completely different table than the tree) then <tt>HistoricalFileSystemNode</tt>
     * can return a veto when it is saved, so that the main tree will not be updated.
     *
     * @return true if a particular instance should not trigger nested set tree updates
     */
    public boolean vetoNestedSetUpdate();

    /**
     * Nested set updates require that direct DML is executed by event listeners, this is
     * the name of the entity that is used by the event listeners. You can in most cases
     * return the simple class name of an instance, unless you customize your persistent
     * entity identifiers or when inheritance is involved. If, for example, you have a
     * class named <tt>FileSystemNode</tt> that implements <tt>NestedSetNode</tt>, and this
     * class has subclasses <tt>RegularFile</tt> and <tt>Directory</tt>, all instances need
     * to return <tt>FileSystemNode</tt> so that all nodes in the tree can be reached when
     * the nested set manipulation occurs in event listeners.
     *
     * @return the persistent entity name of the superclass that implements this interface
     */
    public String getTreeSuperclassEntityName();
    public Class getTreeSuperclass();

    /**
     * Utility method required until TODO: http://opensource.atlassian.com/projects/hibernate/browse/HHH-1615
     * is implemented. If you query for nested set subtrees, you need to group by all properties of
     * the nested set node implementation class (in fact, the whole hierarchy). So for example,
     * this method would need to return all scalar property names and foreign key property names of
     * classes <tt>FileSystemNode</tt> and its potential subclasses <tt>RegularFile</tt> and
     * <tt>Directory</tt>. Yes, this is not not great.
     *
     * @return all property names of scalar and foreign key properties of the nested set class hierarchy
     */
    public abstract String[] getTreeSuperclassPropertiesForGrouping();

    /**
     * An implementation must return the parent instance of a node in the tree, this can be mapped
     * as a regular many-to-one. This property should be nullable, that is, the root node of a thread
     * (a thread represents a single tree) does not have a parent. Although not strictly required by
     * the nested set approach (children of a parent can be identified solely by their "left" and
     * "right" values), it simplifies regular navigation "up the tree".
     *
     * @return the parent of this nested set node
     */
    public N getParent();
    public void setParent(N parent);

    /**
     * An implementation must return the direct children (the sublevel) of a nested set node. This
     * can be mapped as a regular one-to-many collection, however, you are free to chose the type
     * of collection: lists, bags, and sets all work. This collection is the collection you need
     * to modify if you want to link a child node to a parent (by adding an element) or if you
     * delete a node (by removing an element).
     *
     * @return the immediate children of a nested set node
     */
    public Collection<N> getChildren();

    /**
     * Convenience method that should link a node into the tree by adding it to the <tt>children</tt>
     * collection and setting its <tt>parent</tt> to be <i>this</i>.
     *
     * @param child the child node to be added at this level in the tree
     */
    public void addChild(N child);

    /**
     * Convenience method that should remove a node from this level of the tree by removing it from
     * the <tt>children</tt> collection and setting its <tt>parent</tt> to null. Called before a
     * node is finally deleted in a persistent fashion, in your application.
     *
     * @param child the child node that is removed from the tree
     * @return the removed child node
     */
    public N removeChild(N child);

    /**
     * The root of a tree is a nested set node without a parent. Its identifier is also the thread
     * number of all nodes in that particular tree. So all children nodes (and their children, recursively)
     * need to have the same thread number. This should be mapped as a persistent property of the
     * implementor of this interface, not nullable and not updatable. Any updates that are required are
     * done transparently with event listeners.
     *
     * @return the non-nullable, persistent, and not updatable mapped persistent identifier for a particular tree
     */
    public Long getNsThread();
    public void setNsThread(Long nsThread);

    /**
     * In the nested set model, each node requires two additional attributes right visit and left visit. The tree is
     * then traversed in a modified pre-order: starting with the root node, each node is visited twice. Whenever
     * a node is entered or exited during the traversal, the sequence number of all visits is saved in
     * the current node's right visit and left visit. This is the job of the event listeners, not yours. You can
     * retrieve the current value with this method.
     *
     * @return the left value of a node
     */
    public Long getNsLeft();
    public void setNsLeft(Long nsLeft);

    /**
     * In the nested set model, each node requires two additional attributes right visit and left visit. The tree is
     * then traversed in a modified pre-order: starting with the root node, each node is visited twice. Whenever
     * a node is entered or exited during the traversal, the sequence number of all visits is saved in
     * the current node's right visit and left visit. This is the job of the event listeners, not yours. You can
     * retrieve the current value with this method.
     *
     * @return the left value of a node
     */
    public Long getNsRight();
    public void setNsRight(Long nsRight);

    /**
     * The number of children of this node, only one level deep.
     *
     * @return the number of children of this node, one level deep.
     */
    public int getDirectChildCount();

    /**
     * The number of child nodes of this node, total at all sub-levels. This can be calculated by taking
     * the left and right values: <tt>Math.floor((getNsRight() - getNsLeft()) / 2)</tt>
     *
     * @return the total number of children on all sub-levels
     */
    public int getTotalChildCount();
    
}
