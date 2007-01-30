package org.jboss.seam.security;

import java.io.Serializable;

/**
 * A user role
 *  
 * @author Shane Bryzak
 */
public class Role implements Serializable
{
   private String name;
   
   public Role(String name)
   {
      this.name = name;
   }
   
   public String getName()
   {
      return name;
   }
}
