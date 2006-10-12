package org.jboss.seam.jsf;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;

public class SeamApplicationFactory extends ApplicationFactory
{
   
   private final ApplicationFactory delegate;
   private boolean isJsf12;
   
   public SeamApplicationFactory(ApplicationFactory af)
   {
      delegate = af;
      try
      {
         delegate.getApplication().getClass().getMethod("getELResolver");
         isJsf12 = true;
      }
      catch (NoSuchMethodException nsme)
      {
         isJsf12 = false; 
      }
   }

   @Override
   public Application getApplication()
   {
      return isJsf12 ? 
            new SeamApplication12( delegate.getApplication() ) : 
            new SeamApplication11( delegate.getApplication() );
   }

   @Override
   public void setApplication(Application application)
   {
      delegate.setApplication(application);
   }

}
