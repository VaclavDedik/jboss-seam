/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.nestedset;

/**
 * Generate a copy of a <tt>NestedSetNode</tt>.
 *
 * <p>
 * Used by the <tt>NestedSetResultTransformer</tt> to duplicate nodes after retrieving them
 * from the database. Useful if you need a copy of the tree for display purposes which is stable and
 * does not reflect any changes to the underlying real persistent nodes. This copy should be not
 * connected or hollow, that is, you should copy only the miminum properties you require for display
 * and probably not any collections or to-one entity references.
 * </p>
 *
 * @author Christian Bauer
 */
public interface NestedSetNodeDuplicator<N> {

    /**
     * Make a (probably hollow) copy of the given node.
     * <p>
     * You <b>have to</b> ensure that the copy holds the same identifier value as the original.
     * </p>
     * @param nestedSetNode the persistent node from the database
     * @return null if a copy couln't be made, skipping the node in the tree result transformation
     */
    public N duplicate(N nestedSetNode);
}
