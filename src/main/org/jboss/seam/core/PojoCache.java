//$Id$
package org.jboss.seam.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.cache.PropertyConfigurator;
import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.contexts.Contexts;

@Name("org.jboss.seam.core.pojoCache")
@Scope(ScopeType.APPLICATION)
@Intercept(InterceptionType.NEVER)
@Install(false)
public class PojoCache 
{

   private static final Log log = LogFactory.getLog(PojoCache.class);

   private org.jboss.cache.aop.PojoCache cache;
   private String cfgResourceName = "treecache.xml";

   @Create
   public void start() throws Exception 
   {
      log.debug("starting JBoss Cache");
      cache = new org.jboss.cache.aop.PojoCache();
      new PropertyConfigurator().configure(cache, cfgResourceName);
      cache.start();
   }
   
   @Destroy
   public void stop() 
   {
      log.debug("stopping JBoss Cache");
      cache.stop();
      cache.destroy();
      cache = null;
   }

   @Unwrap
   public org.jboss.cache.aop.PojoCache getCache() 
   {
      return cache;
   }

   public String getCfgResourceName()
   {
      return cfgResourceName;
   }

   public void setCfgResourceName(String cfgResourceName)
   {
      this.cfgResourceName = cfgResourceName;
   }

   public static org.jboss.cache.aop.PojoCache instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application scope");
      }
      return (org.jboss.cache.aop.PojoCache) Component.getInstance(PojoCache.class, ScopeType.APPLICATION);
   }
   
}
