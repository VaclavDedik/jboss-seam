package org.jboss.seam.security;

import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.jboss.seam.security.EntitySecurity.Action;

public class HibernateSecurityInterceptor extends EmptyInterceptor
{
   @Override
   public boolean onLoad(Object entity, Serializable id, Object[] state,
                      String[] propertyNames, Type[] types)
   {
      EntitySecurity.check(entity, Action.read);
      return true;
   }
   
   @Override
   public void onDelete(Object entity, Serializable id, Object[] state, 
                        String[] propertyNames, Type[] types)
   {
      EntitySecurity.check(entity, Action.delete);      
   }
   
   @Override
   public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState,
                   Object[] previousState, String[] propertyNames, Type[] types)
   {
      EntitySecurity.check(entity, Action.update);
      return true;
   }
   
   @Override
   public boolean onSave(Object entity, Serializable id, Object[] state,
                      String[] propertyNames, Type[] types)
   {
      EntitySecurity.check(entity, Action.insert);      
      return true;
   }       
}
