package org.jboss.seam.wiki.core.action;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.preferences.PreferenceComponent;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;

import javax.faces.application.FacesMessage;
import java.util.List;

@Name("adminHome")
@Scope(ScopeType.CONVERSATION)
public class AdminHome {

    @In
    private FacesMessages facesMessages;

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

        facesMessages.addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_INFO,
            "systemSettingsUpdated",
            "System settings updated"
        );
        return null;
    }

    @DataModel
    private List<PreferenceComponent> systemPreferenceComponents;

    @Factory("systemPreferenceComponents")
    public void loadUserPreferenceComponents() {
        preferenceEditor = (PreferenceEditor)Component.getInstance("prefEditor");
        preferenceEditor.setPreferenceVisibility(PreferenceVisibility.SYSTEM);
        systemPreferenceComponents = preferenceEditor.loadPreferenceComponents();
        Contexts.getConversationContext().set("preferenceEditor", preferenceEditor);
    }
}
