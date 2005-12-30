//$Id$
package org.jboss.seam.annotations.datamodel;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Injects the selected row index of a ListDataModel. Intended
 * for use with @DataModel.
 * 
 * @author Gavin King
 */
@Target({FIELD, METHOD})
@Retention(RUNTIME)
@Documented
public @interface DataModelSelectionIndex
{
   /**
    * The context variable name of the DataModel. Defaults 
    * to the name for the outjected @DataModel if there
    * is exactly one @DataModel for the component.
    */
   String value() default "";
}
