package org.jboss.seam.transaction;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import javax.naming.NamingException;

import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Expressions.ValueExpression;

/**
 * Support for Hibernate Transaction API
 * 
 * @author Gavin King
 * 
 */
@Name("org.jboss.seam.transaction.transaction")
@Scope(ScopeType.STATELESS)
@Install(value=false, precedence=FRAMEWORK)
@BypassInterceptors
public class HibernateTransaction extends Transaction
{

   private ValueExpression<Session> session;
   
   @Unwrap
   @Override
   public UserTransaction getTransaction() throws NamingException
   {
      Session s = session.getValue();
      if ( s==null )
      {
         return createNoTransaction();
      }
      else
      {
         return createHTransaction(s);
      }
   }

   protected UserTransaction createHTransaction(Session session)
   {
      return new HTransaction( session.getTransaction() );
   }

   public ValueExpression<Session> getSession()
   {
      return session;
   }

   public void setSession(ValueExpression<Session> entityManager)
   {
      this.session = entityManager;
   }

}
