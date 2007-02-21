/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.contexts;

import java.util.ArrayList;
import java.util.Enumeration;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.core.Events;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class WebRequestContext implements Context {

	private ContextAdaptor request;
	
   public ScopeType getType()
   {
      return ScopeType.EVENT;
   }

	public WebRequestContext(ContextAdaptor request) {
		this.request = request;
	}
   
   private String getKey(String name)
   {
      return /*getPrefix() + */ name;
   }

   /*private String getPrefix()
   {
      return ScopeType.EVENT.getPrefix() + '$';
   }*/

	public Object get(String name) {
		return request.getAttribute( getKey(name) );
	}

	public void set(String name, Object value) {
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.preSetVariable." + name);
		request.setAttribute( getKey(name), value);
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.postSetVariable." + name);
	}

	public boolean isSet(String name) {
		return get(name)!=null;
	}

	public void remove(String name) {
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.preRemoveVariable." + name);
		request.removeAttribute( getKey(name) );
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.postRemoveVariable." + name);
	}

	public String[] getNames() {
		Enumeration names = request.getAttributeNames();
		ArrayList<String> results = new ArrayList<String>();
      //String prefix = getPrefix();
		while ( names.hasMoreElements() ) {
			String name = (String) names.nextElement();
         /*if ( name.startsWith(prefix) )
         {
            results.add( name.substring(prefix.length()) );
         }*/
         if ( request.getAttribute(name)!=null )
         {
            results.add(name);
         }
		}
		return results.toArray( new String[]{} );
	}

   public Object get(Class clazz)
   {
      return get( Component.getComponentName(clazz) );
   }

   public void flush() {}

}
