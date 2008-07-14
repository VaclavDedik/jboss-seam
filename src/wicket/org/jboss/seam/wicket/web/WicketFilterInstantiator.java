/**
 * 
 */
package org.jboss.seam.wicket.web;

import static org.jboss.seam.annotations.Install.BUILT_IN;
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
         public void init(final FilterConfig filterConfig) throws ServletException
         {
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
            super.init(filterConfig);
         }
         
         @Override
         protected ClassLoader getClassLoader()
         {
            return classLoader;
         }
         
      };
   }

}
