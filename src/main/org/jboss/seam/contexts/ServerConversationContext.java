/*
 * JBoss, Home of Professional Open Source  
 * 
 * Distributable under LGPL license. 
 * See terms of license at gnu.org.  
 */
package org.jboss.seam.contexts;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.core.Manager;

/**
 * A conversation context is a logical context that lasts longer than 
 * a request but shorter than a login session
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class ServerConversationContext implements Context {

   private final Session session;
   private final Map<String, Object> additions = new HashMap<String, Object>();
   private final Set<String> removals = new HashSet<String>();
   private final String id;
   private final LinkedList<String> idStack;
   
   private LinkedList<String> getIdStack()
   {
      return idStack==null ? Manager.instance().getCurrentConversationIdStack() : idStack;
   }
   
   private String getId()
   {
      return id==null ? Manager.instance().getCurrentConversationId() : id;
   }
   
   public ScopeType getType()
   {
      return ScopeType.CONVERSATION;
   }

   private String getKey(String name)
   {
      return getPrefix( getId() ) + name;
   }

   private String getKey(String name, String id)
   {
      return getPrefix(id) + name;
   }

   private String getPrefix(String id)
   {
      return ScopeType.CONVERSATION.getPrefix() + '#' + id + '$';
   }

   public ServerConversationContext(Session session)
   {
      this.session = session;
      id = null;
      idStack = null;
   }
      
   public ServerConversationContext(Session session, String id)
   {
      this.session = session;
      this.id = id;
      this.idStack = new LinkedList<String>();
      idStack.add(id);
   }
      
	public Object get(String name) {
      Object result = additions.get(name);
      if (result!=null) return result;
      if ( removals.contains(name) ) return null;
      LinkedList<String> stack = getIdStack();
      if (stack==null)
      {
         return session.getAttribute( getKey(name) );
      }
      else
      {
         for ( String id: stack )
         {
            result = session.getAttribute( getKey(name, id) );
            if (result!=null) return result;
         }
         return null;
      }
	}

   public void set(String name, Object value) {
      if (value==null)
      {
         //yes, we need this
         remove(name);
      }
      else
      {
         removals.remove(name);
         additions.put(name, value);
      }
	}

	public boolean isSet(String name) {
		return get(name)!=null;
	}
   
	public void remove(String name) {
      additions.remove(name);
      removals.add(name);
	}

   public String[] getNames() {
      Set<String> results = getNamesFromSession();
      results.addAll( additions.keySet() ); //after, to override
      return results.toArray(new String[]{});
   }

   private Set<String> getNamesFromSession() {
      HashSet<String> results = new HashSet<String>();
      Enumeration names = session.getAttributeNames();
      String prefix = getPrefix( getId() );
      while ( names.hasMoreElements() ) {
         String name = (String) names.nextElement();
         if ( name.startsWith(prefix) )
         {
            name = name.substring( prefix.length() );
            if ( !removals.contains(name) ) results.add(name);
         }
      }
      return results;
   }

   public Object get(Class clazz)
   {
      return get( Seam.getComponentName(clazz) );
   }
   
   public void clear()
   {
      additions.clear();
      removals.addAll( getNamesFromSession() );
   }

   public void flush()
   {
      boolean longRunning = Manager.instance().isLongRunningConversation() ||
            !Manager.instance().getCurrentConversationId().equals( getId() );
      if ( longRunning )
      {
         for (String name: removals)
         {
            session.removeAttribute( getKey(name) );
         }
         removals.clear();
         for (Map.Entry<String, Object> entry: additions.entrySet())
         {
            session.setAttribute( getKey( entry.getKey() ), entry.getValue() );
         }
         additions.clear();
      }
      else
      {
         //TODO: for a pure temporary conversation, this is unnecessary, optimize it
         for (String name: getNamesFromSession())
         {
            session.removeAttribute( getKey(name) );
         }
      }
   }

   public String toString()
   {
      return "ConversationContext(" + getId() + ")";
   }

}
