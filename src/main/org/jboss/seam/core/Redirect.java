package org.jboss.seam.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

@Name("redirect")
@Intercept(InterceptionType.NEVER)
@Scope(ScopeType.CONVERSATION)
public class Redirect implements Serializable
{
   private String viewId;
   private Map<String, Object> parameters = new HashMap<String, Object>();
   
   public String getViewId()
   {
      return viewId;
   }
   
   public void setViewId(String viewId)
   {
      this.viewId = viewId;
   }
   
   public Map<String, Object> getParameters()
   {
      return parameters;
   }
   
   public void setParameter(String name, Object value)
   {
      parameters.put(name, value);
   }
   
   public void captureCurrentRequest()
   {
      parameters.clear();
      FacesContext context = FacesContext.getCurrentInstance();
      parameters.putAll( context.getExternalContext().getRequestParameterMap() );
      viewId = context.getViewRoot().getViewId();
   }
   
   public void execute()
   {
      // only include the conv-id if the Seam redirect filter is installed
      Manager.instance().redirect(viewId, parameters, false);
   }
   
   public static Redirect instance()
   {
      if ( !Contexts.isConversationContextActive() )
      {
         throw new IllegalStateException("No active conversation context");
      }
      return (Redirect) Component.getInstance(Redirect.class, ScopeType.CONVERSATION, true);
   }
   
}
