package org.jboss.seam.ioc.spring;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Lifecycle;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * A seam component that loads up a spring WebApplicationContext
 * 
 * @author Mike Youngstrom
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup
@Name("org.jboss.seam.ioc.spring.contextLoader")
@Install(value = false, precedence = BUILT_IN)
public class ContextLoader
{
   private WebApplicationContext webApplicationContext;
   private String[] configLocations;
   
   @Create 
   public void create() throws Exception
   {
      ServletContext servletContext = Lifecycle.getServletContext();
      try {
         webApplicationContext = createContextLoader(servletContext);
         servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);
      } catch (Exception e) {
         servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, e);
         throw e;
      }
   }
   
   protected WebApplicationContext createContextLoader(ServletContext servletContext) {
      XmlWebApplicationContext xmlWebApplicationContext = new XmlWebApplicationContext();
      xmlWebApplicationContext.setServletContext(servletContext);
      xmlWebApplicationContext.setConfigLocations(getConfigLocations());
      xmlWebApplicationContext.refresh();
      return xmlWebApplicationContext;
   }

   @Destroy
   public void destroy() {
      if(webApplicationContext != null && webApplicationContext instanceof ConfigurableWebApplicationContext) {
         ((ConfigurableWebApplicationContext)webApplicationContext).close();
      }
   }
   
   public String[] getConfigLocations()
   {
      return configLocations;
   }
   
   public void setConfigLocations(String[] configLocations)
   {
      this.configLocations = configLocations;
   }
}
