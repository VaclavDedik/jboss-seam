/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.contexts;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class WebApplicationContext implements Context {

	private ServletContext context;
	
   private String getKey(String name)
   {
      return getPrefix() + name;
   }

   private String getPrefix()
   {
      return ScopeType.APPLICATION.getPrefix() + '$';
   }

	public WebApplicationContext(ServletContext context) {
		this.context = context;
	}

	public Object get(String name) {
		return context.getAttribute( getKey(name) );
	}

	public void set(String name, Object value) {
		context.setAttribute( getKey(name), value );
	}

	public boolean isSet(String name) {
		return get(name)!=null;
	}

	public void remove(String name) {
		context.removeAttribute( getKey(name) );
	}

	public String[] getNames() {
		Enumeration names = context.getAttributeNames();
		ArrayList<String> results = new ArrayList<String>();
      String prefix = getPrefix();
		while ( names.hasMoreElements() ) {
         String name = (String) names.nextElement();
         if ( name.startsWith(prefix) )
         {
            results.add( name.substring(17) );
         }
		}
		return results.toArray(new String[]{});
	}

   public Object get(Class clazz)
   {
      return get( Seam.getComponentName(clazz) );
   }

}
