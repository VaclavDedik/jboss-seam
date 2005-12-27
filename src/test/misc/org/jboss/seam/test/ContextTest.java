//$Id$
package org.jboss.seam.test;

import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpSession;

import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.ServerConversationContext;
import org.jboss.seam.contexts.EventContext;
import org.jboss.seam.contexts.FacesApplicationContext;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.contexts.Session;
import org.jboss.seam.contexts.WebSessionContext;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;
import org.jboss.seam.mock.MockExternalContext;
import org.jboss.seam.mock.MockHttpSession;
import org.jboss.seam.mock.MockServletContext;
import org.jboss.seam.servlet.ServletSessionImpl;
import org.testng.annotations.Test;

public class ContextTest
{
   @Test
   public void testContextManagement()
   {
      MockServletContext servletContext = new MockServletContext();
      MockExternalContext externalContext = new MockExternalContext(servletContext);
      Context appContext = new FacesApplicationContext(externalContext);
      appContext.set(
            Seam.getComponentName(Manager.class) + ".component",
            new Component(Manager.class)
         );
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      
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
      
      assert Contexts.getEventContext()!=null;
      assert Contexts.getSessionContext()!=null;
      assert Contexts.getConversationContext()!=null;
      assert Contexts.getApplicationContext()!=null;
      assert Contexts.getEventContext() instanceof EventContext;
      assert Contexts.getSessionContext() instanceof WebSessionContext;
      assert Contexts.getConversationContext() instanceof ServerConversationContext;
      assert Contexts.getApplicationContext() instanceof FacesApplicationContext;
      
      Contexts.getSessionContext().set("foo", "bar");
      Contexts.getApplicationContext().set("foo", "bar");
      Contexts.getConversationContext().set("xxx", "yyy");
      
      Lifecycle.endRequest(externalContext);
      
      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isConversationContextActive();
      assert !Contexts.isApplicationContextActive();
      assert ((MockHttpSession)externalContext.getSession(false)).getAttributes().size()==2;
      assert ((MockServletContext)externalContext.getContext()).getAttributes().size()==3;
      
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
      assert Contexts.getEventContext() instanceof EventContext;
      assert Contexts.getSessionContext() instanceof WebSessionContext;
      assert Contexts.getConversationContext() instanceof ServerConversationContext;
      assert Contexts.getApplicationContext() instanceof FacesApplicationContext;
      
      assert Contexts.getSessionContext().get("foo").equals("bar");
      assert Contexts.getApplicationContext().get("foo").equals("bar");
      assert Contexts.getConversationContext().get("xxx").equals("yyy");
      
      Manager.instance().setLongRunningConversation(false);
      Lifecycle.endRequest(externalContext);
      
      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isConversationContextActive();
      assert !Contexts.isApplicationContextActive();
      assert ((MockHttpSession)externalContext.getSession(false)).getAttributes().size()==1;
      assert ((MockServletContext)externalContext.getContext()).getAttributes().size()==3;
      
      Lifecycle.endSession( servletContext, new ServletSessionImpl( (HttpSession) externalContext.getSession(true) ) );
      
      //assert session.getAttributes().size()==0;
      
      Lifecycle.endApplication(servletContext);
      
      //assert servletContext.getAttributes().size()==0;
   }
   
   @Test
   public void testContexts()
   {
      MockServletContext servletContext = new MockServletContext();
      ExternalContext externalContext = new MockExternalContext(servletContext);
      Context appContext = new FacesApplicationContext(externalContext);
      Session session = new ServletSessionImpl( (HttpSession) externalContext.getSession(true) );
      appContext.set(
            Seam.getComponentName(Manager.class) + ".component", 
            new Component(Manager.class) 
         );
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      Lifecycle.beginRequest(externalContext);
      Manager.instance().setLongRunningConversation(true);
      testContext( new FacesApplicationContext(externalContext) );
      testContext( new WebSessionContext(session) );
      testContext( new EventContext() );
      testContext( new ServerConversationContext(session, "1") );
      testEquivalence( new ServerConversationContext(session, "1"), new ServerConversationContext(session, "1") );
      testEquivalence( new WebSessionContext(session), new WebSessionContext(session) );
      testEquivalence( new FacesApplicationContext(externalContext), new FacesApplicationContext(externalContext) );
      testIsolation( new ServerConversationContext(session, "1"), new ServerConversationContext(session, "2") );
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
