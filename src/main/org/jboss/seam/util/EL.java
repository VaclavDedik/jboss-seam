package org.jboss.seam.util;

import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.el.VariableMapper;

import org.jboss.seam.jsf.SeamELResolver;

import com.sun.el.ExpressionFactoryImpl;
import com.sun.el.lang.FunctionMapperImpl;
import com.sun.el.lang.VariableMapperImpl;

public class EL
{
   private static final ELResolver EL_RESOLVER = createELResolver();
   public static final ELContext EL_CONTEXT = createELContext();
   public static final ExpressionFactory EXPRESSION_FACTORY = new ExpressionFactoryImpl();
   
   private static ELResolver createELResolver()
   {
      CompositeELResolver resolver = new CompositeELResolver();
      resolver.add( new SeamELResolver() );
      resolver.add( new MapELResolver() );
      resolver.add( new ListELResolver() );
      resolver.add( new ResourceBundleELResolver() );
      resolver.add( new BeanELResolver() );
      return resolver;
   }

   private static ELContext createELContext()
   {
      return new ELContext()
      {

         @Override
         public ELResolver getELResolver()
         {
            return EL_RESOLVER;
         }

         @Override
         public FunctionMapper getFunctionMapper()
         {
            return new FunctionMapperImpl();
         }

         @Override
         public VariableMapper getVariableMapper()
         {
            return new VariableMapperImpl();
         }
         
      };
   }
   
}
