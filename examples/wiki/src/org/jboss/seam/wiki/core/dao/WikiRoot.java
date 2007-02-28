package org.jboss.seam.wiki.core.dao;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.node.Directory;

import javax.persistence.EntityManager;
import java.io.Serializable;

@Name("wikiRoot")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class WikiRoot implements Serializable {

    @In
    protected EntityManager entityManager;

    protected Directory wikiRoot;

    @Unwrap
    public Directory getWikiRoot() {
        if (wikiRoot == null) loadWikiRoot();
        return wikiRoot;
    }

    @Transactional
    private void loadWikiRoot() {
        entityManager.joinTransaction();
        try {
            wikiRoot =(Directory)entityManager
                    .createQuery("select d from Directory d where d.parent is null")
                    .getSingleResult();
        } catch (RuntimeException ex) {
            throw new RuntimeException("You need to INSERT at least one parentless directory into the database", ex);
        }
    }

}
