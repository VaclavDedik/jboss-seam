//$Id$
package org.jboss.seam.annotations.datamodel;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Outjects a List to the same scope as the owning component,
 * after wrapping as a ListDataModel. Note that the List
 * will be re-wrapped and re-outjected each time the current
 * component value is different to the value held by the
 * context variable as determined by calling List.equals().
 * 
 * @author Gavin King
 */
@Target({FIELD, METHOD})
@Retention(RUNTIME)
@Documented
public @interface DataModel
{
   /**
    * The context variable name. Defaults to the name of 
    * the annotated field or getter method.
    */
   String value() default "";
}
