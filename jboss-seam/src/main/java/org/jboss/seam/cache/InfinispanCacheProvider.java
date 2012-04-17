package org.jboss.seam.cache;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.lang.reflect.Method;

import org.infinispan.Cache;
import org.infinispan.tree.Fqn;
import org.infinispan.tree.TreeCache;
import org.infinispan.tree.TreeCacheFactory;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Reflections;

/**
 * Implementation of CacheProvider backed by Infinispan 5.x. for simple objects.
 * 
 * @author Marek Novotny
 */

@Name("org.jboss.seam.cache.cacheProvider")
@Scope(APPLICATION)
@BypassInterceptors
@Install(value = false, precedence = BUILT_IN, classDependencies = { "org.infinispan.tree.TreeCache", "org.jgroups.MembershipListener" })
@AutoCreate
@SuppressWarnings("rawtypes")
public class InfinispanCacheProvider extends AbstractInfinispanCacheProvider<TreeCache<Object, Object>>
{

   private org.infinispan.tree.TreeCache cache;

   private static final LogProvider log = Logging.getLogProvider(InfinispanCacheProvider.class);

   private static Method GET;
   private static Method PUT;
   private static Method REMOVE;
   private static Method REMOVE_NODE;

   static
   {
      try
      {
         GET = TreeCache.class.getDeclaredMethod("get", Fqn.class, Object.class);
         PUT = TreeCache.class.getDeclaredMethod("put", Fqn.class, Object.class, Object.class);
         REMOVE = TreeCache.class.getDeclaredMethod("remove", Fqn.class, Object.class);
         REMOVE_NODE = TreeCache.class.getDeclaredMethod("removeNode", Fqn.class);
      }
      catch (Exception e)
      {
         log.error(e);
         throw new IllegalStateException("Unable to use Infinispan Cache", e);
      }
   }

   @SuppressWarnings("unchecked")
   @Create
   public void create()
   {
      log.debug("Starting Infinispan Cache");

      try
      {
         DefaultCacheManager manager = new DefaultCacheManager(getConfigurationAsStream());        
         Cache defaultCache = manager.getCache();
         cache = new TreeCacheFactory().createTreeCache(defaultCache);
      }
      catch (Exception e)
      {
         throw new IllegalStateException("Error starting Infinispan Cache", e);
      }
   }

   @Destroy
   public void destroy()
   {
      log.debug("Stopping Infinispan Cache");
      try
      {
         cache.stop();
         cache = null;
      }
      catch (Exception e)
      {
         throw new IllegalStateException("Error stopping Infinispan Cache", e);
      }
   }

   @Override
   public Object get(String region, String key)
   {
      return Reflections.invokeAndWrap(GET, cache, getFqn(region), key);
   }

   @Override
   public void put(String region, String key, Object object)
   {
      Reflections.invokeAndWrap(PUT, cache, getFqn(region), key, object);
   }

   @Override
   public void remove(String region, String key)
   {
      Reflections.invokeAndWrap(REMOVE, cache, getFqn(region), key);
   }

   @Override
   public void clear()
   {
      Reflections.invokeAndWrap(REMOVE_NODE, cache, getFqn(null));
   }

   @Override
   public TreeCache getDelegate()
   {
      return cache;
   }

}