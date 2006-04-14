package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.hibernate.validator.InvalidValue;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.Template;

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
   private Map<String, List<FacesMessage>> keyedFacesMessages = new HashMap<String, List<FacesMessage>>();

   public void beforeRenderResponse() 
   {
      for (FacesMessage facesMessage: facesMessages)
      {
         FacesContext.getCurrentInstance().addMessage(null, facesMessage);
      }
      for ( Map.Entry<String, List<FacesMessage>> entry: keyedFacesMessages.entrySet() )
      {
         for ( FacesMessage msg: entry.getValue() )
         {
            FacesContext.getCurrentInstance().addMessage( entry.getKey(), msg );
         }
      }
      clear();
   }
   
   public void clear()
   {
      facesMessages.clear();
      keyedFacesMessages.clear();
   }
   
   /**
    * Add a FacesMessage that will be used
    * the next time a page is rendered.
    */
   public void add(FacesMessage facesMessage) 
   {
      facesMessages.add(facesMessage);
   }
   
   /**
    * Add a FacesMessage instance to a particular component id
    * @param id a JSF component id
    */
   public void add(String id, FacesMessage facesMessage)
   {
      String clientId = getClientId(id);
      List<FacesMessage> list = keyedFacesMessages.get(clientId);
      if (list==null)
      {
         list = new ArrayList<FacesMessage>();
         keyedFacesMessages.put(clientId, list);
      }
      list.add(facesMessage);
   }
   
   /**
    * Add a templated FacesMessage to a particular component id
    * @param id a JSF component id
    */
   public void add(String id, String messageTemplate)
   {
      add(id, FacesMessage.SEVERITY_INFO, messageTemplate);
   }
   
   /**
    * Add a templated FacesMessage to a particular component id
    * @param id a JSF component id
    */
   public void add(String id, Severity severity, String messageTemplate)
   {
      add( id, createFacesMessage(severity, messageTemplate) );
   }

   public static FacesMessages instance()
   {
      if ( !Contexts.isConversationContextActive() )
      {
         throw new IllegalStateException("No active conversation context");
      }
      return (FacesMessages) Component.getInstance(FacesMessages.class, ScopeType.CONVERSATION, true);
   }
   
   /**
    * Add a templated FacesMessage that will be used
    * the next time a page is rendered.
    */
   public void add(String messageTemplate)
   {
      add(FacesMessage.SEVERITY_INFO, messageTemplate);
   }
   
   /**
    * Add a templated FacesMessage that will be used
    * the next time a page is rendered.
    */
   public void add(Severity severity, String messageTemplate)
   {
      add( createFacesMessage(severity, messageTemplate) );
   }
   
   /**
    * Add a templated FacesMessage by looking for the message
    * template in the resource bundle. 
    */
   public void addFromResourceBundle(String key)
   {
      addFromResourceBundle(FacesMessage.SEVERITY_INFO, key);
   }
   
   /**
    * Add a templated FacesMessage by looking for the message
    * template in the resource bundle. 
    */
   public void addFromResourceBundle(Severity severity, String key)
   {
      addFromResourceBundle(severity, key, key);
   }
   
   /**
    * Add a templated FacesMessage by looking for the message
    * template in the resource bundle. If it is missing, use
    * the given message template.
    */
   public void addFromResourceBundle(Severity severity, String key, String defaultMessageTemplate)
   {
      String messageTemplate = defaultMessageTemplate;
      java.util.ResourceBundle resourceBundle = ResourceBundle.instance();
      if (resourceBundle!=null) 
      {
         try
         {
            String bundleMessage = resourceBundle.getString(key);
            if (bundleMessage!=null) messageTemplate = bundleMessage;
         }
         catch (MissingResourceException mre) {} //swallow
      }
      add(severity, messageTemplate);
   }

   public void add(InvalidValue iv)
   {
      add( iv.getPropertyName(), new FacesMessage( iv.getMessage() ) );
   }
   
   private FacesMessage createFacesMessage(Severity severity, String messageTemplate)
   {
      return new FacesMessage( severity, Template.render(messageTemplate), null );
   }
   
   private String getClientId(String id)
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      return getClientId( facesContext.getViewRoot(), id, facesContext);
   }

   private static String getClientId(UIComponent component, String id, FacesContext facesContext)
   {
      String componentId = component.getId();
      if (componentId!=null && componentId.equals(id))
      {
         return component.getClientId(facesContext);
      }
      else
      {
         Iterator iter = component.getFacetsAndChildren();
         while ( iter.hasNext() )
         {
            UIComponent child = (UIComponent) iter.next();
            String clientId = getClientId(child, id, facesContext);
            if (clientId!=null) return clientId;
         }
         return null;
      }
   }
  
}
