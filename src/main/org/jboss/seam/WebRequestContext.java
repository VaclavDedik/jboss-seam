/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class WebRequestContext implements Context {

	private HttpServletRequest request;
	
	public WebRequestContext(HttpServletRequest request) {
		this.request = request;
	}

	public Object get(String name) {
		return request.getAttribute(name);
	}

	public void set(String name, Object value) {
		request.setAttribute(name, value);
	}

	public boolean isSet(String name) {
		return get(name)!=null;
	}

	public void remove(String name) {
		request.removeAttribute(name);
	}

	public String[] getNames() {
		Enumeration names = request.getAttributeNames();
		ArrayList<String> results = new ArrayList<String>();
		while ( names.hasMoreElements() ) {
			results.add( (String) names.nextElement() );
		}
		return results.toArray(new String[]{});
	}
   
   public void destroy() {
      SeamVariableResolver svr = new SeamVariableResolver();
      Enumeration names = request.getAttributeNames();
      while ( names.hasMoreElements() ) {
         String name = (String) names.nextElement();
         SeamComponent component = svr.findSeamComponent(name);
         if ( component!=null && component.hasDestroyMethod() )
         {
            try {
               Object instance = request.getAttribute(name);
               instance.getClass().getMethod(component.getDestroyMethod().getName()).invoke( instance );
            }
            catch (Exception e)
            {
               throw new RuntimeException(e);
            }
         }
      }
   }
   
}
