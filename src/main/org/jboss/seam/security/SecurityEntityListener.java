package org.jboss.seam.security;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Contexts;

public class SecurityEntityListener
{
   @PrePersist
   public void prePersist(Object entity)
   { 
      entitySecurityCheck(entity, "insert");
   }
   
   @PreUpdate
   public void preUpdate(Object entity)
   {
      entitySecurityCheck(entity, "update");
   }
   
   @PreRemove
   public void preRemove(Object entity)
   {
      entitySecurityCheck(entity, "delete");
   }
   
   protected void entitySecurityCheck(Object entity, String action)
   {
      String name = Seam.getComponentName(entity.getClass());
      if (name == null) name = entity.getClass().getName();
      
      Contexts.getMethodContext().set("entity", entity);
      String expr = String.format("#{s:hasPermission('%s', '%s', entity)}",
               name, action);
      
      Identity.instance().checkRestriction(expr);
   }
}
