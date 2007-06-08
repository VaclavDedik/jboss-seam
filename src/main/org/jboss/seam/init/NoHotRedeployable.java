//$Id$
package org.jboss.seam.init;

import java.io.File;

import org.jboss.seam.deployment.ComponentScanner;

/**
 * No hot deployment environment
 * 
 * @author Emmanuel Bernard
 */
public class NoHotRedeployable implements RedeployableStrategy
{
   public NoHotRedeployable(File resource) {}
   
   public NoHotRedeployable() {}

   public ClassLoader getClassLoader()
   {
      return null;
   }

   public File[] getPaths()
   {
      return null;
   }

   public ComponentScanner getScanner()
   {
      return null;
   }

   public boolean isFromHotDeployClassLoader(Class componentClass)
   {
      return false;
   }
}
