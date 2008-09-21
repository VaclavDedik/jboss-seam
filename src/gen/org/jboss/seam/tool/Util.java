package org.jboss.seam.tool;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.mapping.Property;
import org.hibernate.mapping.ToOne;

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
   public Set set()
   {
      return new HashSet();
   }
   public boolean isToOne(Property property)
   {
      return (property.getValue() != null) && (property.getValue() instanceof ToOne);
   }
}
