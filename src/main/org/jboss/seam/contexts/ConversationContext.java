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
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.ExternalContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.Session;
import org.jboss.seam.core.Manager;

/**
 * A conversation context is a logical context that lasts longer than 
 * a request but shorter than a login session
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class ConversationContext implements Context, Serializable {

   private final Session session;
   private final Map<String, Object> temporarySession = new HashMap<String, Object>();
   private final String id;
   
   public ScopeType getType()
   {
      return ScopeType.CONVERSATION;
   }

   public String getKey(String name)
   {
      return getPrefix() + name;
   }

   private String getPrefix()
   {
      return ScopeType.CONVERSATION.getPrefix() + '#' + id + '$';
   }

   public ConversationContext(ExternalContext externalContext, String id)
   {
      this.session = (Session) externalContext.getSession(true);
      this.id = id;
   }
   
	public Object get(String name) {
      Object temp = temporarySession.get(name);
      if (temp!=null) return temp;
		return session.getAttribute( getKey(name) );
	}

	public void set(String name, Object value) {
		temporarySession.put(name, value);
      //session.setAttribute( getKey(name), value );
	}

	public boolean isSet(String name) {
		return get(name)!=null;
	}
   
	public void remove(String name) {
      temporarySession.remove(name);
      //hum, could we queue this one also?
		session.removeAttribute( getKey(name) );
	}

   public String[] getNames() {
      ArrayList<String> results = new ArrayList<String>();
      Enumeration names = session.getAttributeNames();
      String prefix = getPrefix();
      while ( names.hasMoreElements() ) {
         String name = (String) names.nextElement();
         if ( name.startsWith(prefix) )
         {
            results.add( name.substring( prefix.length() ) );
         }
      }
      results.addAll( temporarySession.keySet() ); //after, to override
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
   
   public void clear()
   {
      temporarySession.clear();
      //the conversation is being destroyed, clean up its state
      for (String name: getNames())
      {
         session.removeAttribute( getKey(name) );
      }
   }

   public void flush()
   {
      if ( Manager.instance().isLongRunningConversation() )
      {
         for (Map.Entry<String, Object> entry: temporarySession.entrySet())
         {
            session.setAttribute( getKey( entry.getKey() ), entry.getValue() );
         }
      }
      else
      {
         //TODO: for a pure temporary conversation, this is unnecessary, optimize it
         clear();
      }
   }
}
