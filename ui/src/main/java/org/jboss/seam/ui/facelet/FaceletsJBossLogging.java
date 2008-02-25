package org.jboss.seam.ui.facelet;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.lang.reflect.Field;
import java.util.logging.Filter;
import java.util.logging.Logger;

import org.apache.log4j.Level;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.interop.jul.Log4JConversionFilter;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Reflections;

import com.sun.facelets.FaceletViewHandler;
import com.sun.facelets.compiler.TagLibraryConfig;
import com.sun.facelets.impl.DefaultFaceletFactory;
import com.sun.facelets.tag.jsf.ComponentHandler;
import com.sun.facelets.util.Resource;

@Name("org.jboss.seam.ui.facelet.faceletsJBossLogging")
@Scope(APPLICATION)
@Install(classDependencies={"com.sun.facelets.Facelet", "org.jboss.logging.Logger", "org.apache.log4j.Logger", "org.jboss.seam.interop.jul.Log4JConversionFilter"}, precedence=BUILT_IN)
@Startup
@BypassInterceptors
public class FaceletsJBossLogging
{
   
   private LogProvider log = Logging.getLogProvider(FaceletsJBossLogging.class);

   public static void setLevel(java.util.logging.Logger julLogger)
   {
       org.apache.log4j.Logger log4jLogger = 
           org.apache.log4j.Logger.getLogger(julLogger.getName());

       julLogger.setLevel(java.util.logging.Level.OFF);

       if (log4jLogger.isEnabledFor(Level.FATAL))
           julLogger.setLevel(java.util.logging.Level.SEVERE);

       if (log4jLogger.isEnabledFor(Level.ERROR))
           julLogger.setLevel(java.util.logging.Level.SEVERE);

       if (log4jLogger.isEnabledFor(Level.WARN))
           julLogger.setLevel(java.util.logging.Level.WARNING);

       if (log4jLogger.isEnabledFor(Level.INFO))
           julLogger.setLevel(java.util.logging.Level.INFO);

       if (log4jLogger.isEnabledFor(Level.DEBUG)) 
           julLogger.setLevel(java.util.logging.Level.FINE);

       if (log4jLogger.isEnabledFor(Level.DEBUG))
           julLogger.setLevel(java.util.logging.Level.FINEST);

       if (log4jLogger.isEnabledFor(Level.ALL))
           julLogger.setLevel(java.util.logging.Level.ALL);
   }
   
   @SuppressWarnings("deprecation")
   @Create
   public void create()
   {
      Filter conversionFilter = null;
      try
      {
         conversionFilter = new Log4JConversionFilter();
      }
      catch (Exception e) 
      {
         // Filter isn't installed in the container
         return;
      }
      try
      {
         
   
         java.util.logging.Logger julLogger;
   
         // Gah have to do this by reflection as the loggers are protected
         
         // And some aren't static, so this really is best effort
         
         julLogger = getPrivateStaticLogger(TagLibraryConfig.class, "log");
         setLevel(julLogger);
         julLogger.setFilter(conversionFilter);
         
         julLogger = getPrivateStaticLogger(com.sun.facelets.compiler.Compiler.class, "log");
         setLevel(julLogger);
         julLogger.setFilter(conversionFilter);
         
         julLogger = getPrivateStaticLogger(DefaultFaceletFactory.class, "log");
         setLevel(julLogger);
         julLogger.setFilter(conversionFilter);
         
         julLogger = getPrivateStaticLogger(TagLibraryConfig.class, "log");
         setLevel(julLogger);
         julLogger.setFilter(conversionFilter);
         
         julLogger = getPrivateStaticLogger(ComponentHandler.class, "log");
         setLevel(julLogger);
         julLogger.setFilter(conversionFilter);
         
         julLogger = getPrivateStaticLogger(Resource.class, "log");
         setLevel(julLogger);
         julLogger.setFilter(conversionFilter);
         
         julLogger = getPrivateStaticLogger(FaceletViewHandler.class, "log");
         setLevel(julLogger);
         julLogger.setFilter(conversionFilter);
         
         // These ones are in a package-scoped class
         
         julLogger = getPrivateStaticLogger("com.sun.facelets.compiler.CompilationManager", "log");
         setLevel(julLogger);
         julLogger.setFilter(conversionFilter);    
         
         julLogger = getPrivateStaticLogger("com.sun.facelets.tag.jsf.ComponentRule", "log");
         setLevel(julLogger);
         julLogger.setFilter(conversionFilter);
         
         julLogger = getPrivateStaticLogger("com.sun.facelets.tag.MetaRulesetImpl", "log");
         setLevel(julLogger);
         julLogger.setFilter(conversionFilter);
         
      }
      catch (Exception e)
      {
         log.warn("Unable to wrap Facelets JDK logging in Log4j logging", e);
      }
   }
   
   private Logger getPrivateStaticLogger(Class clazz, String fieldName) throws Exception 
   {
      Field field = Reflections.getField(clazz, fieldName);
      field.setAccessible(true);
      return (Logger) Reflections.get(field, new Object());
   }
   
   private Logger getPrivateStaticLogger(String className, String fieldName) throws Exception
   {
      return getPrivateStaticLogger(Reflections.classForName(className), fieldName);
   }
   
}
