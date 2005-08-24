/*
 * JBoss, Home of Professional Open Source  
 * 
 * Distributable under LGPL license. 
 * See terms of license at gnu.org.  
 */
package org.jboss.seam.contexts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;

/**
 * A conversation context is a logical context that last longer than 
 * a request but shorter than a login session
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class ConversationContext implements Context, Serializable {

	private final HttpSession session;
   private final String id;
   
   public String getKey(String name)
   {
      return getPrefix() + name;
   }

   private String getPrefix()
   {
      return ScopeType.CONVERSATION.getPrefix() + '#' + id + '$';
   }
   
   public static String getId(Context conversationContext)
   {
      return ( (ConversationContext) conversationContext ).id;
   }
   
   public ConversationContext(HttpSession session)
   {
      this.session = session;
      id = Id.nextId();
   }

   public ConversationContext(HttpSession session, String id)
   {
      this.session = session;
      this.id = id;
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
            results.add( name.substring( prefix.length() ) );
         }
      }
      return results.toArray(new String[]{});
   }
   
   public String toString()
   {
      return "ConversationContext(" + id + ")";
   }

   public Object get(Class clazz)
   {
      return get( Seam.getComponentName(clazz) );
   }
}
