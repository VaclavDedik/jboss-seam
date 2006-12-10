package org.jboss.seam.tool;

public class Util
{
   public String lower(String name)
   {
      return name.substring(0, 1).toLowerCase() + name.substring(1);
   }
   public String upper(String name)
   {
      return name.substring(0, 1).toUpperCase() + name.substring(1);
   }
}
