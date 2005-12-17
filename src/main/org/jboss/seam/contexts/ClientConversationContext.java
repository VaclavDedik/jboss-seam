/*
 * JBoss, Home of Professional Open Source  
 * 
 * Distributable under LGPL license. 
 * See terms of license at gnu.org.  
 */
package org.jboss.seam.contexts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

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
public class ClientConversationContext implements Context, Serializable {

   private Map<String, Object> temporarySession;
   
   public ClientConversationContext()
   {
      temporarySession = (Map<String, Object>) getAttributeMap().remove( ScopeType.CONVERSATION.getPrefix() );
      if (temporarySession==null) temporarySession = new HashMap<String, Object>();
   }

   public ScopeType getType()
   {
      return ScopeType.CONVERSATION;
   }
   
	public Object get(String name) 
   {
      return temporarySession.get(name);
	}

	public void set(String name, Object value) 
   {
		temporarySession.put(name, value);
	}

	public boolean isSet(String name) 
   {
		return get(name)!=null;
	}
   
	public void remove(String name) 
   {
      temporarySession.remove(name);
	}

   public String[] getNames() {
      return temporarySession.keySet().toArray( new String[]{} );
   }
   
   public String toString()
   {
      return "ClientConversationContext";
   }

   public Object get(Class clazz)
   {
      return get( Seam.getComponentName(clazz) );
   }

   public void flush()
   {
      if ( Manager.instance().isLongRunningConversation() )
      {
         getAttributeMap().put( ScopeType.CONVERSATION.getPrefix(), temporarySession );
      }
   }

   private Map getAttributeMap()
   {
      return FacesContext.getCurrentInstance().getViewRoot().getAttributes();
   }

}
