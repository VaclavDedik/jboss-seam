//$Id$
package org.jboss.seam;

import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.ScopeType.EVENT;
import static org.jboss.seam.ScopeType.STATELESS;

/**
 * The types of components understood by seam
 * @author Gavin King
 */
public enum ComponentType
{
   ENTITY_BEAN, 
   STATELESS_SESSION_BEAN,
   STATEFUL_SESSION_BEAN,
   JAVA_BEAN;
   
   public boolean isEjb()
   {
      return this!=JAVA_BEAN;
   }
   
   public ScopeType getDefaultScope()
   {
      switch (this)
      {
         case STATEFUL_SESSION_BEAN:
         case ENTITY_BEAN:
            return CONVERSATION;
         case STATELESS_SESSION_BEAN:
            return STATELESS;
         case JAVA_BEAN:
            return EVENT;
         default:
            throw new IllegalStateException();
      }
   }

}
