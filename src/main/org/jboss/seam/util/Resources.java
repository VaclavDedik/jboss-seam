package org.jboss.seam.util;

import java.io.InputStream;

import javax.faces.context.FacesContext;

import org.jboss.seam.Seam;

public class Resources {

   public static InputStream getResourceAsStream(String resource) {
      String stripped = resource.startsWith("/") ? 
            resource.substring(1) : resource;
   
      InputStream stream = null; 

      try
      {
         stream = FacesContext.getCurrentInstance().getExternalContext()
               .getResourceAsStream(resource);
      }
      catch (Exception e) {}
      
      if (stream==null)
      {
         ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
         if (classLoader!=null) {
            stream = classLoader.getResourceAsStream( stripped );
         }
         if ( stream == null ) {
            Seam.class.getResourceAsStream( resource );
         }
         if ( stream == null ) {
            stream = Seam.class.getClassLoader().getResourceAsStream( stripped );
         }
      }
      
      return stream;
   }

}
