//$Id$
package org.jboss.seam.init;

import groovy.lang.GroovyClassLoader;

import java.net.URL;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.jboss.seam.deployment.ComponentScanner;
import org.jboss.seam.deployment.GroovyComponentScanner;

/**
 * Support Groovy file loading as well as Java class loading
 * from hot directory
 *
 * @author Emmanuel Bernard
 */
public class GroovyHotRedeployable extends JavaHotRedeployable
{
   private static final String DEFAULT_SCRIPT_EXTENSION = new CompilerConfiguration().getDefaultScriptExtension();

   public GroovyHotRedeployable(URL resource)
   {
      super(resource);
      /**
       * No need for the Groovy Hotdeploy capability since the parent classloader needs
       * to be replaced to hot deploy classes
       */
      if (classLoader != null) classLoader = new GroovyClassLoader(classLoader);
   }

   @Override
   public ComponentScanner getScanner()
   {
      return classLoader != null ?
            new GroovyComponentScanner(null, (GroovyClassLoader) getClassLoader(), DEFAULT_SCRIPT_EXTENSION) :
            null;
   }

   @Override
   public boolean isFromHotDeployClassLoader(Class componentClass)
   {
      //loaded by groovy or java
      if (classLoader == null) return false;
      ClassLoader classClassLoader = componentClass.getClassLoader().getParent(); //Groovy use an Inner Delegate CL
      return classClassLoader == classLoader || classClassLoader == classLoader.getParent();
   }
}
