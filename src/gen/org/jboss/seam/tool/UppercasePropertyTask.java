package org.jboss.seam.tool;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class UppercasePropertyTask extends Task
{
   private String value;
   private String name;
   
   @Override
   public void execute() throws BuildException
   {
      if ( value!=null && !"".equals(value) )
      {
         getProject().setProperty( name, upper(value) );
      }
   }

   protected String upper(String name)
   {
      return name.substring(0, 1).toUpperCase() + name.substring(1);
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
