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

/**
 * A conversation context is a logical context that last longer than 
 * a request but shorter than a login session
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class ConversationContext implements Context, Serializable {

	private final Map<String, Object> map = new HashMap<String, Object>();

	public Object get(String name) {
		return map.get(name);
	}

	public void set(String name, Object value) {
		map.put( name, value );
	}

	public boolean isSet(String name) {
		return map.containsKey(name);
	}
   
	public void remove(String name) {
		map.remove( name );
	}

	public String[] getNames() {
		return map.keySet().toArray(new String[0]);
	}
   
   public String toString()
   {
      return "ConversationContext" + map.toString();
   }

}
