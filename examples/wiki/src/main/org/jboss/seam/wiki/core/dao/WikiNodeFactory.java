/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.dao;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.core.model.LinkProtocol;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.faces.application.FacesMessage;
import java.io.Serializable;
import java.util.*;

@Name("wikiNodeFactory")
public class WikiNodeFactory implements Serializable {

    @In
    protected EntityManager entityManager;

    @In
    protected EntityManager restrictedEntityManager;

    @Factory(value = "wikiRoot", scope = ScopeType.PAGE, autoCreate = true)
    public Directory loadWikiRoot() {
        try {
            return (Directory) entityManager
                    .createQuery("select d from Directory d where d.parent is null")
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (RuntimeException ex) {
            throw new RuntimeException("You need to INSERT at least one parentless directory into the database", ex);
        }
    }

    @Factory(value = "wikiStart", scope = ScopeType.PAGE, autoCreate = true)
    public Document loadWikiStart() {
        WikiPreferences wikiPreferences = (WikiPreferences) Component.getInstance("wikiPreferences");
        try {
            return (Document) restrictedEntityManager
                    .createQuery("select d from Document d where d.id = :id")
                    .setParameter("id", wikiPreferences.getDefaultDocumentId())
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }

        // TODO: Message instead!
        throw new RuntimeException("Couldn't find default document with id '" + wikiPreferences.getDefaultDocumentId() +"'");
    }

    // Loads the same instance into a different persistence context
    @Factory(value = "restrictedWikiRoot", scope = ScopeType.PAGE, autoCreate = true)
    public Directory loadWikiRootRestricted() {
        Directory wikiroot = (Directory) Component.getInstance("wikiRoot");

        try {
            return (Directory) restrictedEntityManager
                    .createQuery("select d from Directory d where d.id = :id")
                    .setParameter("id", wikiroot.getId())
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (RuntimeException ex) {
            throw new RuntimeException("You need to INSERT at least one parentless directory into the database", ex);
        }
    }

    @Factory(value = "memberArea", scope = ScopeType.PAGE, autoCreate = true)
    public Directory loadMemberArea() {
        Long memberAreaId = ((WikiPreferences)Component.getInstance("wikiPreferences")).getMemberAreaId();
        try {
            return (Directory) entityManager
                    .createQuery("select d from Directory d where d.id = :dirId and d.parent.parent is null")
                    .setParameter("dirId", memberAreaId)
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (RuntimeException ex) {
            FacesMessages.instance().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_ERROR,
                "lacewiki.msg.MemberHomedirectoryNotFound",
                "Could not find member area with id {0}  - your configuration is broken, please change it.",
                memberAreaId
            );
            return null;
        }
    }

    @Factory(value = "linkProtocolMap", scope = ScopeType.CONVERSATION, autoCreate = true)
    public Map<String, LinkProtocol> loadLinkProtocols() {
        Map<String, LinkProtocol> linkProtocols = new TreeMap<String, LinkProtocol>();
        //noinspection unchecked
        List<Object[]> result = entityManager
                .createQuery("select lp.prefix, lp from LinkProtocol lp order by lp.prefix asc")
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
        for (Object[] objects : result) {
            linkProtocols.put((String)objects[0], (LinkProtocol)objects[1]);
        }
        return linkProtocols;
    }

}
