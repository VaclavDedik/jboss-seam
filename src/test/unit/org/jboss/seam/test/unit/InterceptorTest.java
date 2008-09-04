//$Id$
package org.jboss.seam.test.unit;

import java.lang.reflect.Method;

import javax.faces.context.ExternalContext;
import javax.faces.event.PhaseId;

import org.jboss.seam.Component;
import org.jboss.seam.NoConversationException;
import org.jboss.seam.RequiredException;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.ApplicationContext;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.FacesLifecycle;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.core.BijectionInterceptor;
import org.jboss.seam.core.ConversationEntries;
import org.jboss.seam.core.ConversationInterceptor;
import org.jboss.seam.core.ConversationalInterceptor;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.core.Manager;
import org.jboss.seam.ejb.RemoveInterceptor;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.mock.MockApplication;
import org.jboss.seam.mock.MockExternalContext;
import org.jboss.seam.mock.MockFacesContext;
import org.jboss.seam.mock.MockServletContext;
import org.jboss.seam.persistence.PersistenceContexts;
import org.testng.annotations.Test;

public class InterceptorTest
{
   
   @Test
   public void testBijectionInterceptor() throws Exception
   {
      MockServletContext servletContext = new MockServletContext();
      ServletLifecycle.beginApplication(servletContext);
      MockExternalContext externalContext = new MockExternalContext(servletContext);
      Context appContext = new ApplicationContext( externalContext.getApplicationMap() );
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      appContext.set( 
            Seam.getComponentName(ConversationEntries.class) + ".component", 
            new Component(ConversationEntries.class, appContext) 
         );
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

      FacesLifecycle.beginRequest(externalContext);
      Manager.instance().setCurrentConversationId("1");
      FacesLifecycle.resumeConversation(externalContext);
      FacesLifecycle.setPhaseId(PhaseId.RENDER_RESPONSE);
      
      final Bar bar = new Bar();
      final Foo foo = new Foo();
      Contexts.getSessionContext().set("otherFoo", foo);
      
      BijectionInterceptor bi = new BijectionInterceptor();
      bi.setComponent( new Component(Bar.class, appContext) );
      String result = (String) bi.aroundInvoke( new MockInvocationContext() {
         @Override
         public Object getTarget()
         {
            return bar;
         }

         Method method = Foo.class.getMethod("foo");
         @Override
         public Method getMethod()
         {
            return method;
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
      assert Contexts.getEventContext().get("otherString").equals("outAgain");
      assert Contexts.getConversationContext().get("string").equals("out");
      assert Contexts.getSessionContext().isSet("foo");
      assert bar.foo==null;
      assert bar.otherFoo==null;
      
      final Method method;
      try
      {
         method = Bar.class.getMethod("foo");
      }
      catch (Exception e) 
      {
         throw new RuntimeException(e);
      }

      bi.aroundInvoke( new MockInvocationContext() {
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
         @Override
         public Method getMethod()
         {
            return method;
         }
      });
      assert bar.foo==null;
      assert bar.otherFoo==null;
      
      try 
      {
         Contexts.getSessionContext().remove("otherFoo");
         bi.aroundInvoke( new MockInvocationContext() {
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
            @Override
            public Method getMethod()
            {
               return method;
            }
         });
         assert false;
      }
      catch (Exception e)
      {
         assert e instanceof RequiredException;
      }
      
      final Method method2;
      try
      {
         method2 = BrokenAction.class.getMethod("go");
      }
      catch (Exception e) 
      {
         throw new RuntimeException(e);
      }

      final BrokenAction brokenAction = new BrokenAction();
      BijectionInterceptor biba = new BijectionInterceptor();
      biba.setComponent( new Component(BrokenAction.class, appContext) );
      try
      {
         biba.aroundInvoke( new MockInvocationContext() {
   
            @Override
            public Object getTarget() {
               return brokenAction;
            }   
            @Override
            public Object proceed() throws Exception {
               assert false;
               return null;
            }
            
            @Override
            public Method getMethod()
            {
               return method2;
            }
          
         } );
         assert false;
      }
      catch (Exception e)
      {
         assert e instanceof RequiredException;
      }
      
      final Method method3;
      try
      {
         method3 = Action.class.getMethod("go");
      }
      catch (Exception e) 
      {
         throw new RuntimeException(e);
      }

      final Action action = new Action();
      BijectionInterceptor bia = new BijectionInterceptor();
      bia.setComponent( new Component(Action.class, appContext) );
      result = (String) bia.aroundInvoke( new MockInvocationContext() {

         @Override
         public Object getTarget() {
            return action;
         }

         @Override
         public Object proceed() throws Exception {
            assert "Gavin King".equals(action.name);
            return action.go();
         }
         
         @Override
         public Method getMethod()
         {
            return method3;
         }
       
      } );
      assert "success".equals(result);
      assert Contexts.getConversationContext().get("name").equals("Gavin King");

      ServletLifecycle.endApplication();
   }
   
   @Test
   public void testCyclicDependencyDoesNotStackOverflow() throws Exception
   {
      MockServletContext servletContext = new MockServletContext();
      ServletLifecycle.beginApplication(servletContext);
      MockExternalContext externalContext = new MockExternalContext(servletContext);
      Context appContext = new ApplicationContext( externalContext.getApplicationMap() );
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      appContext.set( 
            Seam.getComponentName(ConversationEntries.class) + ".component", 
            new Component(ConversationEntries.class, appContext) 
         );
      appContext.set( 
            Seam.getComponentName(Manager.class) + ".component", 
            new Component(Manager.class, appContext) 
         );
      appContext.set( 
            Seam.getComponentName(CyclicFoo.class) + ".component", 
            new Component(CyclicFoo.class, appContext) 
         );
      appContext.set( 
            Seam.getComponentName(CyclicBar.class) + ".component", 
            new Component(CyclicBar.class, appContext) 
      );

      FacesLifecycle.beginRequest(externalContext);
      Manager.instance().setCurrentConversationId("1");
      FacesLifecycle.resumeConversation(externalContext);
      FacesLifecycle.setPhaseId(PhaseId.RENDER_RESPONSE);
      
      final CyclicFoo cyclicFoo = new CyclicFoo();
      final CyclicBar cyclicBar = new CyclicBar();
      
      final BijectionInterceptor cyclicFooBijectionInterceptor = new BijectionInterceptor();
      cyclicFooBijectionInterceptor.setComponent( new Component(CyclicFoo.class, appContext) );
      final Method cyclicFooGetName = CyclicFoo.class.getMethod("getName");
      final MockInvocationContext callGetName = new MockInvocationContext() {
         @Override
         public Object getTarget()
         {
            return cyclicFoo;
         }

         @Override
         public Object proceed() throws Exception
         {
            return cyclicFoo.getName();
         }
         
         @Override
         public Method getMethod()
         {
            return cyclicFooGetName;
         }
      };
      
      final Method cyclicFooGetFooBar = CyclicFoo.class.getMethod("getFooBar");
      final MockInvocationContext callGetCyclicFooBar = new MockInvocationContext() {
         @Override
         public Object getTarget()
         {
            return cyclicFoo;
         }
         
         @Override
         public Object proceed() throws Exception
         {
            return cyclicFoo.getFooBar();
         }
         
         @Override
         public Method getMethod()
         {
            return cyclicFooGetFooBar;
         }
      };
      
      CyclicFoo cyclicFooProxy = new CyclicFoo()
      {
         @Override
         public String getName() throws Exception
         {
            return (String) cyclicFooBijectionInterceptor.aroundInvoke(callGetName);
         }
         
         @Override
         public String getFooBar() throws Exception
         {
            return (String) cyclicFooBijectionInterceptor.aroundInvoke(callGetCyclicFooBar);
         }
      };
      
      
      final BijectionInterceptor cyclicBarBijectionInterceptor = new BijectionInterceptor();
      cyclicBarBijectionInterceptor.setComponent( new Component(CyclicBar.class, appContext) );
      final Method cyclicBarProvideCyclicFooBar = CyclicBar.class.getMethod("provideCyclicFooBar");
      final MockInvocationContext callProvideCyclicFooBar = new MockInvocationContext() {
         @Override
         public Object getTarget()
         {
            return cyclicBar;
         }

         @Override
         public Object proceed() throws Exception
         {
            return cyclicBar.provideCyclicFooBar();
         }
         
         @Override
         public Method getMethod()
         {
            return cyclicBarProvideCyclicFooBar;
         }
      };
      
      final CyclicBar cyclicBarProxy = new CyclicBarProxy(callProvideCyclicFooBar, cyclicBarBijectionInterceptor);
      
      
      appContext.set("cyclicFoo", cyclicFooProxy);
      appContext.set("cyclicBar", cyclicBarProxy);
      
      cyclicFooProxy.getFooBar();
      
   }

   /*
    * Needs to be non-anonymous, so that provideCyclicFooBar() can be accessed reflectively
    */
   public class CyclicBarProxy extends CyclicBar
   {
      private final MockInvocationContext callProvideCyclicFooBar;
      private final BijectionInterceptor cyclicBarBijectionInterceptor;

      private CyclicBarProxy(MockInvocationContext callProvideCyclicFooBar, BijectionInterceptor cyclicBarBijectionInterceptor)
      {
         this.callProvideCyclicFooBar = callProvideCyclicFooBar;
         this.cyclicBarBijectionInterceptor = cyclicBarBijectionInterceptor;
      }

      @Override
      public String provideCyclicFooBar() throws Exception
      {
         return (String) cyclicBarBijectionInterceptor.aroundInvoke(callProvideCyclicFooBar);
      }
   }

   @Test
   public void testConversationInterceptor() throws Exception
   {
      MockServletContext servletContext = new MockServletContext();
      ServletLifecycle.beginApplication(servletContext);
      MockExternalContext externalContext = new MockExternalContext(servletContext);
      Context appContext = new ApplicationContext( externalContext.getApplicationMap() );
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      appContext.set( 
            Seam.getComponentName(ConversationEntries.class) + ".component", 
            new Component(ConversationEntries.class, appContext) 
         );
      appContext.set( 
            Seam.getComponentName(PersistenceContexts.class) + ".component", 
            new Component(PersistenceContexts.class, appContext) 
         );
      appContext.set( 
            Seam.getComponentName(Manager.class) + ".component", 
            new Component(Manager.class, appContext) 
         );
      FacesLifecycle.beginRequest( externalContext );
      Manager.instance().setCurrentConversationId("1");
      FacesLifecycle.resumeConversation(externalContext);

      ConversationInterceptor ci = new ConversationInterceptor();
      ci.setComponent( new Component(Foo.class, appContext) );
      
      assert !Manager.instance().isLongRunningConversation();

      String result = (String) ci.aroundInvoke( new MockInvocationContext() {
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
      
      Manager.instance().initializeTemporaryConversation();
      
      result = (String) ci.aroundInvoke( new MockInvocationContext() {
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

      result = (String) ci.aroundInvoke( new MockInvocationContext() {
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

      result = (String) ci.aroundInvoke( new MockInvocationContext() {
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
      
      result = (String) ci.aroundInvoke( new MockInvocationContext() {
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

      result = (String) ci.aroundInvoke( new MockInvocationContext() {
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

      result = (String) ci.aroundInvoke( new MockInvocationContext() {
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

      result = (String) ci.aroundInvoke( new MockInvocationContext() {
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

      result = (String) ci.aroundInvoke( new MockInvocationContext() {
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
      
      result = (String) ci.aroundInvoke( new MockInvocationContext() {
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

      result = (String) ci.aroundInvoke( new MockInvocationContext() {
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

      result = (String) ci.aroundInvoke( new MockInvocationContext() {
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

      result = (String) ci.aroundInvoke( new MockInvocationContext() {
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

      result = (String) ci.aroundInvoke( new MockInvocationContext() {
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

      ServletLifecycle.endApplication();
   }
   
   @Test
   public void testConversationalInterceptor() throws Exception
   {
      MockServletContext servletContext = new MockServletContext();
      ServletLifecycle.beginApplication(servletContext);
      MockExternalContext externalContext = new MockExternalContext(servletContext);
      Context appContext = new ApplicationContext( externalContext.getApplicationMap() );
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      appContext.set( 
            Seam.getComponentName(ConversationEntries.class) + ".component", 
            new Component(ConversationEntries.class, appContext) 
         );
      appContext.set( 
            Seam.getComponentName(Manager.class) + ".component", 
            new Component(Manager.class, appContext) 
         );
      appContext.set( 
            Seam.getComponentName(FacesMessages.class) + ".component", 
            new Component(FacesMessages.class, appContext) 
         );
      appContext.set( 
               Seam.getComponentName(Events.class) + ".component", 
               new Component(Events.class, appContext) 
            );
      FacesLifecycle.setPhaseId(PhaseId.INVOKE_APPLICATION);
      FacesLifecycle.beginRequest( externalContext );
      Manager.instance().setCurrentConversationId("1");
      FacesLifecycle.resumeConversation(externalContext);
      
      ConversationalInterceptor ci = new ConversationalInterceptor();
      ci.setComponent( new Component(Bar.class, appContext) );
      
      assert !Manager.instance().isLongRunningConversation();
      
      try
      {

         ci.aroundInvoke( new MockInvocationContext() {
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
         
         assert false;
         
      }
      catch (Exception e)
      {
         assert e instanceof NoConversationException;
      }
      
      assert !Manager.instance().isLongRunningConversation();
      
      String result = (String) ci.aroundInvoke( new MockInvocationContext() {
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
      
      Manager.instance().initializeTemporaryConversation();
      Manager.instance().beginConversation();
      
      //assert Manager.instance().isLongRunningConversation();
      assert "begun".equals(result);

      result = (String) ci.aroundInvoke( new MockInvocationContext() {
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
      
      //assert Manager.instance().isLongRunningConversation();
      assert "foo".equals(result);

      result = (String) ci.aroundInvoke( new MockInvocationContext() {
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
      
      Manager.instance().endConversation(false);
      
      //assert !Manager.instance().isLongRunningConversation();
      assert "ended".equals(result);
      
      ServletLifecycle.endApplication();
      
   }
   
   @Test
   public void testValidationInterceptor() throws Exception
   {
      MockServletContext servletContext = new MockServletContext();
      ServletLifecycle.beginApplication(servletContext);
      ExternalContext externalContext = new MockExternalContext(servletContext);
      new MockFacesContext( externalContext, new MockApplication() ).setCurrent().createViewRoot();
      
      Context appContext = new ApplicationContext( externalContext.getApplicationMap() );
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      appContext.set( 
            Seam.getComponentName(ConversationEntries.class) + ".component", 
            new Component(ConversationEntries.class, appContext) 
         );
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
      FacesLifecycle.setPhaseId(PhaseId.INVOKE_APPLICATION);
      FacesLifecycle.beginRequest(externalContext);
      Manager.instance().setCurrentConversationId("1");
      FacesLifecycle.resumeConversation(externalContext);
      
      ServletLifecycle.endApplication();
   }
   
   @Test 
   public void testRemoveInterceptor() throws Exception
   {
      MockServletContext servletContext = new MockServletContext();
      ServletLifecycle.beginApplication(servletContext);
      MockExternalContext externalContext = new MockExternalContext(servletContext);
      Context appContext = new ApplicationContext( externalContext.getApplicationMap() );
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      appContext.set( 
            Seam.getComponentName(ConversationEntries.class) + ".component", 
            new Component(ConversationEntries.class, appContext) 
         );
      appContext.set( 
            Seam.getComponentName(Manager.class) + ".component", 
            new Component(Manager.class, appContext) 
         );

      FacesLifecycle.beginRequest( externalContext );
      Contexts.getSessionContext().set( "foo", new Foo() );
      
      RemoveInterceptor ri = new RemoveInterceptor();
      ri.setComponent( new Component(Foo.class, appContext) );
      
      ri.aroundInvoke( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("foo");
         }
      } );
      
      assert Contexts.getSessionContext().isSet("foo");
      
      ri.aroundInvoke( new MockInvocationContext() {
         @Override
         public Method getMethod()
         {
            return InterceptorTest.getMethod("destroy");
         }
      } );
      
      assert !Contexts.getSessionContext().isSet("foo");
      
      ServletLifecycle.endApplication();
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
