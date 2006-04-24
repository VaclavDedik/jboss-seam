package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that the default behavior for an EJB session bean,
 * @TransactionAttribute(REQUIRED), should apply to a JavaBean
 * component or method of a JavaBean component. (JavaBean 
 * components have @TransactionAttribute(SUPPORTS) behavior 
 * otherwise.)
 * 
 * @author Gavin King
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Documented
public @interface Transactional
{

}
