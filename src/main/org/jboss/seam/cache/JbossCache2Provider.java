package org.jboss.seam.cache;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import org.jboss.cache.Cache;
import org.jboss.cache.CacheFactory;
import org.jboss.cache.DefaultCacheFactory;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Implementation of CacheProvider backed by JBoss Cache 2.x.
 * for simple objects.
 * 
 * @author Sebastian Hennebrueder
 * @author Pete Muir
 */

@Name("org.jboss.seam.cache.cacheProvider")
@Scope(APPLICATION)
@BypassInterceptors
@Install(precedence = BUILT_IN, classDependencies="org.jboss.cache.Cache")
@AutoCreate
public class JbossCache2Provider extends AbstractJBossCacheProvider<Cache>
{

   private org.jboss.cache.Cache cache;

   private static final LogProvider log = Logging.getLogProvider(JbossCache2Provider.class);

   @Create
   public void create()
   {
      log.debug("Starting JBoss Cache");

      try
      {
         CacheFactory factory = new DefaultCacheFactory();
         cache = factory.createCache(getConfigurationAsStream());

         cache.create();
         cache.start();
      }
      catch (Exception e)
      {
         log.error(e, e);
         throw new IllegalStateException("Error starting JBoss Cache", e);
      }
   }

   @Destroy
   public void destroy()
   {
      log.debug("Stopping JBoss Cache");
      try
      {
         cache.stop();
         cache.destroy();
         cache = null;
      }
      catch (Exception e)
      {
         throw new IllegalStateException("Error stopping JBoss Cache", e);
      }
   }

   @Override
   public Object get(String region, String key)
   {
      return cache.get(getFqn(region), key);
   }

   @Override
   public void put(String region, String key, Object object)
   {
      cache.put(getFqn(region), key, object);
   }

   @Override
   public void remove(String region, String key)
   {
      cache.remove(getFqn(region), key);
   }

   @Override
   public void clear()
   {
      cache.removeNode(getFqn(null));
   }

   @Override
   public Cache getDelegate()
   {
      return cache;
   }

}