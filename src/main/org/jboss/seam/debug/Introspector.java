package org.jboss.seam.debug;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
      if (component==null) 
      {
         return null;
      }
      else if (component instanceof Map)
      {
         return getMapAttributes( (Map) component );
      }
      else if (component instanceof List)
      {
         return getListAttributes( (List) component );
      }
      else
      {
         return getComponentAttributes(component);
      }
   }
   
   public Attribute[] getMapAttributes(Map<Object, Object> map)
   {
      Attribute[] attributes = new Attribute[map.size()];
      int i=0;
      for( Map.Entry me: map.entrySet() )
      {
         attributes[i++] = new Attribute( me.getKey().toString(), me.getValue() );
      }
      return attributes;
   }

   public Attribute[] getListAttributes(List list)
   {
      Attribute[] attributes = new Attribute[list.size()];
      for(int i=0; i<list.size(); i++ )
      {
         attributes[i] = new Attribute( Integer.toString(i), list.get(i) );
      }
      return attributes;
   }

   private Attribute[] getComponentAttributes(Object component) throws IntrospectionException, IllegalAccessException {
      BeanInfo bi = java.beans.Introspector.getBeanInfo( component.getClass() );
      //MethodDescriptor[] methods = bi.getMethodDescriptors();
      PropertyDescriptor[] properties = bi.getPropertyDescriptors();
      Attribute[] attributes = new Attribute[properties.length];
      for (int i=0; i<properties.length; i++)
      {
         Object value;
         try
         {
            value = properties[i].getReadMethod().invoke(component);
         }
         catch (InvocationTargetException ite)
         {
            Throwable e = ite.getCause();
            value = e.getClass().getName() + '[' + e.getMessage() + ']';
         }
         
         boolean convertArrayToList = value!=null && 
            value.getClass().isArray() && 
            !value.getClass().getComponentType().isPrimitive();
         if ( convertArrayToList )
         {
            value = Arrays.asList( (Object[]) value );
         }
         
         attributes[i] = new Attribute( properties[i].getDisplayName(), value );
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
