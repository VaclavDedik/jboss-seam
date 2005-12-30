/*
 * JBoss, Home of Professional Open Source  
 * 
 * Distributable under LGPL license. 
 * See terms of license at gnu.org.  
 */
package org.jboss.seam.contexts;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;

/**
 * A conversation context is a logical context that lasts longer than 
 * a request but shorter than a login session
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class PageContext implements Context {

   private Map<String, Object> previousPageMap;
   private Map<String, Object> nextPageMap;
   
   public PageContext()
   {
      previousPageMap = (Map<String, Object>) getAttributeMap().remove( ScopeType.PAGE.getPrefix() );
      if (previousPageMap==null) previousPageMap = new HashMap<String, Object>();
      nextPageMap = new HashMap<String, Object>();
   }

   public ScopeType getType()
   {
      return ScopeType.CONVERSATION;
   }
   
	public Object get(String name) 
   {
      Object next = nextPageMap.get(name);
      if (next==null)
      {
         return previousPageMap.get(name);
      }
      else
      {
         return next;
      }
	}

	public void set(String name, Object value) 
   {
		nextPageMap.put(name, value);
	}

	public boolean isSet(String name) 
   {
		return get(name)!=null;
	}
   
	public void remove(String name) 
   {
      nextPageMap.remove(name);
	}

   public String[] getNames() {
      return previousPageMap.keySet().toArray( new String[]{} );
   }
   
   public String toString()
   {
      return "PageContext";
   }

   public Object get(Class clazz)
   {
      return get( Seam.getComponentName(clazz) );
   }

   public void flush()
   {
      getAttributeMap().put( ScopeType.PAGE.getPrefix(), nextPageMap );
   }

   private Map getAttributeMap()
   {
      return FacesContext.getCurrentInstance().getViewRoot().getAttributes();
   }

}
