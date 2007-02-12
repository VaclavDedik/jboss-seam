package org.jboss.seam.security;

import static org.jboss.seam.security.EntityAction.DELETE;
import static org.jboss.seam.security.EntityAction.INSERT;
import static org.jboss.seam.security.EntityAction.READ;
import static org.jboss.seam.security.EntityAction.UPDATE;

import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

/**
 * Facilitates security checks for Hibernate entities
 * 
 * @author Shane Bryzak
 *
 */
public class HibernateSecurityInterceptor extends EmptyInterceptor
{
   @Override
   public boolean onLoad(Object entity, Serializable id, Object[] state,
                      String[] propertyNames, Type[] types)
   {
      Identity.instance().checkEntityPermission(entity, READ);
      return false;
   }
   
   @Override
   public void onDelete(Object entity, Serializable id, Object[] state, 
                        String[] propertyNames, Type[] types)
   {
      Identity.instance().checkEntityPermission(entity, DELETE);      
   }
   
   @Override
   public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState,
                   Object[] previousState, String[] propertyNames, Type[] types)
   {
      Identity.instance().checkEntityPermission(entity, UPDATE);
      return false;
   }
   
   @Override
   public boolean onSave(Object entity, Serializable id, Object[] state,
                      String[] propertyNames, Type[] types)
   {
      Identity.instance().checkEntityPermission(entity, INSERT);      
      return false;
   }       
}
