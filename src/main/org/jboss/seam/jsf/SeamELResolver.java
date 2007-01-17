package org.jboss.seam.jsf;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.model.DataModel;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Init;

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
         if ( !Contexts.isApplicationContextActive() )
         {
            //if no Seam contexts, bypass straight through to JSF
            return null;
         }
         
         String name = (String) property;
         name = name.replace('$', '.');
         Object result = Component.getInstance(name);
         if (result==null)
         {
            result = Init.instance().getRootNamespace().getChild(name);
         }
         if (result!=null)
         {
            context.setPropertyResolved(true);
         }
         return result;
      }
      else if (base instanceof DataModel)
      {
         if ( "size".equals(property) )
         {
            context.setPropertyResolved(true);
            return ( (DataModel) base ).getRowCount();
         }
         else if ( "empty".equals(property) )
         {
            context.setPropertyResolved(true);
            return ( (DataModel) base ).getRowCount()==0;
         }
         else
         {
            return null;
         }
      }
      else if (base instanceof Collection)
      {
         if ( "size".equals(property) )
         {
            context.setPropertyResolved(true);
            return ( (Collection) base ).size();
         }
         else
         {
            return null;
         }
      }
      else if (base instanceof Map)
      {
         if ( "size".equals(property) )
         {
            context.setPropertyResolved(true);
            return ( (Map) base ).size();
         }
         else if ( "values".equals(property) )
         {
            context.setPropertyResolved(true);
            return ( (Map) base ).values();
         }
         else if ( "keySet".equals(property) )
         {
            context.setPropertyResolved(true);
            return ( (Map) base ).keySet();
         }
         else if ( "entrySet".equals(property) )
         {
            context.setPropertyResolved(true);
            return ( (Map) base ).entrySet();
         }
         else
         {
            return null;
         }
      }
      else
      {
         return null;
      }
   }

   @Override
   public boolean isReadOnly(ELContext context, Object base, Object property)
   {
      return base!=null && 
            ( (base instanceof DataModel) || (base instanceof Collection) || (base instanceof Map) );
   }

   @Override
   public void setValue(ELContext context, Object base, Object property, Object value) {}

}
