/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.nestedset;

import java.util.*;

/**
 * Wraps a {@link NestedSetNode} and links it into a read-only tree of parent and children.
 * <p>
 * This wrapper is returned by the {@link NestedSetResultTransformer}. For example,
 * you query your tree with a nested set query starting from a particular node. You
 * want all children of that start node, including their children, and so on. The
 * {@link NestedSetResultTransformer} will handle your query result, which represents
 * a flat subtree, and link together the nodes in a hierarchical fashion. You will get
 * back your start node in a {@link NestedSetNodeWrapper} and you can access the
 * children and their children, and so on, through the <tt>wrappedChildren</tt> collection
 * of the wrapper. The regular <tt>children</tt> collection of the wrapped
 * {@link NestedSetNode} instances are not initialized! Use the wrapper tree to
 * display the data or to work with the whole subtree. As a bonus you also get
 * the <tt>level</tt> of each node in the (sub)tree you queried. You can access (but not
 * modify) the linked parent of each wrapped node through <tt>wrappedParent</tt>.
 * </p>
 * <p>
 * The <tt>wrappedChildren</tt> of each wrapper are by default in a {@link java.util.List}.
 * You can also access the same nodes through the <tt>getWrappedChildrenSorted()</tt> method,
 * which returns a {@link java.util.SortedSet} that is sorted with the {@link java.util.Comparator}
 * supplied at construction time. This means that in-level sorting (how the children of a particular node
 * are sorted) does not occur in the database but in memory. This should not be a performance problem,
 * as you'd usually query for quite small subtrees, most of the time to display a
 * subtree. The comparator usually sorts the collection by some property of the
 * wrapped {@link NestedSetNode}.
 * </p>
 * <p>
 * Note: Do not modify the collections or the parent reference of the wrapper, these
 * are read-only results and modifications are not reflected in the database.
 * </p>
 *
 * @author Christian Bauer
 */
public class NestedSetNodeWrapper<N extends NestedSetNode> {

    N wrappedNode;
    NestedSetNodeWrapper<N> wrappedParent;
    List<NestedSetNodeWrapper<N>> wrappedChildren = new ArrayList<NestedSetNodeWrapper<N>>();
    Comparator<NestedSetNodeWrapper<N>> comparator;
    Long level;
    Map<String, Object> additionalProjections = new HashMap<String, Object>();
    public boolean childrenLoaded = false;

    public NestedSetNodeWrapper(N wrappedNode, Comparator<NestedSetNodeWrapper<N>> comparator) {
        this(wrappedNode, comparator, 0l);
    }

    public NestedSetNodeWrapper(N wrappedNode, Comparator<NestedSetNodeWrapper<N>> comparator, Long level) {
        this(wrappedNode, comparator, level, new HashMap<String,Object>());
    }

    public NestedSetNodeWrapper(N wrappedNode, Comparator<NestedSetNodeWrapper<N>> comparator, Long level, Map<String,Object> additionalProjections) {
        this.wrappedNode = wrappedNode;
        this.comparator = comparator;
        this.level = level;
        this.additionalProjections = additionalProjections;
    }

    public N getWrappedNode() {
        return wrappedNode;
    }

    public void setWrappedNode(N wrappedNode) {
        this.wrappedNode = wrappedNode;
    }

    public NestedSetNodeWrapper<N> getWrappedParent() {
        return wrappedParent;
    }

    public void setWrappedParent(NestedSetNodeWrapper<N> wrappedParent) {
        this.wrappedParent = wrappedParent;
        childrenLoaded = true;
    }

    public List<NestedSetNodeWrapper<N>> getWrappedChildren() {
        return wrappedChildren;
    }

    public void setWrappedChildren(List<NestedSetNodeWrapper<N>> wrappedChildren) {
        this.wrappedChildren = wrappedChildren;
    }

    public void addWrappedChild(NestedSetNodeWrapper<N> wrappedChild) {
        getWrappedChildren().add(wrappedChild);
        childrenLoaded = true;
    }

    public Comparator<NestedSetNodeWrapper<N>> getComparator() {
        return comparator;
    }

    public Long getLevel() {
        return level;
    }

    public Map<String, Object> getAdditionalProjections() {
        return additionalProjections;
    }

    public SortedSet<NestedSetNodeWrapper<N>> getWrappedChildrenSorted() {
        SortedSet<NestedSetNodeWrapper<N>> sortedSet = new TreeSet<NestedSetNodeWrapper<N>>(comparator);
        sortedSet.addAll(getWrappedChildren());
        return sortedSet;
    }

    public String toString() {
        return "Wrapper on level " + getLevel() + " for: " + getWrappedNode();
    }

}

