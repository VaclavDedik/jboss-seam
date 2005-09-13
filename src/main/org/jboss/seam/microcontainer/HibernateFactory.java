//$Id$
package org.jboss.seam.microcontainer;

import org.hibernate.cfg.AnnotationConfiguration;

/**
 * A factory that bootstraps a Hiberate SessionFactory
 * 
 * @author Gavin King
 */
public class HibernateFactory
{

   private String cfgResourceName;
   
   public Object getSessionFactory() throws Exception
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
      return acfg.buildSessionFactory();      
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
