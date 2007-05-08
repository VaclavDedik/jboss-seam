//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.persistence.Persistence;

import org.hibernate.cfg.Environment;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.util.Naming;

/**
 * A Seam component that boostraps an EntityManagerFactory,
 * for use of JPA outside of Java EE 5 / Embeddable EJB3.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup(depends={"org.jboss.seam.core.microcontainer", "org.jboss.seam.core.ejb"})
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
   public void startup(Component component) throws Exception
   {
      if (persistenceUnitName==null)
      {
         persistenceUnitName = component.getName();
      }
      
      Map properties = new HashMap();
      Hashtable<String, String> jndiProperties = Naming.getInitialContextProperties();
      if ( jndiProperties!=null )
      {
         // Prefix regular JNDI properties for Hibernate
         for (Map.Entry<String, String> entry : jndiProperties.entrySet())
         {
            properties.put( Environment.JNDI_PREFIX + "." + entry.getKey(), entry.getValue() );
         }
      }
      if (persistenceUnitProperties!=null)
      {
         properties.putAll(persistenceUnitProperties);
      }

      if ( properties.isEmpty() )
      {
         entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
      }
      else
      {
         entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
      }
   }
   
   @Destroy
   public void shutdown()
   {
      if (entityManagerFactory!=null)
      {
         entityManagerFactory.close();
      }
   }
   
   /**
    * The persistence unit name
    */
   public String getPersistenceUnitName()
   {
      return persistenceUnitName;
   }

   public void setPersistenceUnitName(String persistenceUnitName)
   {
      this.persistenceUnitName = persistenceUnitName;
   }

   /**
    * Properties to pass to Persistence.createEntityManagerFactory()
    */
   public Map getPersistenceUnitProperties()
   {
      return persistenceUnitProperties;
   }

   public void setPersistenceUnitProperties(Map persistenceUnitProperties)
   {
      this.persistenceUnitProperties = persistenceUnitProperties;
   }

}
