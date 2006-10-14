//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.Map;

import javax.persistence.Persistence;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;

/**
 * A Seam component that boostraps an EntityManagerFactory,
 * for use of JPA outside of Java EE 5 / Embeddable EJB3.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup(depends="org.jboss.seam.core.microcontainer")
@Name("org.jboss.seam.core.jpa") //this usage is deprecated, install it via components.xml
public class EntityManagerFactory
{

   private String persistenceUnitName;
   private Map persistenceUnitProperties;
   private javax.persistence.EntityManagerFactory entityManagerFactory;
   
   @Unwrap
   public javax.persistence.EntityManagerFactory getEntityManagerFactory()
   {
      return entityManagerFactory;
   }
   
   @Create
   public void startup() throws Exception
   {
      if (persistenceUnitProperties==null)
      {
         entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
      }
      else
      {
         entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName, persistenceUnitProperties);
      }
   }
   
   @Destroy
   public void shutdown()
   {
      entityManagerFactory.close();
   }
   
   /**
    * The persistence unit name
    */
   protected String getPersistenceUnitName()
   {
      return persistenceUnitName;
   }

   protected void setPersistenceUnitName(String persistenceUnitName)
   {
      this.persistenceUnitName = persistenceUnitName;
   }

   /**
    * Properties to pass to Persistence.createEntityManagerFactory()
    */
   protected Map getPersistenceUnitProperties()
   {
      return persistenceUnitProperties;
   }

   protected void setPersistenceUnitProperties(Map persistenceUnitProperties)
   {
      this.persistenceUnitProperties = persistenceUnitProperties;
   }

}
