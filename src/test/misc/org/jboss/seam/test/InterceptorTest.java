//$Id$
package org.jboss.seam.test;

import java.lang.reflect.Method;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.jboss.seam.Component;
import org.jboss.seam.RequiredException;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Outcome;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.FacesApplicationContext;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.core.Manager;
import org.jboss.seam.interceptors.BijectionInterceptor;
import org.jboss.seam.interceptors.ConversationInterceptor;
import org.jboss.seam.interceptors.OutcomeInterceptor;
import org.jboss.seam.interceptors.RemoveInterceptor;
import org.jboss.seam.interceptors.ValidationInterceptor;
import org.jboss.seam.mock.MockApplication;
import org.jboss.seam.mock.MockExternalContext;
import org.jboss.seam.mock.MockFacesContext;
import org.jboss.seam.mock.MockServletContext;
import org.testng.annotations.Test;

public class InterceptorTest
{
   
   @Test
   public void testBijectionInterceptor() throws Exception
   {
      MockServletContext servletContext = new MockServletContext();
      MockExternalContext externalContext = new MockExternalContext(servletContext);
      Context appContext = new FacesApplicationContext(externalContext);
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      appContext.set( 
            Seam.getComponentName(Manager.class) + ".component", 
            new Component(Manager.class, appContext) 
         );
      appContext.set( 
            Seam.getComponentName(Foo.class) + ".component", 
            new Component(Foo.class, appContext) 
         );
      appContext.set( 
            Seam.getComponentName(Factory.class) + ".component", 
            new Component(Factory.class, appContext) 
         );

      Lifecycle.beginRequest( externalContext );
      Manager.instance().setCurrentConversationId("1");
      Lifecycle.resumeConversation( externalContext );
      Lifecycle.setPhaseId(PhaseId.RENDER_RESPONSE);
      
      final Bar bar = new Bar();
      final Foo foo = new Foo();
      Contexts.getSessionContext().set("otherFoo", foo);
      
      BijectionInterceptor bi = new BijectionInterceptor();
      bi.setComponent( new Component(Bar.class, appContext) );
      String result = (String) bi.bijectTargetComponent( new MockInvocationContext() {
         @Override
         public Object getTarget()
         {
            return bar;
         }

         @Override
         public Object proceed() throws Exception
         {
            assert bar.otherFoo==foo;
            assert bar.foo!=null;
            return bar.foo();
         }
      });
      assert "foo".equals(result);
      assert Contexts.getEventContext().get("string").equals("out");
      Foo created = bar.foo;
      assert created!=null;
      assert Contexts.getSessionContext().get("foo")==created;
      
      bar.foo=null;
      bar.otherFoo=null;
      bi.bijectTargetComponent( new MockInvocationContext() {
         @Override
         public Object getTarget()
         {
            return bar;
         }

         @Override
         public Object proceed() throws Exception
         {
            assert bar.otherFoo==foo;
            assert bar.foo!=null;
            return bar.foo();
         }
      });
      assert bar.foo==created;
      
      try 
      {
         Contexts.getSessionContext().remove("otherFoo");
         bi.bijectTargetComponent( new MockInvocationContext() {
            @Override
            public Object getTarget()
            {
               return bar;
            }
            @Override
            public Object proceed() throws Exception
            {
               assert false;
               return null;
            }
         });
         assert false;
      }
      catch (Exception e)
      {
         assert e instanceof RequiredException;
      }
      
      final BrokenAction brokenAction = new BrokenAction();
      BijectionInterceptor biba = new BijectionInterceptor();
      biba.setComponent( new Component(BrokenAction.class, appContext) );
      try
      {
         biba.bijectTargetComponent( new MockInvocationContext() {
   
            @Override
            public Object getTarget() {
               return brokenAction;
            }
   
            @Override
            public Object proceed() throws Exception {
               assert false;
               return null;
            }
          
         } );
         assert false;
      }
      catch (Exception e)
      {
         assert e instanceof RequiredException;
      }
      
      final Action action = new Action();
      BijectionInterceptor bia = new BijectionInterceptor();
      bia.setComponent( new Component(Action.class, appContext) );
      result = (String) bia.bijectTargetComponent( new MockInvocationContext() {

         @Override
         public Object getTarget() {
            return action;
         }

         @Override
         public Object proceed() throws Exception {
            assert "Gavin King".equals(action.name);
            return action.go();
         }
       
      } );
      assert "success".equals(result);
      assert Contexts.getConversationContext().get("name").equals("Gavin King");

      Lifecycle.endApplication(servletContext);
   }
   
   @Test
   public void testConversationInterceptor() throws Exception
   {
      MockServletContext servletContext = new MockServletContext();
      MockExternalContext externalContext = new MockExternalContext(servletContext);
      Context appContext = new FacesApplicationContext(externalContext);
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      appContext.set( 
            Seam.getComponentName(Manager.class) + ".component", 
            new Component(Manager.class, appContext) 
         );
      Lifecycle.beginRequest( externalContext );
      Manager.instance().setCurrentConversationId("1");
      Lifecycle.resumeConversation(externalContext);

      ConversationInterceptor ci = new ConversationInterceptor();
      ci.setComponent( new Component(Foo.class, appContext) );
      
      assert !Manager.instance().isLongRunningConversation();

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
      
      assert !Manager.instance().isLongRunningConversation();
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
      
      assert Manager.instance().isLongRunningConversation();
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
      
      assert Manager.instance().isLongRunningConversation();
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
      
      assert !Manager.instance().isLongRunningConversation();
      assert "ended".equals(result);
      
      result = (String) ci.endOrBeginLongRunningConversation( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("beginNull");
         }
         @Override
         public Object proceed() throws Exception
         {
            return null;
         }
      });
      
      assert !Manager.instance().isLongRunningConversation();
      assert result==null;

      result = (String) ci.endOrBeginLongRunningConversation( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("beginVoid");
         }
         @Override
         public Object proceed() throws Exception
         {
            return null;
         }
      });
      
      assert Manager.instance().isLongRunningConversation();
      assert result==null;

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
      
      assert Manager.instance().isLongRunningConversation();
      assert "foo".equals(result);

      result = (String) ci.endOrBeginLongRunningConversation( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("endNull");
         }
         @Override
         public Object proceed() throws Exception
         {
            return null;
         }
      });
      
      assert Manager.instance().isLongRunningConversation();
      assert result==null;

      result = (String) ci.endOrBeginLongRunningConversation( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("endVoid");
         }
         @Override
         public Object proceed() throws Exception
         {
            return null;
         }
      });
      
      assert !Manager.instance().isLongRunningConversation();
      assert result==null;
      
      result = (String) ci.endOrBeginLongRunningConversation( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("beginIf");
         }
         @Override
         public Object proceed() throws Exception
         {
            return "failure";
         }
      });
      
      assert !Manager.instance().isLongRunningConversation();
      assert "failure".equals(result);

      result = (String) ci.endOrBeginLongRunningConversation( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("beginIf");
         }
         @Override
         public Object proceed() throws Exception
         {
            return "success";
         }
      });
      
      assert Manager.instance().isLongRunningConversation();
      assert "success".equals(result);

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
      
      assert Manager.instance().isLongRunningConversation();
      assert "foo".equals(result);

      result = (String) ci.endOrBeginLongRunningConversation( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("endIf");
         }
         @Override
         public Object proceed() throws Exception
         {
            return "failure";
         }
      });
      
      assert Manager.instance().isLongRunningConversation();
      assert "failure".equals(result);

      result = (String) ci.endOrBeginLongRunningConversation( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("endIf");
         }
         @Override
         public Object proceed() throws Exception
         {
            return "success";
         }
      });
      
      assert !Manager.instance().isLongRunningConversation();
      assert "success".equals(result);

      Lifecycle.endApplication(servletContext);
   }
   
   @Test
   public void testConversationalConversationInterceptor() throws Exception
   {
      MockServletContext servletContext = new MockServletContext();
      MockExternalContext externalContext = new MockExternalContext(servletContext);
      Context appContext = new FacesApplicationContext(externalContext);
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      appContext.set( 
            Seam.getComponentName(Manager.class) + ".component", 
            new Component(Manager.class, appContext) 
         );
      appContext.set( 
            Seam.getComponentName(FacesMessages.class) + ".component", 
            new Component(FacesMessages.class, appContext) 
         );
      Lifecycle.setPhaseId(PhaseId.INVOKE_APPLICATION);
      Lifecycle.beginRequest( externalContext );
      Manager.instance().setCurrentConversationId("1");
      Lifecycle.resumeConversation(externalContext);
      
      ConversationInterceptor ci = new ConversationInterceptor();
      ci.setComponent( new Component(Bar.class, appContext) );
      
      assert !Manager.instance().isLongRunningConversation();

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
      
      assert !Manager.instance().isLongRunningConversation();
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
      
      assert Manager.instance().isLongRunningConversation();
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
      
      assert Manager.instance().isLongRunningConversation();
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
      
      assert !Manager.instance().isLongRunningConversation();
      assert "ended".equals(result);
      
      Lifecycle.endApplication(servletContext);
      
   }
   
   @Test
   public void testValidationInterceptor() throws Exception
   {
      MockServletContext servletContext = new MockServletContext();
      ExternalContext externalContext = new MockExternalContext(servletContext);
      new MockFacesContext( externalContext, new MockApplication() ).setCurrent();
      
      Context appContext = new FacesApplicationContext(externalContext);
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      appContext.set( 
            Seam.getComponentName(Manager.class) + ".component", 
            new Component(Manager.class, appContext) 
         );
      appContext.set( 
            Seam.getComponentName(FacesMessages.class) + ".component", 
            new Component(FacesMessages.class, appContext) 
         );
      appContext.set(
            Seam.getComponentName(Interpolator.class) + ".component", 
            new Component(Interpolator.class, appContext)
         );
      Lifecycle.setPhaseId(PhaseId.INVOKE_APPLICATION);
      Lifecycle.beginRequest(externalContext);
      Manager.instance().setCurrentConversationId("1");
      Lifecycle.resumeConversation(externalContext);
      
      ValidationInterceptor vi = new ValidationInterceptor();
      vi.setComponent( new Component(Foo.class, appContext) );

      final Foo foo = new Foo();
      
      String result = (String) vi.validateTargetComponent( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("foo");
         }
         @Override
         public Object getTarget()
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
      FacesMessages.afterPhase();
      FacesMessages.instance().beforeRenderResponse();
      assert !FacesContext.getCurrentInstance().getMessages().hasNext();      
      
      result = (String) vi.validateTargetComponent( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("bar");
         }
         @Override
         public Object getTarget()
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
      FacesMessages.afterPhase();
      FacesMessages.instance().beforeRenderResponse();
      assert FacesContext.getCurrentInstance().getMessages().hasNext();      

      foo.setValue("not null");
      
      result = (String) vi.validateTargetComponent( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("bar");
         }
         @Override
         public Object getTarget()
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
      
      Lifecycle.endApplication(servletContext);
   }
   
   @Test
   public void testOutcomeInterceptor() throws Exception
   {
      Lifecycle.setPhaseId(PhaseId.INVOKE_APPLICATION);
      
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
      MockServletContext servletContext = new MockServletContext();
      MockExternalContext externalContext = new MockExternalContext(servletContext);
      Context appContext = new FacesApplicationContext(externalContext);
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      appContext.set( 
            Seam.getComponentName(Manager.class) + ".component", 
            new Component(Manager.class, appContext) 
         );

      Lifecycle.beginRequest( externalContext );
      Contexts.getSessionContext().set( "foo", new Foo() );
      
      RemoveInterceptor ri = new RemoveInterceptor();
      ri.setComponent( new Component(Foo.class, appContext) );
      
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
      
      Lifecycle.endApplication(servletContext);
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
