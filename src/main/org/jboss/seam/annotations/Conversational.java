//$Id$
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that a component or method is conversational, 
 * and may only be called inside the scope of a long-running 
 * conversation.
 * 
 * @author Gavin King
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Documented
@Inherited
public @interface Conversational
{
   /**
    * The JSF outcome if the component is invoked outside
    * of the scope of its conversation during the invoke
    * application phase.
    * 
    * @deprecated use no-conversation-view-id in pages.xml
    */
   String ifNotBegunOutcome() default "";
   /**
    * If true, the component must be the initiator of the
    * conversation. That is, the conversation must have
    * begun by a call to some @Begin method of this
    * component. If false, the component or method may be 
    * called inside a conversation begun by any component.
    * 
    * @deprecated use no-conversation-view-id in pages.xml
    */
   boolean initiator() default false;
}
