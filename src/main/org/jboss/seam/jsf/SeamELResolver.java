package org.jboss.seam.jsf;

import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;

import org.jboss.seam.Component;

public class SeamELResolver extends ELResolver
{

   @Override
   public Class getCommonPropertyType(ELContext context, Object base)
   {
      return null;
   }

   @Override
   public Iterator getFeatureDescriptors(ELContext context, Object base)
   {
      return null;
   }

   @Override
   public Class getType(ELContext context, Object base, Object property)
   {
      return null;
   }

   @Override
   public Object getValue(ELContext context, Object base, Object property)
   {
      if (base==null)
      {
         String name = (String) property;
         name = name.replace('$', '.');
         Object result = Component.getInstance(name);
         if (result!=null)
         {
            context.setPropertyResolved(true);
         }
         return result;
      }
      else
      {
         return null;
      }
   }

   @Override
   public boolean isReadOnly(ELContext context, Object base, Object property)
   {
      return false;
   }

   @Override
   public void setValue(ELContext context, Object base, Object property, Object value) {}

}
