package org.jboss.seam.wiki.core.captcha;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;

import java.io.Serializable;
import java.util.Random;
import java.security.SecureRandom;

@Name("org.jboss.seam.captcha.captcha")
@Scope(ScopeType.PAGE)
@Install(precedence = Install.DEPLOYMENT)
public class WikiCaptcha implements Serializable {

    private String question;
    private transient String response;
    private transient Random myRamdom = new SecureRandom();

    public String getQuestion() {
        int one = myRamdom.nextInt(50);
        int two = myRamdom.nextInt(50);
        question = String.valueOf(one + two);
        response = null;
        return one + " + " + two;
    }

    @WikiCaptchaResponse
    public String getResponse() {
        return response;
    }

    public void setResponse(String input) {
        this.response = input;
    }

    public boolean validateResponse(String response) {
        return question.equals(response);
    }

    public static WikiCaptcha instance() {
        if (!Contexts.isPageContextActive()) {
            throw new IllegalStateException("No page context active");
        }
        return (WikiCaptcha) Component.getInstance(WikiCaptcha.class, ScopeType.PAGE);
    }
}

