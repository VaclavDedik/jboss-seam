/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.contexts;

import java.util.ArrayList;
import java.util.Enumeration;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Mutable;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class WebSessionContext implements Context 
{

   private ContextAdaptor session;
	
   public ScopeType getType()
   {
      return ScopeType.SESSION;
   }

   private String getKey(String name)
   {
      return /*getPrefix() + */ name;
   }

   /*private String getPrefix()
   {
      return ScopeType.SESSION.getPrefix() + '$';
   }*/

	public WebSessionContext(ContextAdaptor session) 
   {
       this.session = session;
	}

	public Object get(String name) 
   {
		return session.getAttribute( getKey(name) );
	}

	public void set(String name, Object value) 
   {
      Events.instance().raiseEvent("org.jboss.seam.preSetVariable." + name);
		session.setAttribute( getKey(name), value );
      Events.instance().raiseEvent("org.jboss.seam.postSetVariable." + name);
	}

	public boolean isSet(String name) 
   {
		return get(name)!=null;
	}

	public void remove(String name) 
   {
      Events.instance().raiseEvent("org.jboss.seam.preRemoveVariable." + name);
		session.removeAttribute( getKey(name) );
      Events.instance().raiseEvent("org.jboss.seam.postRemoveVariable." + name);
	}

	public String[] getNames() 
   {
		Enumeration names = session.getAttributeNames();
		ArrayList<String> results = new ArrayList<String>();
      String prefix = ScopeType.CONVERSATION.getPrefix();
      while ( names.hasMoreElements() ) {
         String name = (String) names.nextElement();
         if ( !name.startsWith(prefix) )
         {
            results.add(name);
            //results.add( name.substring(prefix.length()) );
         }
      }
		return results.toArray(new String[]{});
	}
   
   public Object get(Class clazz)
   {
      return get( Seam.getComponentName(clazz) );
   }

   public void flush() {
      for ( String name: getNames() )
      {
         Object attribute = session.getAttribute(name);
         if ( attribute instanceof Mutable && ( (Mutable) attribute ).clearDirty() )
         {
            session.setAttribute(name, attribute);
         }
      }      
   }
  
}
