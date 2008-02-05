package org.jboss.seam.wicket;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.IFormSubmitListener;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequestCycleProcessor;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.protocol.http.request.WebRequestCodingStrategy;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.IRequestCycleProcessor;
import org.apache.wicket.request.target.component.IBookmarkablePageRequestTarget;
import org.apache.wicket.request.target.component.listener.IListenerInterfaceRequestTarget;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Manager;
import org.jboss.seam.wicket.ioc.SeamInjectionListener;

/**
 * The base class for Seam Web Applications
 * 
 * TODO Remove the need to extend this
 * 
 * @author Pete Muir
 *
 */
public abstract class SeamWebApplication extends WebApplication
{

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
                  if (IFormSubmitListener.INTERFACE.getName().equals(requestTarget.getRequestListenerInterface().getName()))
                  {
                     // TODO Do this nicely
                     StringBuilder stringBuilder = new StringBuilder(super.encode(requestCycle, requestTarget));
                     if (Manager.instance().isReallyLongRunningConversation())
                     {
                        stringBuilder.append("&" + Manager.instance().getConversationIdParameter() + "=" + Conversation.instance().getId());
                     }
                     return stringBuilder.subSequence(0, stringBuilder.length());
                  }
                  else
                  {
                     return super.encode(requestCycle, requestTarget);
                  }
               }
               
               @Override
               protected CharSequence encode(RequestCycle requestCycle, IBookmarkablePageRequestTarget requestTarget)
               {
                  if (requestCycle.getRequest().getParameter("cid") != null)
                  {
                     // TODO Do this nicely
                     StringBuilder stringBuilder = new StringBuilder(super.encode(requestCycle, requestTarget));
                     if (Manager.instance().isReallyLongRunningConversation())
                     {
                        stringBuilder.append("&" + Manager.instance().getConversationIdParameter() + "=" + Conversation.instance().getId());
                     }
                     return stringBuilder.subSequence(0, stringBuilder.length());
   
                  }
                  return super.encode(requestCycle, requestTarget);
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
   	initializeSeamInjection();
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
    * Add Seam injection support to your app. Required for proper functioning
    */
   protected void initializeSeamInjection()
   {
      addComponentInstantiationListener(new SeamInjectionListener());
   }
  
   protected abstract Class getLoginPage();

}
