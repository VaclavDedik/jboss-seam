package org.jboss.seam.wiki.core.captcha;

import org.hibernate.validator.Validator;

import java.lang.annotation.Annotation;

public class WikiCaptchaResponseValidator implements Validator {

    public void initialize(Annotation captchaResponse) {
    }

    public boolean isValid(Object response) {
        return WikiCaptcha.instance().validateResponse((String) response);
    }
}
