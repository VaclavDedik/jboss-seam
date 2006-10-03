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
import org.jboss.seam.core.Events;

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
      return isRenderResponsePhase() ?
            nextPageMap : previousPageMap;
   }

   private Map getCurrentWritableMap()
   {
      return isBeforeInvokeApplicationPhase() ?
            previousPageMap : nextPageMap;
   }

	public void set(String name, Object value) 
   {
      Events.instance().raiseEvent("org.jboss.seam.preSetVariable." + name);
      getCurrentWritableMap().put(name, value);
      Events.instance().raiseEvent("org.jboss.seam.postSetVariable." + name);
	}

	public void remove(String name) 
   {
      Events.instance().raiseEvent("org.jboss.seam.preRemoveVariable." + name);
      getCurrentWritableMap().remove(name);
      Events.instance().raiseEvent("org.jboss.seam.postRemoveVariable." + name);
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

   /**
    * Put the context variables in the faces view root.
    */
   public void flush()
   {
      getAttributeMap().put( ScopeType.PAGE.getPrefix(), nextPageMap );
   }

   private static Map getAttributeMap()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      if (facesContext==null)
      {
         throw new IllegalStateException("no FacesContext bound to current thread");
      }
      return facesContext.getViewRoot().getAttributes();
   }

   private static PhaseId getPhaseId()
   {
      PhaseId phaseId = Lifecycle.getPhaseId();
      if (phaseId==null)
      {
         throw new IllegalStateException("No phase id bound to current thread");
      }
      return phaseId;
   }

   private static boolean isBeforeInvokeApplicationPhase()
   {
      return getPhaseId().compareTo(PhaseId.INVOKE_APPLICATION) < 0;
   }

   private static boolean isRenderResponsePhase()
   {
      return getPhaseId().compareTo(PhaseId.INVOKE_APPLICATION) > 0;
   }

}
