package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

/**
 * A Seam component that TBD.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.CONVERSATION)
@Name("facesMessages")
@Intercept(NEVER)
public class FacesMessages 
{
   
   private List<FacesMessage> facesMessages = new ArrayList<FacesMessage>();

   public void beforeRenderResponse() 
   {
      for (FacesMessage facesMessage: facesMessages)
      {
         FacesContext.getCurrentInstance().addMessage(null, facesMessage);
      }
      clear();
   }
   
   public void clear()
   {
      facesMessages.clear();
   }
   
   /**
    * Add a FacesMessage that will be used
    * the next time a page is rendered.
    */
   public void add(FacesMessage facesMessage) 
   {
      facesMessages.add(facesMessage);
   }
   
   public static FacesMessages instance()
   {
      if ( !Contexts.isConversationContextActive() )
      {
         throw new IllegalStateException("No active conversation context");
      }
      return (FacesMessages) Component.getInstance(FacesMessages.class, ScopeType.CONVERSATION, true);
   }
}
