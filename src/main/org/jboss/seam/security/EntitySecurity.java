package org.jboss.seam.security;

import org.jboss.seam.Seam;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;

public class EntitySecurity
{
   public enum Action { read, insert, update, delete }
   
   public static void check(Object entity, Action action)
   {
      if (!entity.getClass().isAnnotationPresent(Restrict.class))
         return;

      String name = Seam.getComponentName(entity.getClass());
      if (name == null) name = entity.getClass().getName();
      
      Contexts.getMethodContext().set("entity", entity);
      String expr = String.format("#{s:hasPermission('%s', '%s', entity)}",
               name, action);
      
      Identity.instance().checkRestriction(expr);
   }
}
