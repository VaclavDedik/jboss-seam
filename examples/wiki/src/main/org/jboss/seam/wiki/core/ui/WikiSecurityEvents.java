/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.security.FacesSecurityEvents;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import javax.faces.application.FacesMessage;

/**
 * @author Christian Bauer
 */
@Name("org.jboss.seam.security.facesSecurityEvents")
@Install(precedence = Install.APPLICATION, classDependencies = "javax.faces.context.FacesContext")
@BypassInterceptors
@Startup
public class WikiSecurityEvents extends FacesSecurityEvents {

    public FacesMessage.Severity getLoginFailedMessageSeverity() {
        return FacesMessage.SEVERITY_WARN;
    }
}
