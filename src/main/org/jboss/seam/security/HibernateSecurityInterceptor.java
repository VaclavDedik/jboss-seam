package org.jboss.seam.security;

import static org.jboss.seam.security.EntityAction.DELETE;
import static org.jboss.seam.security.EntityAction.INSERT;
import static org.jboss.seam.security.EntityAction.READ;
import static org.jboss.seam.security.EntityAction.UPDATE;

import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;

/**
 * Facilitates security checks for Hibernate entities
 * 
 * @author Shane Bryzak
 *
 */
public class HibernateSecurityInterceptor extends EmptyInterceptor
{
   private Interceptor wrappedInterceptor;
   
   public HibernateSecurityInterceptor(Interceptor wrappedInterceptor)
   {
      this.wrappedInterceptor = wrappedInterceptor;
   }
   
   @Override
   public boolean onLoad(Object entity, Serializable id, Object[] state,
                      String[] propertyNames, Type[] types)
   {
      Identity.instance().checkEntityPermission(entity, READ);
      return wrappedInterceptor != null ? 
               wrappedInterceptor.onLoad(entity, id, state, propertyNames, types) : 
               false;
   }
   
   @Override
   public void onDelete(Object entity, Serializable id, Object[] state, 
                        String[] propertyNames, Type[] types)
   {
      Identity.instance().checkEntityPermission(entity, DELETE);   
      if (wrappedInterceptor != null)
         wrappedInterceptor.onDelete(entity, id, state, propertyNames, types);
   }
   
   @Override
   public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState,
                   Object[] previousState, String[] propertyNames, Type[] types)
   {
      Identity.instance().checkEntityPermission(entity, UPDATE);
      return wrappedInterceptor != null ? 
               wrappedInterceptor.onFlushDirty(entity, id, currentState, 
                        previousState, propertyNames, types) : false;
   }
   
   @Override
   public boolean onSave(Object entity, Serializable id, Object[] state,
                      String[] propertyNames, Type[] types)
   {
      Identity.instance().checkEntityPermission(entity, INSERT);      
      return wrappedInterceptor != null ? 
               wrappedInterceptor.onSave(entity, id, state, propertyNames, types) : 
               false;
   }       
}
