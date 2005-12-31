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
import javax.faces.event.PhaseId;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;

/**
 * The page context allows you to store state during a request that
 * renders a page, and access that state from any postback request
 * that originates from that page. The state is destroyed at the 
 * end of the second request. During the RENDER_RESPONSE phase,
 * the page context instance refers to the page that is about to
 * be rendered. Prior to the INVOKE_APPLICATION phase, it refers
 * to the page that was the source of the request. During the
 * INVOKE_APPLICATION phase, set() and remove() manipulate the
 * context of the page that is about to be rendered, while get()
 * returns values from the page that was the source of the request.
 * 
 * @author Gavin King
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
      return getCurrentReadableMap().get(name);
	}
   
   public boolean isSet(String name) 
   {
      return get(name)!=null;
   }
   
   private Map getCurrentReadableMap()
   {
      return Lifecycle.getPhaseId().compareTo(PhaseId.INVOKE_APPLICATION) > 0 ?
            nextPageMap : previousPageMap;
   }

   private Map getCurrentWritableMap()
   {
      return Lifecycle.getPhaseId().compareTo(PhaseId.INVOKE_APPLICATION) < 0 ?
            previousPageMap : nextPageMap;
   }

	public void set(String name, Object value) 
   {
      getCurrentWritableMap().put(name, value);
	}

	public void remove(String name) 
   {
      getCurrentWritableMap().remove(name);
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
