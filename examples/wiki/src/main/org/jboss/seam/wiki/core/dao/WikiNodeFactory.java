package org.jboss.seam.wiki.core.dao;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.GlobalPreferences;

import javax.persistence.EntityManager;
import java.io.Serializable;

@Name("wikiNodeFactory")
public class WikiNodeFactory implements Serializable {

    @In
    protected EntityManager entityManager;

    @In
    protected EntityManager restrictedEntityManager;

    @Factory(value = "wikiRoot", scope = ScopeType.CONVERSATION, autoCreate = true)
    @Transactional
    public Directory loadWikiRoot() {
        entityManager.joinTransaction();
        try {
            return (Directory) entityManager
                    .createQuery("select d from Directory d where d.parent is null")
                    .getSingleResult();
        } catch (RuntimeException ex) {
            throw new RuntimeException("You need to INSERT at least one parentless directory into the database", ex);
        }
    }

    // Loads the same instance into a different persistence context
    @Factory(value = "restrictedWikiRoot", scope = ScopeType.CONVERSATION, autoCreate = true)
    @Transactional
    public Directory loadWikiRootRestricted() {
        Directory wikiroot = (Directory) Component.getInstance("wikiRoot");
        restrictedEntityManager.joinTransaction();
        return restrictedEntityManager.find(Directory.class, wikiroot.getId());
    }

    @Factory(value = "memberArea", scope = ScopeType.CONVERSATION, autoCreate = true)
    @Transactional
    public Directory loadMemberArea() {
        Long memberAreaId = ((GlobalPreferences)Component.getInstance("globalPrefs")).getMemberAreaId();
        entityManager.joinTransaction();
        try {
            return (Directory) entityManager
                    .createQuery("select d from Directory d where d.id = :dirId and d.parent.parent is null")
                    .setParameter("dirId", memberAreaId)
                    .getSingleResult();
        } catch (RuntimeException ex) {
            throw new RuntimeException("Could not find member area with id " + memberAreaId, ex);
        }
    }

}
