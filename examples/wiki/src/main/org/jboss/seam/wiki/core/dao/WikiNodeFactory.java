package org.jboss.seam.wiki.core.dao;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.GlobalPreferences;
import org.jboss.seam.wiki.core.model.Document;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.io.Serializable;

@Name("wikiNodeFactory")
public class WikiNodeFactory implements Serializable {

    @In
    protected EntityManager entityManager;

    @In
    protected EntityManager restrictedEntityManager;

    @In
    protected GlobalPreferences globalPrefs;

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

    @Factory(value = "wikiStart", scope = ScopeType.CONVERSATION, autoCreate = true)
    @Transactional
    public Document loadWikiStart() {
        restrictedEntityManager.joinTransaction();
        try {
            return (Document) restrictedEntityManager
                    .createQuery("select d from Document d where d.id = :id")
                    .setParameter("id", globalPrefs.getDefaultDocumentId())
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }
        throw new RuntimeException("Couldn't find default document with id '" + globalPrefs.getDefaultDocumentId() +"'");
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
