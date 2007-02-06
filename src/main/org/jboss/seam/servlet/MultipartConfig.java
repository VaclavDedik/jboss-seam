package org.jboss.seam.servlet;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Contexts;

/**
 * Contains configuration options for processing multipart requests
 * 
 * @author Shane Bryzak
 */
@Startup
@Scope(APPLICATION)
@Name("org.jboss.seam.multipartConfig")
@Install(precedence=BUILT_IN)
@Intercept(NEVER)
public class MultipartConfig
{
   private boolean createTempFiles = false;
   private int maxRequestSize = 0; // no limit
   
   public static MultipartConfig instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application scope");
      }
      return (MultipartConfig) Component.getInstance(MultipartConfig.class);     
   }
   
   public boolean getCreateTempFiles()
   {
      return createTempFiles;
   }
   
   public void setCreateTempFiles(boolean createTempFiles)
   {
      this.createTempFiles = createTempFiles;
   }
   
   public int getMaxRequestSize()
   {
      return maxRequestSize;
   }
   
   public void setMaxRequestSize(int maxFileSize)
   {
      this.maxRequestSize = maxFileSize;
   }
}
