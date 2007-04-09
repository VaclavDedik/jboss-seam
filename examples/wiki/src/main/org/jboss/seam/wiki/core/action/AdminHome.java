package org.jboss.seam.wiki.core.action;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.model.LinkProtocol;
import org.jboss.seam.wiki.preferences.PreferenceComponent;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;

import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Name("adminHome")
@Scope(ScopeType.CONVERSATION)
public class AdminHome {

    @In
    private FacesMessages facesMessages;

    @In
    EntityManager entityManager;

    PreferenceEditor preferenceEditor;

    @Create
    public void create() {
        if (!Identity.instance().hasPermission("User", "isAdmin", (User)Component.getInstance("currentUser") ) ) {
            throw new AuthorizationException("You don't have permission for this operation");
        }
    }

    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public String update() {

        // Preferences
        if (preferenceEditor != null) {
            String editorFailed = preferenceEditor.save();
            if (editorFailed != null) return null;
        }

        entityManager.flush(); // Flush everything (maybe again if prefEditor.save() already flushed)

        facesMessages.addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_INFO,
            "systemSettingsUpdated",
            "System settings updated"
        );

        return null;
    }

    @DataModel(value = "systemPreferenceComponents")
    private List<PreferenceComponent> systemPreferenceComponents;

    @Factory("systemPreferenceComponents")
    public void loadUserPreferenceComponents() {
        preferenceEditor = (PreferenceEditor)Component.getInstance("prefEditor");
        preferenceEditor.setPreferenceVisibility(PreferenceVisibility.SYSTEM);
        systemPreferenceComponents = preferenceEditor.loadPreferenceComponents();
        Contexts.getConversationContext().set("preferenceEditor", preferenceEditor);
    }

    @DataModel(value = "linkProtocols")
    private List<LinkProtocol> linkProtocols;

    @DataModelSelection(value = "linkProtocols")
    private LinkProtocol selectedLinkProtocol;
    LinkProtocol linkProtocol = new LinkProtocol();
    LinkProtocol newLinkProtocol;

    @Factory("linkProtocols")
    @Transactional
    public void loadLinkProtocols() {
        //noinspection unchecked
        Map<String, LinkProtocol> linkProtocolMap = (Map<String, LinkProtocol>)Component.getInstance("linkProtocolMap");
        linkProtocols = new ArrayList<LinkProtocol>(linkProtocolMap.values());
    }

    public LinkProtocol getLinkProtocol() {
        return linkProtocol;
    }

    @Transactional
    public void addLinkProtocol() {
        entityManager.joinTransaction();

        newLinkProtocol = linkProtocol;
        linkProtocols.add(newLinkProtocol);
        entityManager.persist(newLinkProtocol);
        linkProtocol = new LinkProtocol();
    }

    @Transactional
    public void removeLinkProtocol() {
        entityManager.joinTransaction();
        entityManager.remove(selectedLinkProtocol);
        linkProtocols.remove(selectedLinkProtocol);
    }

}
