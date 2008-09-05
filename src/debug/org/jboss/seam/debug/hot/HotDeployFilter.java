package org.jboss.seam.debug.hot;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.File;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.core.Init;
import org.jboss.seam.exception.Exceptions;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.web.AbstractFilter;

@Name("org.jboss.seam.debug.hotDeployFilter")
@Install(debug=true, precedence=BUILT_IN)
@BypassInterceptors
@Scope(APPLICATION)
@Filter
public class HotDeployFilter extends AbstractFilter
{

   private static LogProvider log = Logging.getLogProvider(HotDeployFilter.class);
   
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
   {
      Init init = (Init) getServletContext().getAttribute( Seam.getComponentName(Init.class) );
      if ( init!=null && init.hasHotDeployableComponents() )
      {
         for ( File file: init.getHotDeployPaths() )
         {
            if ( scan(request, init, file) )
            {
               Seam.clearComponentNameCache();
               new Initialization( getServletContext() ).redeploy( (HttpServletRequest) request );
               break;
            }
         }
      }
      
      //TODO: check the timestamp, for a minor optimization
      // instead 
      Pages pages = (Pages) getServletContext().getAttribute(Seam.getComponentName(Pages.class));
      if (pages!= null) {
          pages.initialize();
      }
      
      getServletContext().removeAttribute( Seam.getComponentName(Exceptions.class) );
      
      //TODO: is there anything we should remove from the session scope?
      /*HttpSession session = ( (HttpServletRequest) request ).getSession(false);
      if (session!=null)
      {
         session.removeAttribute( ... );
      }*/
      
      chain.doFilter(request, response);
   }

   private boolean scan(ServletRequest request, Init init, File file)
   {
      if ( file.isFile() )
      {
         if ( !file.exists() || ( file.lastModified() > init.getTimestamp() ) )
         {
            if ( log.isDebugEnabled() )
            {
               log.debug( "file updated: " + file.getName() );
            }
            return true;
         }
      }
      else if ( file.isDirectory() )
      {
         for ( File f: file.listFiles() )
         {
            if ( scan(request, init, f) )
            {
               return true;
            }
         }
      }
      return false;
   }

}
