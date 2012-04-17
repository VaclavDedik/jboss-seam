package org.jboss.seam.cache;

import org.infinispan.tree.Fqn;

public abstract class AbstractInfinispanCacheProvider<T> extends CacheProvider<T>
{
   
   public AbstractInfinispanCacheProvider()
   {
      super.setConfiguration("infinispan.xml");
   }
   
   private Fqn defaultFqn;
   
   protected Fqn getFqn(String region)
   {
      if (region != null)
      {
         return Fqn.fromString(region);
      }
      else
      {
         if (defaultFqn == null)
         {
            defaultFqn = Fqn.fromString(getDefaultRegion());
         }
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