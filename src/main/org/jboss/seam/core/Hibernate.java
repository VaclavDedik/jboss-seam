//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.util.Naming;

/**
 * A seam component that boostraps a Hiberate SessionFactory
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup(depends="org.jboss.seam.core.microcontainer")
@Name("org.jboss.seam.core.hibernate")
public class Hibernate
{

   private SessionFactory sessionFactory;
   
   private String cfgResourceName;
   
   @Unwrap
   public SessionFactory getSessionFactory()
   {
      return sessionFactory;
   }
   
   @Create
   public void startup() throws Exception
   {
      
      AnnotationConfiguration acfg = new AnnotationConfiguration();
      if (cfgResourceName==null) 
      {
         acfg.configure();
      }
      else
      {
         acfg.configure(cfgResourceName);
      }

      // Prefix regular JNDI properties for Hibernate
      Hashtable<String, String> hash = Naming.getInitialContextProperties();
      Properties prefixed = new Properties();
      for (Map.Entry<String, String> entry: hash.entrySet() )
      {
         prefixed.setProperty( Environment.JNDI_PREFIX + "." + entry.getKey(), entry.getValue() );
      }

      acfg.getProperties().putAll(prefixed);
      
      sessionFactory = acfg.buildSessionFactory();
      
   }
   
   @Destroy
   public void shutdown()
   {
      sessionFactory.close();
   }

   public String getCfgResourceName()
   {
      return cfgResourceName;
   }

   public void setCfgResourceName(String cfgFileName)
   {
      this.cfgResourceName = cfgFileName;
   }

}
