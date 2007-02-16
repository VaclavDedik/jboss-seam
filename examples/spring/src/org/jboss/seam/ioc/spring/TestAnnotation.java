/**
 *
 */
package org.jboss.seam.example.spring;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author youngm
 *
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface TestAnnotation {

}
