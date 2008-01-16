/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.faqBrowser;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.wiki.core.model.WikiDirectory;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("faqBrowserDAO")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class FaqBrowserDAO implements Serializable {

    @In
    EntityManager restrictedEntityManager;

    public WikiDirectory findFaqRootDir(WikiDirectory startDir) {

        StringBuilder queryString = new StringBuilder();
        queryString.append("select dir from WikiDirectory dir join dir.defaultFile f, WikiDocument doc");
        queryString.append(" where dir.nodeInfo.nsThread = :nsThread and");
        queryString.append(" dir.nodeInfo.nsLeft <= :nsLeft and dir.nodeInfo.nsRight >= :nsRight");
        queryString.append(" and f = doc and doc.headerMacrosString like '%faqBrowser%'");

        Query query = restrictedEntityManager.createQuery(queryString.toString())
                .setParameter("nsThread", startDir.getNodeInfo().getNsThread())
                .setParameter("nsLeft", startDir.getNodeInfo().getNsLeft())
                .setParameter("nsRight", startDir.getNodeInfo().getNsRight());

        try {
            return (WikiDirectory) query.getSingleResult();
        }
        catch (EntityNotFoundException ex) {}
        catch (NoResultException ex) {}
        return null;
    }

}
