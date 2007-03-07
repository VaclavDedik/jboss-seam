package org.jboss.seam.debug.hot;

import static org.jboss.seam.InterceptionType.NEVER;
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
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.core.Init;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.web.AbstractFilter;

@Name("org.jboss.seam.debug.hotDeployFilter")
@Startup
@Install(precedence = BUILT_IN)
@Intercept(NEVER)
@Scope(APPLICATION)
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
               new Initialization( getServletContext() ).redeploy( ( (HttpServletRequest) request ).getSession(true) );
               break;
            }
         }
      }
      chain.doFilter(request, response);
   }

   private boolean scan(ServletRequest request, Init init, File file)
   {
      if ( file.isFile() )
      {
         if ( log.isDebugEnabled() )
         {
            log.debug( "file updated: " + file.getName() );
         }
         if ( file.lastModified() > init.getTimestamp() )
         {
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
