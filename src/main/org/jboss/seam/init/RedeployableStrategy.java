//$Id$
package org.jboss.seam.init;

import java.io.File;

import org.jboss.seam.deployment.ComponentScanner;
import org.jboss.seam.deployment.Scanner;

/**
 * Abstract the redeployable initialization mechanism
 * to prevent hard dependency between Seam and the
 * scripting language infrastructure
 *
 * @author Emmanuel Bernard
 */
public interface RedeployableStrategy
{
   /**
    * Mandatory constructor
    *
    * @param resource url containing the redeployable files
    */
   //RedeployableInitialization(URL resource);


   ClassLoader getClassLoader();

   File[] getPaths();

   ComponentScanner getScanner();

   boolean isFromHotDeployClassLoader(Class componentClass);
}
