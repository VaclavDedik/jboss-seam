/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.nestedset;

import org.hibernate.transform.ResultTransformer;

import java.util.*;
import java.io.Serializable;

/**
 * Transforms a nested set query result into a tree of in-memory linked {@link NestedSetNodeWrapper} instances.
 * <p>
 * A typical nested set query, for all subtree nodes starting at a particular node, in HQL, looks as follows:
 * </p>
 * <pre>
 * select
 *  count(n1.id) as nestedSetNodeLevel,
 *  n1           as nestedSetNode
 * from [NestedSetNodeEntityName] n1, [NestedSetNodeEntityName] n2
 * where
 *  n1.nsThread = :thread and n2.nsThread = :thread
 *  and n1.nsLeft between n2.nsLeft and n2.nsRight
 *  and n2.nsLeft > :startLeft and n2.nsRight < :startRight
 * group by [allPropertiesOfNestedSetNodeEntityClassHierarchy]
 * order by n1.nsLeft
 * </pre>
 * <p>
 * The values for <tt>thread</tt>, <tt>startLeft</tt>, and <tt>startRight</tt> parameters are the values of
 * the root node of the subtree you want to query for. This start node is not included in the query result.
 * </p>
 * <p>
 * This transformer expects two projected values, with the alias names <tt>nestedSetNodeLevel</tt> and
 * <tt>nestedSetNode</tt>. Your query must return these two values in that order. The transformer uses
 * these values to build an in-memory tree of linked {@link NestedSetNodeWrapper} instances from the
 * flat tree in the query result.
 * </p>
 * <p>
 * You need to manually create a {@link NestedSetNodeWrapper} for the start node of your query (which
 * is not included in the query result) and pass it to the constructor of the transformer. For example:
 * </p>
 * <pre>
 * NestedSetNodeWrapper<N> startNodeWrapper = new NestedSetNodeWrapper<N>(startNode, comparator);
 * nestedSetQuery.setResultTransformer( new NestedSetResultTransformer<N>(startNodeWrapper) );
 * nestedSetQuery.list();
 * </pre>
 * <p>
 * The start node is at level zero. The query returns nothing, the transformer takes each tuple of the
 * result and appends it to the tree, on the <tt>startNodeWrapper</tt>. You can now navigate the tree
 * you loaded by accessing the <tt>wrappedChildren</tt> collection (recursiveley all the way down) and
 * <tt>wrappedParent</tt> property, starting with the <tt>startNodeWrapper</tt> (which doesn't have a
 * linked <tt>wrappedParent</tt>). The <tt>wrappedChildrenSorted</tt> colleciton of each wrapper is
 * sorted with the given {@link java.util.Comparator}.
 * </p>
 * <p>
 * If you supply a <tt>flattenToLevel</tt> constructor argument, the transformed tree will be flattened
 * to the specified level. If, for example, you declare that the tree should be flattened to level 3, all
 * nodes that are deeper than level 3 will be appended to the parent in level 3, so that the tree is no
 * deeper than 3 levels. This is useful for certain kinds of tree display.
 * </p>
 * <p>
 * This transformer accepts a <tt>NestedSetNodeDuplicator</tt> instance which will be used, if not null, to
 * copy every <tt>nestedSetNode</tt> returned by the query. This is useful if you do not want or require the
 * original <tt>nestedSetNode</tt> instances wrapped, for example, if changes on these instances should not
 * be reflected by the wrapped instances. You can effectively generate a stable copy of the tree, e.g. for
 * display purposes.
 * </p>
 * <p>
 * A note about restrictions: If the only restriction condition in your query is the one shown above, limiting
 * the returned tuples to the nodes of the subtree, you will have a whole and complete subtree, hence, you will
 * not have any gaps in the in-memory tree of {@link NestedSetNodeWrapper}s returned by the transformer. However,
 * if you add another condition (e.g. "only return tuples <tt>where isMenuItem = true</tt>"), you will have gaps
 * in the in-memory tree. These gaps can be recursive, for example, if a subnode B has children C, D, and E, and only
 * C, D, and E have the <tt>isMenuItem</tt> flag enabled, they will not be included in the in-memory tree because
 * their parent, B, does not have the <tt>isMenuItem</tt> flag enabled. The query won't return B so its children,
 * which are returned by the query, can't be linked into the in-memory tree. They will be ignored. This might be
 * the correct behavior for building a tree of menu items, but there are certainly situations when you don't want
 * these gaps but only restrict what <i>leaf</i> nodes are included in the tree. This is currently not possible with
 * the query/transform approach.
 * </p>
 *
 * @author Christian Bauer
 */
public class NestedSetResultTransformer<N extends NestedSetNode> implements ResultTransformer {

    Comparator<NestedSetNodeWrapper<N>> comparator;
    NestedSetNodeWrapper<N> rootWrapper;
    NestedSetNodeWrapper<N> currentParent;
    long flattenToLevel = 0;
    Map<String, String> additionalProjections = new HashMap<String, String>();
    NestedSetNodeDuplicator<N> nestedSetNodeDuplicator = null;

    public NestedSetResultTransformer(NestedSetNodeWrapper<N> rootWrapper) {
        this(rootWrapper, 0l);
    }

    public NestedSetResultTransformer(NestedSetNodeWrapper<N> rootWrapper, long flattenToLevel) {
        this(rootWrapper, flattenToLevel, new HashMap<String, String>());
    }

    public NestedSetResultTransformer(NestedSetNodeWrapper<N> rootWrapper, long flattenToLevel, Map<String, String> additionalProjections) {
        this.rootWrapper = rootWrapper;
        this.flattenToLevel = flattenToLevel;
        this.additionalProjections = additionalProjections;

        this.comparator = rootWrapper.getComparator();
        currentParent = rootWrapper;
    }

    public NestedSetNodeWrapper<N> getRootWrapper() {
        return rootWrapper;
    }

    public Map<String, String> getAdditionalProjections() {
        return additionalProjections;
    }

    public void setNestedSetNodeDuplicator(NestedSetNodeDuplicator<N> nestedSetNodeDuplicator) {
        this.nestedSetNodeDuplicator = nestedSetNodeDuplicator;
    }

    public Object transformTuple(Object[] objects, String[] aliases) {

        if (!"nestedSetNodeLevel".equals(aliases[0]))
            throw new RuntimeException("Missing alias 'nestedSetNodeLevel' as the first projected value in the nested set query");
        if (!"nestedSetNode".equals(aliases[1]))
            throw new RuntimeException("Missing alias 'nestedSetNode' as the second projected value in the nested set query");
        if (objects.length < 2) {
            throw new RuntimeException("Nested set query needs to at least return two values, the level and the nested set node instance");
        }

        Long nestedSetNodeLevel = (Long)objects[0];
        N nestedSetNode = (N)objects[1];

        Long nestedSetNodeParentId = nestedSetNode.getParent().getId(); // Store the parent id before making a duplicate
        if (nestedSetNodeDuplicator != null) nestedSetNode = nestedSetNodeDuplicator.duplicate(nestedSetNode);
        if (nestedSetNode == null) return null; // Continue in loop if the duplicator didn't make a proper copy

        Map<String, Object> additionalProjectionValues = new LinkedHashMap<String, Object>();
        int i = 2;
        for (Map.Entry<String, String> entry : additionalProjections.entrySet()) {
            additionalProjectionValues.put(entry.getKey(), objects[i++]);
        }

        // Connect the tree hierarchically (child to parent, skip child if parent isn't present)
        NestedSetNodeWrapper<N> nodeWrapper = new NestedSetNodeWrapper<N>(nestedSetNode, comparator, nestedSetNodeLevel, additionalProjectionValues);
        if (!nestedSetNodeParentId.equals(currentParent.getWrappedNode().getId())) {
            NestedSetNodeWrapper<N> foundParent = findParentInTree(nestedSetNodeParentId, currentParent);
            if (foundParent != null) {
                currentParent = foundParent;
            } else {
                return null; // Continue
            }
        }
        nodeWrapper.setWrappedParent(currentParent);
        currentParent.getWrappedChildren().add(nodeWrapper);
        currentParent = nodeWrapper;

        return rootWrapper; // Return just something so that transformList() will be called when we are done
    }

    private NestedSetNodeWrapper<N> findParentInTree(Serializable parentId, NestedSetNodeWrapper<N> startNode) {
        if (!parentId.equals(startNode.getWrappedNode().getId()) && startNode.getWrappedParent() != null) {
            return findParentInTree(parentId, startNode.getWrappedParent());
        } else if (parentId.equals(startNode.getWrappedNode().getId())) {
            return startNode;
        } else {
            return null;
        }
    }

    public List transformList(List list) {
        if (flattenToLevel > 0) {
            List<NestedSetNodeWrapper<N>> flatChildren = new ArrayList<NestedSetNodeWrapper<N>>();
            for(NestedSetNodeWrapper<N> child: rootWrapper.getWrappedChildrenSorted()) {
                flattenTree(flatChildren, 1l, child);
            }
            rootWrapper.setWrappedChildren(flatChildren);
        }
        return new ArrayList(); // Nothing is returned from this transformer
    }

    // Recursively flatten tree
    private void flattenTree(List<NestedSetNodeWrapper<N>> flatChildren, long i, NestedSetNodeWrapper<N> wrapper) {
        NestedSetNodeWrapper<N> newWrapper =
                new NestedSetNodeWrapper<N>(wrapper.getWrappedNode(), comparator, i, wrapper.getAdditionalProjections());
        flatChildren.add( newWrapper );
        if (wrapper.getWrappedChildren().size() > 0 && wrapper.getLevel() < flattenToLevel) {
            i++;
            for (NestedSetNodeWrapper<N> child : wrapper.getWrappedChildrenSorted()) {
                flattenTree(newWrapper.getWrappedChildren(), i, child);
            }
        } else if (wrapper.getWrappedChildren().size() > 0) {
            for (NestedSetNodeWrapper<N> child : wrapper.getWrappedChildrenSorted()) {
                flattenTree(flatChildren, i, child);
            }
        }
    }

}
