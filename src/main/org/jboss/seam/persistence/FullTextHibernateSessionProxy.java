package org.jboss.seam.persistence;

import org.hibernate.search.FullTextSession;
import org.jboss.seam.util.DelegatingInvocationHandler;

/**
 * Wraps a Hibernate Search session
 * 
 * @author Gavin King
 * @author Shane Bryzak
 *
 */
@SuppressWarnings("deprecation")
public class FullTextHibernateSessionProxy extends DelegatingInvocationHandler<FullTextSession>
{   
   public FullTextHibernateSessionProxy(FullTextSession fullTextSession)
   {
      super(fullTextSession);
   }
}
