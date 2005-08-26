//$Id$
package org.jboss.seam.test;

import java.lang.reflect.Method;
import java.util.Map;

import javax.ejb.EJBContext;
import javax.ejb.InvocationContext;

public class MockInvocationContext implements InvocationContext
{

   public Object getBean()
   {
      //TODO
      return null;
   }

   public Map getContextData()
   {
      //TODO
      return null;
   }

   public EJBContext getEJBContext()
   {
      //TODO
      return null;
   }

   public Method getMethod()
   {
      //TODO
      return null;
   }

   public Object[] getParameters()
   {
      //TODO
      return null;
   }

   public Object proceed() throws Exception
   {
      return null;
   }

   public void setParameters(Object[] params)
   {
      //TODO
      
   }

}
