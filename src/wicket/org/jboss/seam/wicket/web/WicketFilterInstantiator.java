/**
 * 
 */
package org.jboss.seam.wicket.web;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.HashMap;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.apache.wicket.protocol.http.WicketFilter;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.util.Reflections;
import org.jboss.seam.web.FilterConfigWrapper;
import org.jboss.seam.wicket.WebApplication;
import org.jboss.seam.wicket.SeamWebApplication;
import org.jboss.seam.wicket.ioc.JavassistInstrumentor;

@Name("org.jboss.seam.wicket.web.wicketFilterInstantiator")
@Install(precedence = BUILT_IN, classDependencies={"org.apache.wicket.Application"})
@BypassInterceptors
@Scope(ScopeType.STATELESS)
public class WicketFilterInstantiator
{
   
   @Unwrap
   public Filter unrwap()
   {
      return new WicketFilter()
      {
         
         private ClassLoader classLoader;
         
         @Override
         public void init(FilterConfig filterConfig) throws ServletException
         {
            Map<String, String> parameters = new HashMap<String, String>();
            try
            {
               JavassistInstrumentor javassistInstrumentor = new JavassistInstrumentor(filterConfig.getServletContext());
               javassistInstrumentor.instrument();
               classLoader = javassistInstrumentor.getClassLoader();
            }
            catch (NotFoundException e)
            {
               throw new ServletException(e);
            }
            catch (CannotCompileException e)
            {
               throw new ServletException(e);
            }
            catch (ClassNotFoundException e)
            {
               throw new ServletException(e);
            }
            if (filterConfig.getInitParameter("applicationClassName") == null)
            {
               String applicationClass = WebApplication.instance().getApplicationClass();
               if (applicationClass != null)
               {
                  parameters.put("applicationClassName", applicationClass); 
               }
               else
               {
                  throw new IllegalStateException("Must set application-class using <wicket:web-application /> in components.xml");
               }
            }
            super.init(new FilterConfigWrapper(filterConfig, parameters));
         }
         
         @Override
         protected ClassLoader getClassLoader()
         {
            return classLoader;
         }
         
      };
   }

}
