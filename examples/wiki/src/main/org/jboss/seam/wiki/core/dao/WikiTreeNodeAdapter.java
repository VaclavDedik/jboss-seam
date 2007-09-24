package org.jboss.seam.wiki.core.dao;

import org.richfaces.model.TreeNode;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.nestedset.NestedSetNodeWrapper;
import org.jboss.seam.wiki.core.nestedset.NestedSetResultTransformer;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.Component;

import java.util.*;

public class WikiTreeNodeAdapter extends NestedSetNodeWrapper<Node>
        implements org.richfaces.model.TreeNode {

    private Long prefetchDepth;

    public WikiTreeNodeAdapter(Node wrappedNode, Comparator<NestedSetNodeWrapper<Node>> comparator, Long prefetchDepth) {
        super(wrappedNode, comparator);
        this.prefetchDepth = prefetchDepth;
    }

    private WikiTreeNodeAdapter(Node wrappedNode,
                                Comparator<NestedSetNodeWrapper<Node>> comparator,
                                Long level,
                                Map<String, Object> additionalProjections,
                                Long prefetchDepth) {
        super(wrappedNode, comparator, level, additionalProjections);
        this.prefetchDepth = prefetchDepth;
    }

    public Object getData() {
        return this;
    }

    public boolean isLeaf() {
        //return !WikiUtil.isDirectory(this.getWrappedNode());
        if (!childrenLoaded && getWrappedNode().getTotalChildCount() != 0) {
            System.out.println("###### LOADING CHILDREN OF: " + this);
            loadChildren();
            childrenLoaded = true;
        }
        if (childrenLoaded) {
            System.out.println("###### CHILDREN ARE LOADED OF: " + this);
            return getWrappedChildren().size() == 0;
        }
        System.out.println("###### HUH?: " + this);
        return true;
    }

    public Iterator getChildren() {

        //if (!childrenLoaded) loadChildren();

        // Super ugly API, need to return Map.Entry
        return new Iterator() {
            final List wrappedList = new ArrayList(getWrappedChildrenSorted());
            Iterator wrappedIterator = new ArrayList(getWrappedChildrenSorted()).iterator();
            public boolean hasNext() {
                return wrappedIterator.hasNext();
            }
            public Object next() {
                final WikiTreeNodeAdapter next = (WikiTreeNodeAdapter)wrappedIterator.next();
                return new Map.Entry() {
                    public Object getKey() { return wrappedList.indexOf(next); }
                    public Object getValue() { return next; }
                    public Object setValue(Object o) { throw new UnsupportedOperationException(); }
                };
            }
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }

    public TreeNode getChild(Object identifier) {
        return (WikiTreeNodeAdapter)new ArrayList(getWrappedChildrenSorted()).get( (Integer)identifier );
    }

    public TreeNode getParent() {
        return (WikiTreeNodeAdapter)getWrappedParent();
    }

    public void setData(Object o) {
        throw new UnsupportedOperationException("Tree is immutable!");
    }

    public void addChild(Object o, TreeNode treeNode) {
        throw new UnsupportedOperationException("Tree is immutable!");
    }

    public void removeChild(Object o) {
        throw new UnsupportedOperationException("Tree is immutable!");
    }

    public void setParent(TreeNode treeNode) {
        throw new UnsupportedOperationException("Tree is immutable!");
    }

    public void loadChildren() {

        // Override creation of adapters when the tree is transformed
        NestedSetResultTransformer<Node> transformer =
            new NestedSetResultTransformer<Node>(this) {
                public NestedSetNodeWrapper<Node> createNestedSetNodeWrapper(Node nestedSetNode,
                                                                             Comparator<NestedSetNodeWrapper<Node>> comparator,
                                                                             Long nestedSetNodeLevel,
                                                                             Map<String, Object> additionalProjectionValues) {
                    return new WikiTreeNodeAdapter(nestedSetNode, comparator, nestedSetNodeLevel, additionalProjectionValues, prefetchDepth);
                }

                public void flattenTree(List<NestedSetNodeWrapper<Node>> flatChildren, long i, NestedSetNodeWrapper<Node> nestedSetNodeWrapper) {
                    throw new UnsupportedOperationException("Can't flatten an adapted nested set tree for RichFaces display");
                }
            };

        NodeDAO nodeDAO = (NodeDAO) Component.getInstance("nodeDAO");

        nodeDAO.appendNestedSetNodes(transformer, prefetchDepth, false);
        //nodeDAO.appendNestedSetNodes(transformer, prefetchDepth, false, "n1.class = 'DIRECTORY'");
    }

}
