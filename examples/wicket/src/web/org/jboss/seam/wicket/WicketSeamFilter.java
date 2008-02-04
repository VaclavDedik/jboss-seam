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
package org.jboss.seam.wicket;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.IOException;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.core.ConversationPropagation;
import org.jboss.seam.core.Manager;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.servlet.ContextualHttpServletRequest;
import org.jboss.seam.servlet.ServletRequestSessionMap;
import org.jboss.seam.web.AbstractFilter;
import org.jboss.seam.web.ServletContexts;

/**
 * Seam component that delegates requests to the {@link WicketFilter} and sets
 * up and pulls down Seam {@link Context}s for the request.
 * <p>
 * Users do not have to install this filter, but instead just install
 * {@link WicketFilter} like they would normally do. This Seam component
 * automatically attaches to it.
 * </p>
 * <p>
 * The filter automatically picks up the {@link WebApplication} when there is
 * only one active for the web application this filter is configured for. If
 * there are multiple Wicket applications active in the web application, you'll
 * have to explicitly configure which one to use by providing filter init
 * parameter 'applicationName', which corresponds to the filter name of the
 * Wicket filter for the application you want to use this filter with.
 * </p>
 * 
 * @author eelcohillenius
 */
@Scope(APPLICATION)
@Name("org.apache.wicket.seam.WicketSeamFilter")
@Install(classDependencies = { "org.apache.wicket.protocol.http.WebApplication" }, precedence = BUILT_IN)
@BypassInterceptors
@Filter()
public class WicketSeamFilter extends AbstractFilter {

	private static final class WicketSeamFilterConfigurationException extends
			IllegalStateException {
		public WicketSeamFilterConfigurationException(String msg) {
			super(msg);
		}
	}

	private static final String APPLICATION_NONE = "<none>";

	private static LogProvider log = Logging.getLogProvider(WicketSeamFilter.class);

	private String applicationName = null;

	private WicketFilter delegate = null;

	/**
	 * Construct.
	 */
	public WicketSeamFilter() {
	}

	@SuppressWarnings("unchecked")
	public void doFilter(final ServletRequest servletRequest,
			final ServletResponse servletResponse, final FilterChain filterChain)
			throws IOException, ServletException {

		// Check for the Wicket filter (which might be initialized after this
		// filter, hence the lazy loading). Synchronization is not important.
		if (delegate == null && !APPLICATION_NONE.equals(applicationName)) {

			if (applicationName == null) {
				Set<String> applicationKeys = Application.getApplicationKeys();
				if (applicationKeys.size() > 1) {
					throw new WicketSeamFilterConfigurationException(
							"If you run this filter in the context of multiple Wicket "
									+ "application instances (/ filters) you have to provide filer "
									+ "init parameter 'applicationName' which should correspond to "
									+ "the filter name you want to use this filter with.");
				} else if (applicationKeys.size() == 0) {
					// no Wicket apps configured... set to special name
					applicationName = APPLICATION_NONE;
				} else {
					applicationName = applicationKeys.iterator().next();
				}
			}
			Application application = (!APPLICATION_NONE
					.equals(applicationName)) ? Application
					.get(applicationName) : null;
			if (application != null && !(application instanceof WebApplication)) {
				log
						.warn("This filter can only be used with Wicket WebApplications. Currently, "
								+ "it is configured to work with an application of type "
								+ application.getClass().getName());
				applicationName = APPLICATION_NONE;
				filterChain.doFilter(servletRequest, servletResponse);
				return;
			}

			WebApplication webApplication = (WebApplication) application;
			if (webApplication == null) {
				log
						.warn("ignoring request: no Wicket web application instance found");
				applicationName = APPLICATION_NONE;
				filterChain.doFilter(servletRequest, servletResponse);
				return;
			}

			delegate = webApplication.getWicketFilter();
		}
		
		new ContextualHttpServletRequest((HttpServletRequest) servletRequest)
		{
		   @Override
         public void process() throws Exception 
		   {
		      delegate.doFilter(servletRequest, servletResponse, filterChain);
		   }
		   
		}.run();
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
		this.applicationName = filterConfig.getInitParameter("applicationName");
	}
}
