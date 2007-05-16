package org.jboss.seam.jsf;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.el.FunctionMapper;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.SecurityFunctions;

/**
 * Resolves Seam EL functions to their corresponding methods.
 *  
 * @author Shane Bryzak
 */
public class SeamELFunctionMapper extends FunctionMapper
{
   private static final String SEAM_EL_PREFIX = "s";
   
   private static Map<String,Method> methodCache = new HashMap<String,Method>();
   
   private static final LogProvider log = Logging.getLogProvider(SeamELFunctionMapper.class);
   
   private FunctionMapper functionMapper;
   
   public SeamELFunctionMapper(FunctionMapper functionMapper)
   {
      this.functionMapper = functionMapper;
   }
   
   static 
   {
      cacheMethod("hasPermission", SecurityFunctions.class, "hasPermission", 
               new Class[] {String.class, String.class, Object.class});
      cacheMethod("hasRole", SecurityFunctions.class, "hasRole",
               new Class[] { String.class });      
   }

   @Override 
   public Method resolveFunction(String prefix, String localName) 
   {
      if (SEAM_EL_PREFIX.equals(prefix))
      {
         return methodCache.get(localName);
      }
      else
      {
         return functionMapper.resolveFunction(prefix, localName);
      }
   }  
   
   private static void cacheMethod(String localName, Class cls, String name, Class[] params)
   {
      try
      {
         Method m = cls.getMethod(name, params);
         methodCache.put(localName, m);         
      }
      catch (NoSuchMethodException ex)
      {
         log.warn(String.format("Method %s.%s could not be cached", cls.getName(), name));
      }
   }
   
}
