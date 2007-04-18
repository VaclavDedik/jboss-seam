//$Id$
package org.jboss.seam.init;

import java.io.File;
import java.net.URL;

import org.jboss.seam.deployment.ComponentScanner;

/**
 * No hot deployment environment
 * 
 * @author Emmanuel Bernard
 */
public class NoHotRedeployable implements RedeployableStrategy
{
   public NoHotRedeployable(URL resource) {
   }

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
