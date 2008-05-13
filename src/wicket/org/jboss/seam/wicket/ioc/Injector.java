package org.jboss.seam.wicket.ioc;

import static org.jboss.seam.ScopeType.STATELESS;
import static org.jboss.seam.ScopeType.UNSPECIFIED;
import static org.jboss.seam.wicket.ioc.MetaModelUtils.createProxyFactory;
import static org.jboss.seam.wicket.ioc.MetaModelUtils.toName;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.util.proxy.ProxyObject;

import org.jboss.seam.Component;
import org.jboss.seam.Namespace;
import org.jboss.seam.annotations.In;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Init;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Controls injection for a MetaModel
 *
 */
public class Injector
{
   
   // TODO Ouch
   private static final Map<Class, Class<ProxyObject>> proxyFactories = new HashMap<Class, Class<ProxyObject>>();
   
   private List<BijectedAttribute<In>> inAttributes = new ArrayList<BijectedAttribute<In>>();
   
   private final MetaModel metaModel;
   
   private static LogProvider log = Logging.getLogProvider(Injector.class);

   public Injector(MetaModel metaModel)
   {
      this.metaModel = metaModel;
   }

   public void add(Method method)
   {
      if ( method.isAnnotationPresent(In.class) )
      {
         In in = method.getAnnotation(In.class);
         String name = toName( in.value(), method );
         inAttributes.add( new BijectedMethod(name, method, in, metaModel) );
      }
   }
   
   public void add(Field field)
   {
      if ( field.isAnnotationPresent(In.class) )
      {
         In in = field.getAnnotation(In.class);
         String name = toName( in.value(), field );
         inAttributes.add( new BijectedField(name, field, in, metaModel) );
      }
   }
   
   public void inject(Object instance) throws Exception
   {
      for ( BijectedAttribute<In> in : inAttributes )
      {
         in.set( instance, wrap( in, metaModel.getMetaModelName() ) );
      }
   }
   
   private static Object wrap(final BijectedAttribute<In> in, final String metaModelName) throws Exception
   {
      ProxyObject proxy = getProxyFactory(in.getType()).newInstance();
      proxy.setHandler(new InjectionInterceptor(in)
      {
         @Override
         protected Object getValueToInject(String name, In annotation, Object value)
         {
            return getValue(name, annotation, metaModelName, value);
         }
      });
      return proxy;
   }
   
   private static Class<ProxyObject> getProxyFactory(Class type)
   {
      if (proxyFactories.containsKey(type))
      {
         return proxyFactories.get(type);
      }
      else
      {
         Class<ProxyObject> factory = createProxyFactory( type );
         proxyFactories.put(type, factory);
         return factory;
      }
   }
   
   private static Object getValue(String name, In annotation, String metaModelName, Object bean)
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
                  getMetaModel(metaModelName).getAttributeMessage(name)
               );
         }
         if ( annotation.scope()==STATELESS )
         {
            throw new IllegalArgumentException(
                  "cannot specify explicit scope=STATELESS on @In: " +
                  getMetaModel(metaModelName).getAttributeMessage(name)
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
   
   private static Object getInstanceInAllNamespaces(String name, boolean create)
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
   
   private static MetaModel getMetaModel(String metaModelName)
   {
      return MetaModel.forName(metaModelName);     
   }
   
}
