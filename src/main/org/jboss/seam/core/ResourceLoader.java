package org.jboss.seam.core;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.InputStream;
import java.net.URL;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.util.Resources;

@Scope(ScopeType.STATELESS)
@BypassInterceptors
@Install(precedence=BUILT_IN)
@Name("org.jboss.seam.core.resourceLoader")
public class ResourceLoader
{
   public InputStream getResourceAsStream(String resource)
   {
      return Resources.getResourceAsStream( resource, ServletLifecycle.getServletContext() );
   }

   public URL getResource(String resource) 
   {
      return Resources.getResource( resource, ServletLifecycle.getServletContext() );
   }
   
   public static ResourceLoader instance()
   {
      return (ResourceLoader) Component.getInstance(ResourceLoader.class, ScopeType.STATELESS);
   }
   
}
