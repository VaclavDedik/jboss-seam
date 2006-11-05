package org.jboss.seam.tool;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class PackageDirectoryTask extends Task
{
   private String packageName;
   private String propertyName;
   
   @Override
   public void execute() throws BuildException
   {
      this.getProject().setProperty( propertyName, packageName.replace('.', '/') );
      super.execute();
   }

   public void setPackage(String packageName)
   {
      this.packageName = packageName;
   }

   public void setProperty(String propertyName)
   {
      this.propertyName = propertyName;
   }
}
