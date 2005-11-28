//$Id$
package org.jboss.seam.test;

import javax.faces.context.ExternalContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.Session;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.ConversationContext;
import org.jboss.seam.contexts.EventContext;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.contexts.WebApplicationContext;
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
      MockExternalContext externalContext = new MockExternalContext();
      Context appContext = new WebApplicationContext(externalContext);
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
      
      Lifecycle.resumeConversation(externalContext, "3");
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
      assert Contexts.getConversationContext() instanceof ConversationContext;
      assert Contexts.getApplicationContext() instanceof WebApplicationContext;
      
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
      
      Lifecycle.resumeConversation(externalContext, "3");
      
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
      assert Contexts.getConversationContext() instanceof ConversationContext;
      assert Contexts.getApplicationContext() instanceof WebApplicationContext;
      
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
      
      Lifecycle.endSession(externalContext);
      
      //assert session.getAttributes().size()==0;
      
      Lifecycle.endApplication(externalContext);
      
      //assert servletContext.getAttributes().size()==0;
   }
   
   @Test
   public void testContexts()
   {
      ExternalContext externalContext = new MockExternalContext();
      HttpSession session = new MockHttpSession();
      Context appContext = new WebApplicationContext(externalContext);
      appContext.set(
            Seam.getComponentName(Manager.class) + ".component", 
            new Component(Manager.class) 
         );
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      Lifecycle.beginRequest(externalContext);
      Manager.instance().setLongRunningConversation(true);
      testContext( new WebApplicationContext(externalContext) );
      testContext( new WebSessionContext(externalContext) );
      testContext( new EventContext() );
      testContext( new ConversationContext(externalContext, "1") );
      testEquivalence( new ConversationContext(externalContext, "1"), new ConversationContext(externalContext, "1") );
      testEquivalence( new WebSessionContext(externalContext), new WebSessionContext(externalContext) );
      testEquivalence( new WebApplicationContext(externalContext), new WebApplicationContext(externalContext) );
      testIsolation( new ConversationContext(externalContext, "1"), new ConversationContext(externalContext, "2") );
      testIsolation( new WebSessionContext(externalContext), new WebSessionContext( new MockExternalContext()) );
      
      Lifecycle.endApplication(externalContext);
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
