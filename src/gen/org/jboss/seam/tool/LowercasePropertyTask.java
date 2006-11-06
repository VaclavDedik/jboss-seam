package org.jboss.seam.tool;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class LowercasePropertyTask extends Task
{
   private String value;
   private String name;
   
   @Override
   public void execute() throws BuildException
   {
      if (value!=null)
      {
         getProject().setProperty( name, lower(value) );
      }
   }

   protected String lower(String name)
   {
      return name.substring(0, 1).toLowerCase() + name.substring(1);
   }

   public void setValue(String packageName)
   {
      this.value = packageName;
   }

   public void setName(String propertyName)
   {
      this.name = propertyName;
   }
}
