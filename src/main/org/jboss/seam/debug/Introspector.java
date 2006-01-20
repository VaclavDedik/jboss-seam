package org.jboss.seam.debug;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.contexts.Contexts;

@Name("org.jboss.seam.debug.introspector")
public class Introspector {
   
   @RequestParameter
   private String name;
   
   public Attribute[] getAttributes() throws Exception
   {
      if (name==null) return null;
      Object component = Contexts.lookupInStatefulContexts(name);
      if (component==null) return null;
      BeanInfo bi = java.beans.Introspector.getBeanInfo( component.getClass() );
      //MethodDescriptor[] methods = bi.getMethodDescriptors();
      PropertyDescriptor[] properties = bi.getPropertyDescriptors();
      Attribute[] attributes = new Attribute[properties.length];
      for (int i=0; i<properties.length; i++)
      {
         attributes[i] = new Attribute( properties[i].getDisplayName(), properties[i].getReadMethod().invoke(component) );
      }
      return attributes;
   }
   
   public static class Attribute
   {
      private String name;
      private Object value;
      
      public Attribute(String name, Object value)
      {
         this.name = name;
         this.value = value;
      }
      
      public String getName() {
         return name;
      }
      public void setName(String name) {
         this.name = name;
      }
      public Object getValue() {
         return value;
      }
      public void setValue(Object value) {
         this.value = value;
      }
      
   }
}
