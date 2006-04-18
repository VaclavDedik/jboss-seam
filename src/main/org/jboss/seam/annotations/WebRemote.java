package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated method is accessible via the remoting framework.
 *
 * @author Shane Bryzak
 */
@Target(METHOD)
@Documented
@Retention(RUNTIME)
public @interface WebRemote {
}
