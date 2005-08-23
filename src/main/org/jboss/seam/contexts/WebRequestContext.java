/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.contexts;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;

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
   
   private String getKey(String name)
   {
      return getPrefix() + name;
   }

   private String getPrefix()
   {
      return ScopeType.EVENT.getPrefix() + '$';
   }

	public Object get(String name) {
		return request.getAttribute( getKey(name) );
	}

	public void set(String name, Object value) {
		request.setAttribute( getKey(name), value);
	}

	public boolean isSet(String name) {
		return get(name)!=null;
	}

	public void remove(String name) {
		request.removeAttribute( getKey(name) );
	}

	public String[] getNames() {
		Enumeration names = request.getAttributeNames();
		ArrayList<String> results = new ArrayList<String>();
      String prefix = getPrefix();
		while ( names.hasMoreElements() ) {
			String name = (String) names.nextElement();
         if ( name.startsWith(prefix) )
         {
            results.add( name.substring(13) );
         }
		}
		return results.toArray( new String[]{} );
	}

   public <T> T get(Class<T> clazz)
   {
      return (T) get( Seam.getComponentName(clazz) );
   }

}
