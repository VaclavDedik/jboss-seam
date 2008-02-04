/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.example.wicket;

import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.RequestListenerInterface;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.form.IFormSubmitListener;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequestCycleProcessor;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.protocol.http.request.WebRequestCodingStrategy;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.IRequestCycleProcessor;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.component.IBookmarkablePageRequestTarget;
import org.apache.wicket.request.target.component.listener.IListenerInterfaceRequestTarget;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Manager;
import org.jboss.seam.wicket.SeamAuthorizationStrategy;
import org.jboss.seam.wicket.SeamSupport;

/**
 * Test application for Wicket/ Seam/ Jetty.
 */
public class WicketBookingApplication extends WebApplication {

	/**
	 * Constructor.
	 */
	public WicketBookingApplication() {
	}

	/**
	 * @see wicket.Application#getHomePage()
	 */
	@SuppressWarnings("unchecked")
	public Class getHomePage() {
		return Home.class;
	}

	/**
	 * Custom session with invalidation override. We can't just let Wicket
	 * invalidate the session as Seam might have to do some cleaning up to do.
	 */
	@Override
	public Session newSession(Request request, Response response) {
		return new WebSession(WicketBookingApplication.this, request) {

			@Override
			public void invalidate() {
				org.jboss.seam.web.Session.getInstance().invalidate();
			}

			@Override
			public void invalidateNow() {
				// sorry, can't support this with Seam
				org.jboss.seam.web.Session.getInstance().invalidate();
			}
		};
	}
	
	@Override
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
	protected void init() {
		super.init();
		SeamSupport.activate(this);
		getSecuritySettings().setAuthorizationStrategy(new SeamAuthorizationStrategy(Home.class));
	}
}