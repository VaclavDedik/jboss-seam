package org.jboss.seam.wicket;

import static org.jboss.seam.ScopeType.STATELESS;
import static org.jboss.seam.ScopeType.UNSPECIFIED;
import static org.jboss.seam.wicket.MetaModelUtils.toName;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.RequiredException;
import org.jboss.seam.annotations.Out;


public class OutjectionSupport
{
   
   private List<BijectedAttribute<Out>> outAttributes = new ArrayList<BijectedAttribute<Out>>();
   
   private MetaModel metaModel;

   public OutjectionSupport(MetaModel metaModel)
   {
      this.metaModel = metaModel;
   }

   public void add(Method method)
   {
      Out out = method.getAnnotation(Out.class);
      String name = toName( out.value(), method );
      outAttributes.add( new BijectedMethod(name, method, out, metaModel) );
   }
   
   public void add(Field field)
   {
      if ( field.isAnnotationPresent(Out.class) )
      {
         Out out = field.getAnnotation(Out.class);
         String name = toName( out.value(), field );
         outAttributes.add(new BijectedField(name, field, out, metaModel) );
      }
   }
   
   public void outject(Object instance)
   {
      for ( BijectedAttribute<Out> att: outAttributes )
      {
         outjectAttribute( att.getAnnotation(), att.getName(), instance, att.get(instance) );
      }
   }
   
   private void outjectAttribute(Out out, String name, Object bean, Object value)
   {
      
      if (value==null && out.required())
      {
         throw new RequiredException(
               "@Out attribute requires non-null value: " +
               metaModel.getAttributeMessage(name)
            );
      }
      else
      {
         if ( out.scope()==UNSPECIFIED )
         {
            throw new IllegalArgumentException(
                        "Must specify a scope to outject to: " +
                        metaModel.getAttributeMessage(name)
                     );
         }
         else if ( out.scope()==STATELESS )
         {
            throw new IllegalArgumentException(
                  "cannot specify explicit scope=STATELESS on @Out: " +
                  metaModel.getAttributeMessage(name)
               );
         }
      
         if ( out.scope().isContextActive() )
         {
            if (value==null)
            {
               out.scope().getContext().remove(name);
            }
            else
            {
               out.scope().getContext().set(name, value);
            }
         }
      }
   }
   
}
