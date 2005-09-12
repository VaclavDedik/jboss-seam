//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;

/**
 * A seam component that boostraps a Hiberate SessionFactory
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup(depends={"org.jboss.seam.core.jndi", "org.jboss.seam.core.jta"})
@Name("org.jboss.seam.core.hibernate")
public class Hibernate
{
   private static final Logger log = Logger.getLogger(Hibernate.class);

   private SessionFactory sf;
   
   private String cfgResourceName;
   private String dataSourceName;
   
   @Unwrap
   public SessionFactory getSessionFactory()
   {
      return sf;
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
      
      //force datasource startup
      if (dataSourceName==null)
      {
         dataSourceName = acfg.getProperty(Environment.DATASOURCE);
      }
      log.info("Starting Hibernate, using datasource: " + dataSourceName);
      Component.getInstance( dataSourceName, true );
      
      sf = acfg.buildSessionFactory();
      
   }
   
   @Destroy
   public void shutdown()
   {
      sf.close();
   }

   public String getCfgResourceName()
   {
      return cfgResourceName;
   }

   public void setCfgResourceName(String cfgFileName)
   {
      this.cfgResourceName = cfgFileName;
   }

   public String getDataSourceName()
   {
      return dataSourceName;
   }

   public void setDataSourceName(String dataSourceName)
   {
      this.dataSourceName = dataSourceName;
   }

}
