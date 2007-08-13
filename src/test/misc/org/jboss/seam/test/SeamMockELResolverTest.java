package org.jboss.seam.test;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;

import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

/**
 * Test for adding EL resolvers to Seam MockFacesContext
 * 
 * @author Pete Muir
 * 
 */
public class SeamMockELResolverTest extends SeamTest
{
   
   private static final String property = "customELResolverTest";

   @Override
   protected void startJbossEmbeddedIfNecessary()
            throws org.jboss.deployers.spi.DeploymentException, java.io.IOException
   {
   }

   @Override
   protected ELResolver[] getELResolvers()
   {
      ELResolver[] resolvers = new ELResolver[1];
      resolvers[0] = new ELResolver()
      {

         @Override
         public Class<?> getCommonPropertyType(ELContext arg0, Object arg1)
         {
            return null;
         }

         @Override
         public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext arg0, Object arg1)
         {
            return null;
         }

         @Override
         public Class<?> getType(ELContext arg0, Object base, Object property)
                  throws NullPointerException, PropertyNotFoundException, ELException
         {
            return null;
         }

         @Override
         public Object getValue(ELContext context, Object base, Object property)
                  throws NullPointerException, PropertyNotFoundException, ELException
         {
            if (SeamMockELResolverTest.property.equals(property))
            {
               context.setPropertyResolved(true);
               return "found";
            }
            return null;
         }

         @Override
         public boolean isReadOnly(ELContext arg0, Object base, Object property)
                  throws NullPointerException, PropertyNotFoundException, ELException
         {
            if (SeamMockELResolverTest.property.equals(property))
            {
               return false;
            }
            return false;
         }

         @Override
         public void setValue(ELContext context, Object base, Object property, Object value)
                  throws NullPointerException, PropertyNotFoundException,
                  PropertyNotWritableException, ELException
         {
            if (SeamMockELResolverTest.property.equals(property))
            {
               throw new PropertyNotWritableException();
            }
         }

      };
      return resolvers;
   }

   @Test
   public void testCustomELResolver() throws Exception
   {
      new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            assert "found".equals(getValue("#{" + property + "}"));
         }
      }.run();
   }

}
