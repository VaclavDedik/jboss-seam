package org.jboss.seam.wicket;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.IRedirectListener;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.behavior.IBehaviorListener;
import org.apache.wicket.markup.html.form.IFormSubmitListener;
import org.apache.wicket.markup.html.form.IOnChangeListener;
import org.apache.wicket.markup.html.link.ILinkListener;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.protocol.http.WebRequestCycleProcessor;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.protocol.http.request.WebRequestCodingStrategy;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.IRequestCycleProcessor;
import org.apache.wicket.request.target.component.IBookmarkablePageRequestTarget;
import org.apache.wicket.request.target.component.listener.IListenerInterfaceRequestTarget;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.ConversationPropagation;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Manager;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.servlet.ServletRequestSessionMap;
import org.jboss.seam.web.ServletContexts;
import org.jboss.seam.wicket.international.SeamStatusMessagesListener;

/**
 * The base class for Seam Web Applications
 * 
 * @author Pete Muir
 *
 */
public abstract class SeamWebApplication extends WebApplication
{
   
   private static final LogProvider log = Logging.getLogProvider(SeamWebApplication.class);

   /**
    * When operating in tests, it is sometimes useful to leave the contexts extant
    * after a request, and destroy them upon the next request, so that models that use injections
    * can be queried post-request to determine their values. 
    */
   protected boolean destroyContextsLazily = false;

   public boolean isDestroyContextsLazily()
   {
      return destroyContextsLazily;
   }

   public void setDestroyContextsLazily(boolean destroyContextsLazily)
   {
      this.destroyContextsLazily = destroyContextsLazily;
   }

   /**
    * Custom session with invalidation override. We can't just let Wicket
    * invalidate the session as Seam might have to do some cleaning up to do.
    */
   @Override
   public Session newSession(Request request, Response response)
   {
      return new WebSession(request) 
      {

         @Override
         public void invalidate() 
         {
            org.jboss.seam.web.Session.getInstance().invalidate();
         }

         @Override
         public void invalidateNow() 
         {
            // sorry, can't support this with Seam
            org.jboss.seam.web.Session.getInstance().invalidate();
         }
      };
   }

   @Override
   /**
    * Seam's hooks into Wicket. Required for proper functioning
    */
   protected IRequestCycleProcessor newRequestCycleProcessor()
   {
      return new WebRequestCycleProcessor()
      {
         @Override
         protected IRequestCodingStrategy newRequestCodingStrategy()
         {
            return new WebRequestCodingStrategy()
            {
               @Override
               protected CharSequence encode(RequestCycle requestCycle, final IListenerInterfaceRequestTarget requestTarget)
               {
                  String name = requestTarget.getRequestListenerInterface().getName();
                  CharSequence url = super.encode(requestCycle, requestTarget);
                  if ( Manager.instance().isReallyLongRunningConversation() && (
                       IFormSubmitListener.INTERFACE.getName().equals(name) || 
                       ILinkListener.INTERFACE.getName().equals(name) ||
                       IBehaviorListener.INTERFACE.getName().equals(name) || 
                       IOnChangeListener.INTERFACE.getName().equals(name) ||
                       IRedirectListener.INTERFACE.getName().equals(name) ))
                  {
                     // TODO Do this nicely
                     StringBuilder stringBuilder = new StringBuilder(url);
                     stringBuilder.append("&" + Manager.instance().getConversationIdParameter() + "=" + Conversation.instance().getId());
                     url = stringBuilder.subSequence(0, stringBuilder.length());
                  }
                  
                  return url;
               }

               @Override
               protected CharSequence encode(RequestCycle requestCycle, IBookmarkablePageRequestTarget requestTarget)
               {
                  // TODO Do this nicely
                  StringBuilder stringBuilder = new StringBuilder(super.encode(requestCycle, requestTarget));
                  if (Manager.instance().isLongRunningConversation())
                  {
                     stringBuilder.append("&" + Manager.instance().getConversationIdParameter() + "=" + Conversation.instance().getId());
                  }
                  return stringBuilder.subSequence(0, stringBuilder.length());
               }
            };
         }
      };
   }

   @Override
   protected void init()
   {
      super.init();
      inititializeSeamSecurity();
      initializeSeamStatusMessages();
      addComponentInstantiationListener(new SeamComponentInstantiationListener());
   }

   /**
    * Add Seam Security to the wicket app.
    * 
    * This allows you to @Restrict your Wicket components. Override this method
    * to apply a different scheme
    * 
    */
   protected void inititializeSeamSecurity()
   {
      getSecuritySettings().setAuthorizationStrategy(new SeamAuthorizationStrategy(getLoginPage()));
   }

   /**
    * Add Seam status message transport support to youur app.
    */
   protected void initializeSeamStatusMessages()
   {
      addComponentOnBeforeRenderListener(new SeamStatusMessagesListener());
   }

   protected abstract Class getLoginPage();

   /*
    * Override to provide a seam-specific RequestCycle, which sets up seam contexts.  
    */
   @Override
   public RequestCycle newRequestCycle(final Request request, final Response response)
   {
      return new SeamWebRequestCycle(this, (WebRequest)request, (WebResponse)response);
   }
   
   
   /**
    * A WebRequestCycle that sets up seam requests.  Essentially this
    * is similiar to the work of ContextualHttpServletRequest, but using the wicket API
    * @author cpopetz
    *
    */
   protected static class SeamWebRequestCycle extends WebRequestCycle { 
      
      public SeamWebRequestCycle(WebApplication application, WebRequest request, Response response)
      {
         super(application, request, response);
      }

      @Override
      protected void onBeginRequest() 
      {
         HttpServletRequest httpRequest = ((WebRequest)request).getHttpServletRequest();

         if (Contexts.getEventContext() != null && ((SeamWebApplication)getApplication()).isDestroyContextsLazily() && ServletContexts.instance().getRequest() != httpRequest)
         { 
            destroyContexts();
         }

         if (Contexts.getEventContext() == null)
         {
            ServletLifecycle.beginRequest(httpRequest);
            ServletContexts.instance().setRequest(httpRequest);
            ConversationPropagation.instance().restoreConversationId( request.getParameterMap() );
            Manager.instance().restoreConversation();
            ServletLifecycle.resumeConversation(httpRequest);
            Manager.instance().handleConversationPropagation( request.getParameterMap() );

            // Force creation of the session
            if (httpRequest.getSession(false) == null)
            {
               httpRequest.getSession(true);
            }
         }
         super.onBeginRequest();
         Events.instance().raiseEvent("org.jboss.seam.wicket.beforeRequest");
      }  

      @Override
      protected void onEndRequest() 
      {
         // TODO Auto-generated method stub
         try 
         { 
            super.onEndRequest();
            Events.instance().raiseEvent("org.jboss.seam.wicket.afterRequest");
         }
         finally 
         {
            if (Contexts.getEventContext() != null && !((SeamWebApplication)getApplication()).isDestroyContextsLazily())
            {
               destroyContexts();
            }
         }
      }

      private void destroyContexts() 
      {
         try { 
            HttpServletRequest httpRequest = ((WebRequest)request).getHttpServletRequest();
            Manager.instance().endRequest( new ServletRequestSessionMap(httpRequest)  );
            ServletLifecycle.endRequest(httpRequest);
         }
	      catch (Exception e)
	      {
	         /* Make sure we always clear out the thread locals */
	         Lifecycle.endRequest();
	         log.warn("ended request due to exception", e);
	         throw new RuntimeException(e);
	      }
      }
   }

}
