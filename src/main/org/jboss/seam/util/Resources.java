package org.jboss.seam.util;

import java.io.InputStream;
import java.net.URL;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.jboss.seam.Seam;

public class Resources {

   public static InputStream getResourceAsStream(String resource) {
      String stripped = resource.startsWith("/") ? 
            resource.substring(1) : resource;
   
      InputStream stream = null; 

      try
      {
         if (FacesContext.getCurrentInstance() != null)
         {
            stream = FacesContext.getCurrentInstance().getExternalContext()
               .getResourceAsStream(resource);
         }
      }
      catch (Exception e) {}
      
      if (stream==null)
      {
         stream = getResourceAsStream(resource, stripped);
      }
      
      return stream;
   }

   public static InputStream getResourceAsStream(String resource, ServletContext servletContext) {
      String stripped = resource.startsWith("/") ? 
            resource.substring(1) : resource;
   
      InputStream stream = null; 

      try
      {
         stream = servletContext.getResourceAsStream(resource);
      }
      catch (Exception e) {}
      
      if (stream==null)
      {
         stream = getResourceAsStream(resource, stripped);
      }
      
      return stream;
   }

   private static InputStream getResourceAsStream(String resource, String stripped)
   {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      InputStream stream = null;
      if (classLoader!=null) {
         stream = classLoader.getResourceAsStream(stripped);
      }
      if ( stream == null ) {
         stream = Seam.class.getResourceAsStream(resource);
      }
      if ( stream == null ) {
         stream = Seam.class.getClassLoader().getResourceAsStream(stripped);
      }
      return stream;
   }
   
   public static URL getResource(String resource) 
   {
      String stripped = resource.startsWith("/") ? 
               resource.substring(1) : resource;
      
         URL url = null; 

         try
         {
            url = FacesContext.getCurrentInstance().getExternalContext()
                  .getResource(resource);
         }
         catch (Exception e) {}
         
         if (url==null)
         {
            url = getResource(resource, stripped);
         }
         
         return url;
   }
   
   public static URL getResource(String resource, ServletContext servletContext) {
      String stripped = resource.startsWith("/") ? 
            resource.substring(1) : resource;
   
      URL url  = null; 

      try
      {
         url = servletContext.getResource(resource);
      }
      catch (Exception e) {}
      
      if (url==null)
      {
        url = getResource(resource, stripped);
      }
      
      return url;
   }
   
   private static URL  getResource(String resource, String stripped)
   {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      URL url = null;
      if (classLoader!=null) {
         url = classLoader.getResource(stripped);
      }
      if ( url == null ) {
        url = Seam.class.getResource(resource);
      }
      if ( url == null ) {
         url = Seam.class.getClassLoader().getResource(stripped);
      }
      return url;
   }

}
