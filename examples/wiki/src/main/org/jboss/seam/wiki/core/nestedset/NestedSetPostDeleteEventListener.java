/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.nestedset;

import org.hibernate.ejb.event.EJB3PostDeleteEventListener;
import org.hibernate.event.PostDeleteEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Executes the nested set tree traversal after a node was deleted.
 *
 * @author Christian Bauer
 */
public class NestedSetPostDeleteEventListener extends EJB3PostDeleteEventListener {

    private static final Log log = LogFactory.getLog(NestedSetPostDeleteEventListener.class);

    public void onPostDelete(PostDeleteEvent event) {
        super.onPostDelete(event);

        if (event.getEntity() instanceof NestedSetNode) {
            if (event.getEntity() instanceof NestedSetNode) {
                log.debug("executing nested set delete operation, recalculating the tree");
                new DeleteNestedSetOperation( (NestedSetNode)event.getEntity() ).execute(event.getSession());
             }
        }
    }

}
