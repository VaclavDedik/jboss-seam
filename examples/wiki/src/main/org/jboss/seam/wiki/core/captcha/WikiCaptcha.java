/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.captcha;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

import javax.faces.application.FacesMessage;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Supertrivial replacement for the broken JCaptcha stuff.
 *
 * TODO: Replace with good captcha image generator.
 *
 * @author Christian Bauer
 */
@Name("org.jboss.seam.captcha.captcha")
@Scope(ScopeType.PAGE)
@Install(precedence = Install.DEPLOYMENT)
public class WikiCaptcha implements Serializable {

    private int one;
    private int two;
    private transient String response;
    private transient Random myRamdom = new SecureRandom();

    public String getQuestion() {
        return one + " + " + two;
    }

    @Create
    public void reset() {
        one = myRamdom.nextInt(50);
        two = myRamdom.nextInt(50);
    }

    @WikiCaptchaResponse
    public String getResponse() {
        return response;
    }

    public void setResponse(String input) {
        this.response = input;
    }

    public boolean validateResponse(String response) {
        try {
            new Integer(response);
        } catch (NumberFormatException ex) {
            this.response = null;
            return false;
        }

        if (new Integer(one + two).equals(new Integer(response))) {
            // TODO: Fuck that, doesn't clean out the old value.... no idea why
            this.response = null;
            reset();
            return true;
        } else {
            this.response = null;
            return false;
        }
    }

    public static WikiCaptcha instance() {
        if (!Contexts.isPageContextActive()) {
            throw new IllegalStateException("No page context active");
        }
        return (WikiCaptcha) Component.getInstance(WikiCaptcha.class, ScopeType.PAGE);
    }
}

