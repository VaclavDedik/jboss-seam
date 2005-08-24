/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.contexts;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class WebSessionContext implements Context {

	private HttpSession session;
	
   private String getKey(String name)
   {
      return getPrefix() + name;
   }

   private String getPrefix()
   {
      return ScopeType.SESSION.getPrefix() + '$';
   }

	public WebSessionContext(HttpSession session) {
		this.session = session;
	}

	public Object get(String name) {
		return session.getAttribute( getKey(name) );
	}

	public void set(String name, Object value) {
		session.setAttribute( getKey(name), value );
	}

	public boolean isSet(String name) {
		return get(name)!=null;
	}

	public void remove(String name) {
		session.removeAttribute( getKey(name) );
	}

	public String[] getNames() {
		Enumeration names = session.getAttributeNames();
		ArrayList<String> results = new ArrayList<String>();
      String prefix = getPrefix();
      while ( names.hasMoreElements() ) {
         String name = (String) names.nextElement();
         if ( name.startsWith(prefix) )
         {
            results.add( name.substring(13) );
         }
      }
		return results.toArray(new String[]{});
	}
   
   public Object get(Class clazz)
   {
      return get( Seam.getComponentName(clazz) );
   }
  
}
