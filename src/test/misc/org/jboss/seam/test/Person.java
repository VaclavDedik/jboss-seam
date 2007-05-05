package org.jboss.seam.test;

import org.jboss.seam.annotations.Name;


@Name("user")
public class Person
{
   
   private String name;
   
   public String getName()
   {
      return name;
   }
   
    public void setName(String name)
   {
      this.name = name;
   }

}
