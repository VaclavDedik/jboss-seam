//$Id$
package org.jboss.seam.annotations.datamodel;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.DataBinderClass;
import org.jboss.seam.databinding.DataModelBinder;
import org.jboss.seam.jsf.ListDataModel;
import org.jboss.seam.jsf.MapDataModel;

/**
 * Outjects a collection to the same scope as the owning component
 * (or to the EVENT scope in the case of a stateless component),
 * after wrapping as a JSF DataModel (a List as a ListDataModel, a
 * Map as a MapDataModel, a Set as a SetDataModel, an array as an 
 * ArrayDataModel). Note that the List, Map, Set or array
 * will be re-wrapped and re-outjected each time the current
 * component value is different to the value held by the
 * context variable as determined by calling equals() on the 
 * underlying collection.
 * 
 * @author Gavin King
 * 
 * @see org.jboss.seam.jsf.ListDataModel
 * @see org.jboss.seam.jsf.MapDataModel
 * @see org.jboss.seam.jsf.SetDataModel
 * @see org.jboss.seam.jsf.ArrayDataModel
 */
@Target({FIELD, METHOD})
@Retention(RUNTIME)
@Documented
@DataBinderClass(DataModelBinder.class)
public @interface DataModel
{
   /**
    * The context variable name. Defaults to the name of 
    * the annotated field or getter method.
    */
   String value() default "";
   
   /**
    * By default, the DataModel is outjected to the same
    * scope as the owning component, or to the EVENT scope
    * in the case of a stateless component. If scope=ScopeType.PAGE
    * is explicitly specified, the DataModel will be kept
    * in the PAGE context instead.
    * 
    * @return ScopeType.UNSPECIFIED or ScopeType.PAGE
    */
   ScopeType scope() default ScopeType.UNSPECIFIED;
}
