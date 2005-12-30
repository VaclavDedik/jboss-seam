//$Id$
package org.jboss.seam.annotations.datamodel;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD, METHOD})
@Retention(RUNTIME)
@Documented
public @interface DataModelSelection
{
   String value() default "";
}
