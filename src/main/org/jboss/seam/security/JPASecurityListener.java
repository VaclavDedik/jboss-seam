package org.jboss.seam.security;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.jboss.seam.security.EntitySecurity.Action;

/**
 * Facilitates security checks for entity beans.
 * 
 * @author Shane Bryzak
 */
public class JPASecurityListener
{
   @PostLoad
   public void postLoad(Object entity)
   {
      EntitySecurity.check(entity, Action.read);
   }
   
   @PrePersist
   public void prePersist(Object entity)
   { 
      EntitySecurity.check(entity, Action.insert);
   }
   
   @PreUpdate
   public void preUpdate(Object entity)
   {
      EntitySecurity.check(entity, Action.update);
   }
   
   @PreRemove
   public void preRemove(Object entity)
   {
      EntitySecurity.check(entity, Action.delete);
   }
}
