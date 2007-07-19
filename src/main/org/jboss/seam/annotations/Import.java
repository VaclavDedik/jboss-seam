package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * 
 * @author Gavin King
 *
 */
@Target({TYPE, PACKAGE})
@Retention(RUNTIME)
@Documented
public @interface Import 
{
   String[] value();
}
