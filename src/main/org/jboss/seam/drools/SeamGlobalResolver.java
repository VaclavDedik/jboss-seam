package org.jboss.seam.drools;

import org.drools.spi.GlobalResolver;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Init;

/**
 * Resolves Seam context variables as Drools globals
 * 
 * @author Gavin King
 *
 */
class SeamGlobalResolver implements GlobalResolver
{
   public Object resolve(String name)
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         //TODO: Drools should let us chain GlobalResolvers
         //return resolver.resolve(name)
         return null;
      }
      else
      {
         Object instance = Component.getInstance(name);
         if (instance==null)
         {
            /*instance = resolver.resolve(name);
            if (instance==null)
            {*/
               return Init.instance().getRootNamespace().getChild(name);
            /*}
            else
            {
               return instance;
            }*/
         }
         else
         {
            return instance;
         }
      }
   }
}