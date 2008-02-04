package org.jboss.seam.wicket;

import static org.jboss.seam.wicket.MetaModelUtils.createProxyFactory;
import static org.jboss.seam.wicket.MetaModelUtils.toName;

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
import org.jboss.seam.core.Init;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;


public class InjectionSupport
{
   
   // TODO Ouch
   private static final Map<Class, Class<ProxyObject>> proxyFactories = new HashMap<Class, Class<ProxyObject>>();
   
   private LogProvider log = Logging.getLogProvider(InjectionSupport.class);
   
   private List<BijectedAttribute<In>> inAttributes = new ArrayList<BijectedAttribute<In>>();
   
   private MetaModel metaModel;

   public InjectionSupport(MetaModel metaModel)
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
         // Currently need a proxy here as Wicket has no native support for interceptors
         // TODO Replace this with a Seam ClientSide interceptor. Needs JBSEAM-699
         in.set( instance, wrap( instance, in ) );
      }
   }
   
   private static Object wrap(Object bean, BijectedAttribute<In> in) throws Exception
   {
      ProxyObject proxy = getProxyFactory(in.getType()).newInstance();
      proxy.setHandler(new InjectionInterceptor(in));
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
   
}
