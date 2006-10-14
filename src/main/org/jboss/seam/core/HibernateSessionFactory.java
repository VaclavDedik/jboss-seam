//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import org.hibernate.SessionFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.microcontainer.HibernateFactory;

/**
 * A Seam component that boostraps a Hibernate SessionFactory
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup(depends="org.jboss.seam.core.microcontainer")
@Name("org.jboss.seam.core.hibernate") //this usage is deprecated, install it via components.xml
public class HibernateSessionFactory extends HibernateFactory
{

   private SessionFactory sessionFactory;
   
   @Unwrap
   public SessionFactory getSessionFactory()
   {
      return sessionFactory;
   }
   
   @Create
   public void startup() throws Exception
   {
      sessionFactory = createSessionFactory();
   }
   
   @Destroy
   public void shutdown()
   {
      sessionFactory.close();
   }

}
