//$Id$
package org.jboss.seam.test;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;
import javax.servlet.http.HttpSession;

import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.ContextAdaptor;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.FacesApplicationContext;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.contexts.ServerConversationContext;
import org.jboss.seam.contexts.WebRequestContext;
import org.jboss.seam.contexts.WebSessionContext;
import org.jboss.seam.core.ConversationEntries;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;
import org.jboss.seam.jsf.SeamVariableResolver;
import org.jboss.seam.mock.MockExternalContext;
import org.jboss.seam.mock.MockHttpServletRequest;
import org.jboss.seam.mock.MockHttpSession;
import org.jboss.seam.mock.MockServletContext;
import org.jboss.seam.servlet.ServletRequestImpl;
import org.jboss.seam.servlet.ServletSessionImpl;
import org.testng.annotations.Test;

public class ContextTest
{
   @Test
   public void testContextManagement() throws Exception
   {
      SeamVariableResolver seamVariableResolver = new SeamVariableResolver(VARIABLE_RESOLVER);
      org.jboss.seam.jbpm.SeamVariableResolver jbpmVariableResolver = new org.jboss.seam.jbpm.SeamVariableResolver();
      
      MockServletContext servletContext = new MockServletContext();
      MockExternalContext externalContext = new MockExternalContext(servletContext);
      Context appContext = new FacesApplicationContext(externalContext);
      //appContext.set( Seam.getComponentName(Init.class), new Init() );
      appContext.set( 
            Seam.getComponentName(ConversationEntries.class) + ".component", 
            new Component(ConversationEntries.class, appContext) 
         );
      appContext.set(
            Seam.getComponentName(Manager.class) + ".component",
            new Component(Manager.class, appContext)
         );
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      
      appContext.set( 
            Seam.getComponentName(Bar.class) + ".component",
            new Component(Bar.class, appContext)
      );
      appContext.set( 
            Seam.getComponentName(Foo.class) + ".component",
            new Component(Foo.class, appContext)
      );
      appContext.set("otherFoo", new Foo());
      
      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isConversationContextActive();
      assert !Contexts.isApplicationContextActive();
      
      Lifecycle.beginRequest(externalContext);
      
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert !Contexts.isConversationContextActive();
      assert Contexts.isApplicationContextActive();
      
      Manager.instance().setCurrentConversationId("3");
      Lifecycle.resumeConversation(externalContext);
      Manager.instance().setLongRunningConversation(true);
      
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert Contexts.isConversationContextActive();
      assert Contexts.isApplicationContextActive();
      assert !Contexts.isPageContextActive();
      
      assert Contexts.getEventContext()!=null;
      assert Contexts.getSessionContext()!=null;
      assert Contexts.getConversationContext()!=null;
      assert Contexts.getApplicationContext()!=null;
      assert Contexts.getEventContext() instanceof WebRequestContext;
      assert Contexts.getSessionContext() instanceof WebSessionContext;
      assert Contexts.getConversationContext() instanceof ServerConversationContext;
      assert Contexts.getApplicationContext() instanceof FacesApplicationContext;
      
      Contexts.getSessionContext().set("zzz", "bar");
      Contexts.getApplicationContext().set("zzz", "bar");
      Contexts.getConversationContext().set("xxx", "yyy");
      
      Object bar = seamVariableResolver.resolveVariable(null, "bar");
      assert bar!=null;
      assert bar instanceof Bar;
      assert Contexts.getConversationContext().get("bar")==bar;
      Object foo = Contexts.getSessionContext().get("foo");
      assert foo!=null;
      assert foo instanceof Foo;
      
      Lifecycle.endRequest(externalContext);
      
      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isConversationContextActive();
      assert !Contexts.isApplicationContextActive();
      assert ((MockHttpSession)externalContext.getSession(false)).getAttributes().size()==5;
      assert ((MockServletContext)externalContext.getContext()).getAttributes().size()==7;
      
      Lifecycle.beginRequest(externalContext);
      
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert !Contexts.isConversationContextActive();
      assert Contexts.isApplicationContextActive();
      
      Manager.instance().setCurrentConversationId("3");
      Lifecycle.resumeConversation(externalContext);
      
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert Contexts.isConversationContextActive();
      assert Contexts.isApplicationContextActive();
      
      assert Contexts.getEventContext()!=null;
      assert Contexts.getSessionContext()!=null;
      assert Contexts.getConversationContext()!=null;
      assert Contexts.getApplicationContext()!=null;
      assert Contexts.getEventContext() instanceof WebRequestContext;
      assert Contexts.getSessionContext() instanceof WebSessionContext;
      assert Contexts.getConversationContext() instanceof ServerConversationContext;
      assert Contexts.getApplicationContext() instanceof FacesApplicationContext;
      
      assert Contexts.getSessionContext().get("zzz").equals("bar");
      assert Contexts.getApplicationContext().get("zzz").equals("bar");
      assert Contexts.getConversationContext().get("xxx").equals("yyy");
      assert Contexts.getConversationContext().get("bar")==bar;
      assert Contexts.getSessionContext().get("foo")==foo;
      
      assert Contexts.getConversationContext().getNames().length==2;
      assert Contexts.getApplicationContext().getNames().length==7;
      assert Contexts.getSessionContext().getNames().length==3;
      
      assert seamVariableResolver.resolveVariable(null, "zzz").equals("bar");
      assert seamVariableResolver.resolveVariable(null, "xxx").equals("yyy");
      assert seamVariableResolver.resolveVariable(null, "bar")==bar;
      assert seamVariableResolver.resolveVariable(null, "foo")==foo;
      
      assert jbpmVariableResolver.resolveVariable("zzz").equals("bar");
      assert jbpmVariableResolver.resolveVariable("xxx").equals("yyy");
      assert jbpmVariableResolver.resolveVariable("bar")==bar;
      assert jbpmVariableResolver.resolveVariable("foo")==foo;

      Manager.instance().setLongRunningConversation(false);
      Lifecycle.endRequest(externalContext);
      
      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isConversationContextActive();
      assert !Contexts.isApplicationContextActive();
      assert ((MockHttpSession)externalContext.getSession(false)).getAttributes().size()==3;
      assert ((MockServletContext)externalContext.getContext()).getAttributes().size()==7;
      
      Lifecycle.endSession( servletContext, new ServletSessionImpl( (HttpSession) externalContext.getSession(true) ) );
            
      Lifecycle.endApplication(servletContext);
      
   }
   
   private static final VariableResolver VARIABLE_RESOLVER = new VariableResolver() {
      @Override
      public Object resolveVariable(FacesContext facesContext, String name) { return null; }
   };
   
   @Test
   public void testContexts()
   {
      MockServletContext servletContext = new MockServletContext();
      MockHttpSession session = new MockHttpSession(servletContext);
      MockHttpServletRequest request = new MockHttpServletRequest(session);
      ExternalContext externalContext = new MockExternalContext(servletContext, request);
      ContextAdaptor sessionAdaptor = new ServletSessionImpl(session);
      ContextAdaptor requestAdaptor = new ServletRequestImpl(request);
      Context appContext = new FacesApplicationContext(externalContext);
      appContext.set( 
            Seam.getComponentName(ConversationEntries.class) + ".component", 
            new Component(ConversationEntries.class, appContext) 
         );
      appContext.set(
            Seam.getComponentName(Manager.class) + ".component", 
            new Component(Manager.class) 
         );
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      Lifecycle.beginRequest(externalContext);
      Manager.instance().setLongRunningConversation(true);
      testContext( new FacesApplicationContext(externalContext) );
      testContext( new WebSessionContext(sessionAdaptor) );
      testContext( new WebRequestContext(requestAdaptor) );
      testContext( new ServerConversationContext(sessionAdaptor, "1") );
      testEquivalence( new ServerConversationContext(sessionAdaptor, "1"), new ServerConversationContext(sessionAdaptor, "1") );
      testEquivalence( new WebSessionContext(sessionAdaptor), new WebSessionContext(sessionAdaptor) );
      testEquivalence( new FacesApplicationContext(externalContext), new FacesApplicationContext(externalContext) );
      testIsolation( new ServerConversationContext(sessionAdaptor, "1"), new ServerConversationContext(sessionAdaptor, "2") );
      // testIsolation( new WebSessionContext(externalContext), new WebSessionContext( new MockExternalContext()) );
      
      Lifecycle.endApplication(servletContext);
   }
   
   private void testEquivalence(Context ctx, Context cty)
   {
      ctx.set("foo", "bar");
      ctx.flush();
      assert cty.get("foo").equals("bar");
      ctx.remove("foo");
      ctx.flush();
      assert !cty.isSet("foo");
   }
   
   private void testIsolation(Context ctx, Context cty)
   {
      ctx.set("foo", "bar");
      ctx.flush();
      assert !cty.isSet("foo");
      cty.set("foo", "bar");
      ctx.remove("foo");
      ctx.flush();
      assert cty.get("foo").equals("bar");
   }
   
   private void testContext(Context ctx)
   {
      assert !ctx.isSet("foo");
      ctx.set("foo", "bar");
      assert ctx.isSet("foo");
      assert ctx.get("foo").equals("bar");
      ctx.remove("foo");
      assert !ctx.isSet("foo");
   }
}
