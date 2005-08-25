//$Id$
package org.jboss.seam.test;

import java.lang.reflect.Method;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Outcome;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.interceptors.OutcomeInterceptor;
import org.jboss.seam.interceptors.RemoveInterceptor;
import org.jboss.seam.mock.MockHttpSession;
import org.jboss.seam.mock.MockServletContext;
import org.testng.annotations.Test;

public class InterceptorTest
{
   @Test
   public void testOutcomeInterceptor() throws Exception
   {
      OutcomeInterceptor oi = new OutcomeInterceptor();
      
      String outcome = (String) oi.interceptOutcome( new MockInvocationContext() {
         @Override
         public Object proceed() throws Exception
         {
            return Outcome.REDISPLAY;
         } 
      } );
      assert outcome==null;
      
      outcome = (String) oi.interceptOutcome( new MockInvocationContext() {
         @Override
         public Object proceed() throws Exception
         {
            return "success";
         } 
      } );
      assert outcome=="success";
      
      Object result = oi.interceptOutcome( new MockInvocationContext() {
         @Override
         public Object proceed() throws Exception
         {
            return InterceptorTest.this;
         } 
      } );
      assert result==this;
   }
   
   @Test 
   public void testRemoveInterceptor() throws Exception
   {
      Contexts.beginRequest( new MockHttpSession( new MockServletContext() ) );
      Contexts.getSessionContext().set( "foo", new Foo() );
      
      RemoveInterceptor ri = new RemoveInterceptor();
      ri.setComponent( new Component(Foo.class) );
      
      ri.removeIfNecessary( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("foo");
         }
      } );
      
      assert Contexts.getSessionContext().isSet("foo");
      
      ri.removeIfNecessary( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("destroy");
         }
      } );
      
      assert !Contexts.getSessionContext().isSet("foo");
   }

   static Method getMethod(String name)
   {
      try
      {
         return Foo.class.getMethod(name);
      }
      catch (Exception e)
      {
         assert false;
         return null;
      }
   }
}
