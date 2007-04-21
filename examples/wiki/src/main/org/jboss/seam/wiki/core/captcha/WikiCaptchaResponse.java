package org.jboss.seam.wiki.core.captcha;

import org.hibernate.validator.ValidatorClass;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
@ValidatorClass(WikiCaptchaResponseValidator.class)
public @interface WikiCaptchaResponse {
    String message() default "Your answer was not correct, please try again.";
}
