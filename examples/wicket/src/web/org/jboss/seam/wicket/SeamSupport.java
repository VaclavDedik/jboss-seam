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

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentInstantiationListener;

/**
 * Main support class for letting Wicket work with Seam annotations. Active
 * support by calling {@link #activate(Application) the activate method}
 * preferably from your {@link Application#init() application's init method}.
 * 
 * @author eelcohillenius
 * @author Pete Muir
 */
// TODO handle outjection etc. Might need onAfterRender and may
// onBeforeRender for that, in which case this class can implement the
// appropriate interfaces and register itself as a listener for them.
// TODO handle method level sometime. For starters, see if there is any example/
// test case/ whatever in Seam first that shows off that kind of usage
public class SeamSupport implements IComponentInstantiationListener {

	/**
	 * Activate support for Seam annotations on components for the provided
	 * application.
	 * 
	 * @param application
	 *            The application to active support for
	 */
	public static void activate(Application application) {
		SeamSupport listener = new SeamSupport();
		application.addComponentInstantiationListener(listener);
	}

	/**
	 * @see org.apache.wicket.application.IComponentInstantiationListener#onInstantiation(org.apache.wicket.Component)
	 */
	public void onInstantiation(Component component) 
	{
	   WicketComponent wicketComponent = WicketComponent.forClass(component.getClass());
	   try
	   {
	      wicketComponent.inject(component);
	   }
	   catch (Exception e) 
	   {
         throw new RuntimeException(e);
      }
	}
}
