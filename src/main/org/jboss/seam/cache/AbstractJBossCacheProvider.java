package org.jboss.seam.cache;

import org.jboss.cache.Fqn;

public abstract class AbstractJBossCacheProvider<T> extends CacheProvider<T>
{
   
   public AbstractJBossCacheProvider()
   {
      super.setConfiguration("treecache.xml");
   }
   
   private Fqn defaultFqn = Fqn.fromString(defaultRegion);
   
   protected Fqn getFqn(String region)
   {
      if (region != null)
      {
         return Fqn.fromString(region);
      }
      else
      {
         return defaultFqn;
      }
   }
   
   @Override
   public void setDefaultRegion(String defaultRegion)
   {
      super.setDefaultRegion(defaultRegion);
      this.defaultFqn = Fqn.fromString(defaultRegion);
   }

}