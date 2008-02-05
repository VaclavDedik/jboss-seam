package org.jboss.seam.wicket.ioc;

import static org.jboss.seam.ScopeType.STATELESS;
import static org.jboss.seam.ScopeType.UNSPECIFIED;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.jboss.seam.Component;
import org.jboss.seam.Namespace;
import org.jboss.seam.annotations.In;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Init;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Reflections;

import javassist.util.proxy.MethodHandler;

// TODO Replace with a client side Seam interceptor
public class InjectionInterceptor implements MethodHandler, Serializable
{

   private String name;
   private In annotation;
   private String metaModelName;
   private transient MetaModel metaModel;

   public InjectionInterceptor(BijectedAttribute<In> in)
   {
      this.name = in.getName();
      this.annotation = in.getAnnotation();
      this.metaModelName = in.getMetaModel().getMetaModelName();
   }
   
   private static LogProvider log = Logging.getLogProvider(InjectionInterceptor.class);

   public Object invoke(final Object proxy, final Method method, final Method proceed, final Object[] params) throws Throwable
   {
      return Reflections.invoke(method, getValueToInject(proxy), params);
   }
   
   private Object getValueToInject(Object bean)
   {
      if ( name.startsWith("#") )
      {
         if ( log.isDebugEnabled() )
         {
            log.trace("trying to inject with EL expression: " + name);
         }
         return Expressions.instance().createValueExpression(name).getValue();
      }
      else if ( annotation.scope()==UNSPECIFIED )
      {
         if ( log.isDebugEnabled() )
         {
            log.trace("trying to inject with hierarchical context search: " + name);
         }
         return getInstanceInAllNamespaces(name, annotation.create());
      }
      else
      {
         if ( annotation.create() )
         {
            throw new IllegalArgumentException(
                  "cannot combine create=true with explicit scope on @In: " +
                  getMetaModel().getAttributeMessage(name)
               );
         }
         if ( annotation.scope()==STATELESS )
         {
            throw new IllegalArgumentException(
                  "cannot specify explicit scope=STATELESS on @In: " +
                  getMetaModel().getAttributeMessage(name)
               );
         }
         
         
         log.trace("trying to inject from specified context: " + name);
         
         if ( annotation.scope().isContextActive() )
         {
            return annotation.scope().getContext().get(name);
         }
      }
      return null;
   }
   
   private Object getInstanceInAllNamespaces(String name, boolean create)
   {
      Object result;
      result = Component.getInstance(name, create);
      if (result==null)
      {
         for ( Namespace namespace: Init.instance().getGlobalImports() )
         {
            result = namespace.getComponentInstance(name, create);
            if (result!=null) break; 
         }
      }
      return result;
   }
   
   private MetaModel getMetaModel()
   {
      if (metaModel == null)
      {
         metaModel =  MetaModel.forName(metaModelName);
      }
      return metaModel;
   }

}
