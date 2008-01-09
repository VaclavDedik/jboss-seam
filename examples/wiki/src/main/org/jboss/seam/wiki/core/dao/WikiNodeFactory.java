/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.dao;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.model.LinkProtocol;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.wiki.preferences.Preferences;

import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Name("wikiNodeFactory")
public class WikiNodeFactory implements Serializable {

    @In
    protected EntityManager entityManager;

    @In
    protected EntityManager restrictedEntityManager;

    @Factory(value = "wikiRoot", scope = ScopeType.PAGE, autoCreate = true)
    public WikiDirectory loadWikiRoot() {
        try {
            return (WikiDirectory) entityManager
                    .createQuery("select d from WikiDirectory d left join fetch d.feed where d.parent is null")
                    .setHint("org.hibernate.comment", "Loading wikiRoot")
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (RuntimeException ex) {
            throw new RuntimeException("You need to INSERT at least one parentless directory into the database", ex);
        }
    }

    @Factory(value = "wikiStart", scope = ScopeType.PAGE, autoCreate = true)
    public WikiDocument loadWikiStart() {
        WikiPreferences wikiPreferences = (WikiPreferences) Preferences.getInstance("Wiki");
        try {
            return (WikiDocument) restrictedEntityManager
                    .createQuery("select d from WikiDocument d where d.id = :id")
                    .setParameter("id", wikiPreferences.getDefaultDocumentId())
                    .setHint("org.hibernate.comment", "Loading wikiStart")
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }

        // TODO: Message instead!
        throw new RuntimeException("Couldn't find wiki default start document with id '" + wikiPreferences.getDefaultDocumentId() +"'");
    }

    // Loads the same instance into a different persistence context
    @Factory(value = "restrictedWikiRoot", scope = ScopeType.PAGE, autoCreate = true)
    public WikiDirectory loadWikiRootRestricted() {
        WikiDirectory wikiroot = (WikiDirectory) Component.getInstance("wikiRoot");

        try {
            return (WikiDirectory) restrictedEntityManager
                    .createQuery("select d from WikiDirectory d left join fetch d.feed where d.id = :id")
                    .setParameter("id", wikiroot.getId())
                    .setHint("org.hibernate.comment", "Loading wikiRootRestricted")
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (RuntimeException ex) {
            throw new RuntimeException("You need to INSERT at least one parentless directory into the database", ex);
        }
    }

    @Factory(value = "memberArea", scope = ScopeType.PAGE, autoCreate = true)
    public WikiDirectory loadMemberArea() {
        String memberAreaName = ((WikiPreferences)Preferences.getInstance("Wiki")).getMemberArea();
        try {
            return (WikiDirectory) entityManager
                    .createQuery("select d from WikiDirectory d left join fetch d.feed where d.wikiname = :name and d.parent.parent is null")
                    .setParameter("name", WikiUtil.convertToWikiName(memberAreaName) )
                    .setHint("org.hibernate.comment", "Loading memberArea")
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (RuntimeException ex) {
            FacesMessages.instance().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_ERROR,
                "lacewiki.msg.MemberHomedirectoryNotFound",
                "Could not find member area with name {0}  - your configuration is broken, please change it.",
                memberAreaName
            );
            return null;
        }
    }

    @Factory(value = "trashArea", scope = ScopeType.PAGE, autoCreate = true)
    public WikiDirectory loadTrashArea() {
        String trashAreaName = ((WikiPreferences)Preferences.getInstance("Wiki")).getTrashArea();
        try {
            return (WikiDirectory) entityManager
                    .createQuery("select d from WikiDirectory d left join fetch d.feed where d.wikiname = :name and d.parent.parent is null")
                    .setParameter("name", WikiUtil.convertToWikiName(trashAreaName) )
                    .setHint("org.hibernate.comment", "Loading trashArea")
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (RuntimeException ex) {
            FacesMessages.instance().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_ERROR,
                "lacewiki.msg.TrashAreaNotFound",
                "Could not find trash area with name {0}  - your configuration is broken, please change it.",
                trashAreaName
            );
            return null;
        }
    }

    @Factory(value = "helpArea", scope = ScopeType.PAGE, autoCreate = true)
    public WikiDirectory loadHelpArea() {
        String helpAreaName = ((WikiPreferences)Preferences.getInstance("Wiki")).getHelpArea();
        try {
            return (WikiDirectory) entityManager
                    .createQuery("select d from WikiDirectory d left join fetch d.feed where d.wikiname = :name and d.parent.parent is null")
                    .setParameter("name", WikiUtil.convertToWikiName(helpAreaName) )
                    .setHint("org.hibernate.comment", "Loading trashArea")
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (RuntimeException ex) {
            FacesMessages.instance().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_ERROR,
                "lacewiki.msg.HelpAreaNotFound",
                "Could not find help area with name {0}  - your configuration is broken, please change it.",
                helpAreaName
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
                .setHint("org.hibernate.comment", "Loading link protocols")
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
        for (Object[] objects : result) {
            linkProtocols.put((String)objects[0], (LinkProtocol)objects[1]);
        }
        return linkProtocols;
    }

}
