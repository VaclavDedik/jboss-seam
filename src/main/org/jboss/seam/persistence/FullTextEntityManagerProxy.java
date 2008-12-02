//$Id$
package org.jboss.seam.persistence;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.jboss.seam.util.DelegatingInvocationHandler;

/**
 * Wrap a FullTextEntityManager
 *
 * @author Emmanuel Bernard
 * @author Shane Bryzak
 */
public class FullTextEntityManagerProxy extends DelegatingInvocationHandler<FullTextEntityManager>
{
   public FullTextEntityManagerProxy(FullTextEntityManager entityManager)
   {
      super(entityManager);
   }
}
