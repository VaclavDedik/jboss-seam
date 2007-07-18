package org.jboss.seam.el;

import static org.jboss.seam.util.JSF.DATA_MODEL;
import static org.jboss.seam.util.JSF.getRowCount;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELResolver;

import org.jboss.seam.Namespace;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Init;

/**
 * Resolves Seam components and namespaces. Also
 * allows the use of #{dataModel.size}, #{dataModel.empty},
 * #{collection.size}, #{map.size}, #{map.values}, #{map.keySet},
 * and #{map.entrySet}. Also allows #{sessionContext['name']}.
 * 
 * @author Gavin King
 *
 */
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
         return resolveBase(context, property);
      }
      else if ( base instanceof Namespace )
      {
         return resolveInNamespace(context, base, property);
      }
      else if ( DATA_MODEL.isInstance(base) )
      {
         return resolveInDataModel(context, base, property);
      }
      else if (base instanceof Collection)
      {
         return resolveInCollection(context, base, property);
      }
      else if (base instanceof Map)
      {
         return resolveInMap(context, base, property);
      }
      else if (base instanceof Context)
      {
         return resolveInContextObject(context, base, property);
      }
      else
      {
         return null;
      }
   }

   private Object resolveInContextObject(ELContext context, Object base, Object property)
   {
      Context seamContext = (Context) base;
      if ( seamContext.isSet( (String) property ) )
      {
         context.setPropertyResolved(true);
         return seamContext.get( (String) property );
      }
      else
      {
         return null;
      }
   }

   private Object resolveInMap(ELContext context, Object base, Object property)
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

   private Object resolveInCollection(ELContext context, Object base, Object property)
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

   private Object resolveInDataModel(ELContext context, Object base, Object property)
   {
      if ( "size".equals(property) )
      {
         context.setPropertyResolved(true);
         return getRowCount(base);
      }
      else if ( "empty".equals(property) )
      {
         context.setPropertyResolved(true);
         return getRowCount(base)==0;
      }
      else
      {
         return null;
      }
   }

   private Object resolveBase(ELContext context, Object property)
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         //if no Seam contexts, bypass straight through to JSF
         return null;
      }
      
      String key = (String) property;
      Init init = Init.instance();
      
      //look for a component in the root namespace
      Object result = init.getRootNamespace().getComponentInstance(key);
      if (result!=null)
      {
         context.setPropertyResolved(true);
         return result;
      }
      else
      {
         //look for a component in the imported namespaces
         for ( Namespace ns: init.getGlobalImports() )
         {
            result = ns.getComponentInstance(key);
            if (result!=null)
            {
               context.setPropertyResolved(true);
               return result;
            }
         }
      }
      
      //look for a namespace
      Namespace namespace = init.getRootNamespace().getChild(key);
      if (namespace!=null)
      {
         context.setPropertyResolved(true);
      }
      return namespace;
   }

   private Object resolveInNamespace(ELContext context, Object base, Object property)
   {
      Object result = ( (Namespace) base ).get( (String) property );
      if (result!=null)
      {
         context.setPropertyResolved(true);
      }
      return result;
   }

   @Override
   public boolean isReadOnly(ELContext context, Object base, Object property)
   {
      return base!=null && 
            ( DATA_MODEL.isInstance(base) || (base instanceof Collection) || (base instanceof Map) );
   }

   @Override
   public void setValue(ELContext context, Object base, Object property, Object value) {}

}
