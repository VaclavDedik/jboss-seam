//$Id$
package org.jboss.seam.test;

import java.lang.reflect.Method;

import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Outcome;
import org.jboss.seam.components.ConversationManager;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.interceptors.ConversationInterceptor;
import org.jboss.seam.interceptors.OutcomeInterceptor;
import org.jboss.seam.interceptors.RemoveInterceptor;
import org.jboss.seam.interceptors.ValidationInterceptor;
import org.jboss.seam.mock.MockFacesContext;
import org.jboss.seam.mock.MockHttpServletRequest;
import org.jboss.seam.mock.MockHttpSession;
import org.jboss.seam.mock.MockServletContext;
import org.testng.annotations.Test;

public class InterceptorTest
{
   
   @Test
   public void testConversationInterceptor() throws Exception
   {
      MockHttpSession session = new MockHttpSession( new MockServletContext() );
      Contexts.beginRequest( session );
      Contexts.resumeConversation( session, "1" );
      Contexts.getApplicationContext().set( 
               Seam.getComponentName(ConversationManager.class) + ".component", 
               new Component(ConversationManager.class) 
            );
      
      ConversationInterceptor ci = new ConversationInterceptor();
      ci.setComponent( new Component(Foo.class) );
      
      assert !ConversationManager.instance().isLongRunningConversation();

      String result = (String) ci.endOrBeginLongRunningConversation( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("foo");
         }
         @Override
         public Object proceed() throws Exception
         {
            return "foo";
         }
      });
      
      assert !ConversationManager.instance().isLongRunningConversation();
      assert "foo".equals(result);
      
      result = (String) ci.endOrBeginLongRunningConversation( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("begin");
         }
         @Override
         public Object proceed() throws Exception
         {
            return "begun";
         }
      });
      
      assert ConversationManager.instance().isLongRunningConversation();
      assert "begun".equals(result);

      result = (String) ci.endOrBeginLongRunningConversation( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("foo");
         }
         @Override
         public Object proceed() throws Exception
         {
            return "foo";
         }
      });
      
      assert ConversationManager.instance().isLongRunningConversation();
      assert "foo".equals(result);

      result = (String) ci.endOrBeginLongRunningConversation( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("end");
         }
         @Override
         public Object proceed() throws Exception
         {
            return "ended";
         }
      });
      
      assert !ConversationManager.instance().isLongRunningConversation();
      assert "ended".equals(result);
      
      //TODO: @BeginIf/@EndIf
      
   }
   
   @Test
   public void testConversationalConversationInterceptor() throws Exception
   {
      MockHttpSession session = new MockHttpSession( new MockServletContext() );
      Contexts.beginRequest( session );
      Contexts.resumeConversation( session, "1" );
      Contexts.getApplicationContext().set( 
               Seam.getComponentName(ConversationManager.class) + ".component", 
               new Component(ConversationManager.class) 
            );
      
      ConversationInterceptor ci = new ConversationInterceptor();
      ci.setComponent( new Component(Bar.class) );
      
      assert !ConversationManager.instance().isLongRunningConversation();

      String result = (String) ci.endOrBeginLongRunningConversation( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("foo");
         }
         @Override
         public Object proceed() throws Exception
         {
            assert false;
            return null;
         }
      });
      
      assert !ConversationManager.instance().isLongRunningConversation();
      assert "error".equals(result);
      
      result = (String) ci.endOrBeginLongRunningConversation( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("begin");
         }
         @Override
         public Object proceed() throws Exception
         {
            return "begun";
         }
      });
      
      assert ConversationManager.instance().isLongRunningConversation();
      assert "begun".equals(result);

      result = (String) ci.endOrBeginLongRunningConversation( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("foo");
         }
         @Override
         public Object proceed() throws Exception
         {
            return "foo";
         }
      });
      
      assert ConversationManager.instance().isLongRunningConversation();
      assert "foo".equals(result);

      result = (String) ci.endOrBeginLongRunningConversation( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("end");
         }
         @Override
         public Object proceed() throws Exception
         {
            return "ended";
         }
      });
      
      assert !ConversationManager.instance().isLongRunningConversation();
      assert "ended".equals(result);
      
   }
   
   @Test
   public void testValidationInterceptor() throws Exception
   {
      
      new MockFacesContext( new MockHttpServletRequest( new MockHttpSession( new MockServletContext() ) ) ).setCurrent();
      
      ValidationInterceptor vi = new ValidationInterceptor();
      vi.setComponent( new Component(Foo.class) );
      
      final Foo foo = new Foo();
      
      String result = (String) vi.validateTargetComponent( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("foo");
         }
         @Override
         public Object getBean()
         {
            return foo;
         }

         @Override
         public Object proceed() throws Exception
         {
            return foo.foo();
         }
      });
      
      assert "foo".equals(result);
      assert !FacesContext.getCurrentInstance().getMessages().hasNext();      
      
      result = (String) vi.validateTargetComponent( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("bar");
         }
         @Override
         public Object getBean()
         {
            return foo;
         }

         @Override
         public Object proceed() throws Exception
         {
            assert false;
            return foo.bar();
         }
      });
      
      assert "baz".equals(result);
      assert FacesContext.getCurrentInstance().getMessages().hasNext();      

      foo.setValue("not null");
      
      result = (String) vi.validateTargetComponent( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("bar");
         }
         @Override
         public Object getBean()
         {
            return foo;
         }

         @Override
         public Object proceed() throws Exception
         {
            return foo.bar();
         }
      });
      
      assert "bar".equals(result);
   }
   
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
