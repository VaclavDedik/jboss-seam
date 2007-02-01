package org.jboss.seam.wiki.core.node;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;

import javax.persistence.EntityManager;

@Name("wikiRoot")
@Scope(ScopeType.CONVERSATION)
public class WikiRoot {

    @In(create = true)
    protected EntityManager entityManager;

    protected Directory wikiRoot;

    @Unwrap
    public Directory getWikiRoot() {
        if (wikiRoot == null) loadWikiRoot();
        return wikiRoot;
    }

    @Transactional
    private void loadWikiRoot() {
        try {
            wikiRoot =(Directory)entityManager
                    .createQuery("select d from Directory d where d.parent is null")
                    .getSingleResult();
        } catch (RuntimeException ex) {
            throw new RuntimeException("You need to INSERT at least one parentless directory into the database", ex);
        }
    }

}
