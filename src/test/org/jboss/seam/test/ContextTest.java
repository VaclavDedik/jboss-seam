//$Id$
package org.jboss.seam.test;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.ConversationContext;
import org.jboss.seam.contexts.EventContext;
import org.jboss.seam.contexts.WebApplicationContext;
import org.jboss.seam.contexts.WebSessionContext;
import org.jboss.seam.core.Manager;
import org.jboss.seam.mock.MockHttpSession;
import org.jboss.seam.mock.MockServletContext;
import org.testng.annotations.Test;

public class ContextTest
{
   @Test
   public void testContextManagement()
   {
      MockServletContext servletContext = new MockServletContext();
      MockHttpSession session = new MockHttpSession(servletContext);
      new WebApplicationContext(servletContext).set(
            Seam.getComponentName(Manager.class) + ".component",
            new Component(Manager.class)
         );
      
      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isConversationContextActive();
      assert !Contexts.isApplicationContextActive();
      
      Contexts.beginRequest(session);
      
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert !Contexts.isConversationContextActive();
      assert Contexts.isApplicationContextActive();
      
      Contexts.resumeConversation(session, "3");
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
      
      Contexts.endRequest(session);
      
      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isConversationContextActive();
      assert !Contexts.isApplicationContextActive();
      assert session.getAttributes().size()==2;
      assert servletContext.getAttributes().size()==2;
      
      Contexts.beginRequest(session);
      
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert !Contexts.isConversationContextActive();
      assert Contexts.isApplicationContextActive();
      
      Contexts.resumeConversation(session, "3");
      
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
      Contexts.endRequest(session);
      
      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isConversationContextActive();
      assert !Contexts.isApplicationContextActive();
      assert session.getAttributes().size()==1;
      assert servletContext.getAttributes().size()==2;
      
      Contexts.endSession(session);
      
      //assert session.getAttributes().size()==0;
      
      Contexts.endApplication(servletContext);
      
      //assert servletContext.getAttributes().size()==0;
   }
   
   @Test
   public void testContexts()
   {
      ServletContext servletContext = new MockServletContext();
      HttpSession session = new MockHttpSession(servletContext);
      Contexts.beginRequest(session);
      Contexts.getApplicationContext().set(
            Seam.getComponentName(Manager.class) + ".component", 
            new Component(Manager.class) 
         );
      Manager.instance().setLongRunningConversation(true);
      testContext( new WebApplicationContext(servletContext) );
      testContext( new WebSessionContext(session) );
      testContext( new EventContext() );
      testContext( new ConversationContext(session, "1") );
      testEquivalence( new ConversationContext(session, "1"), new ConversationContext(session, "1") );
      testEquivalence( new WebSessionContext(session), new WebSessionContext(session) );
      testEquivalence( new WebApplicationContext(servletContext), new WebApplicationContext(servletContext) );
      testIsolation( new ConversationContext(session, "1"), new ConversationContext(session, "2") );
      testIsolation( new WebSessionContext(session), new WebSessionContext( new MockHttpSession(servletContext) ) );
      
      Contexts.endApplication(servletContext);
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
