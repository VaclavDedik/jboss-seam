package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;
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
import org.jboss.seam.annotations.Install;
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
@Name("org.jboss.seam.core.facesMessages")
@Install(precedence=BUILT_IN)
@Intercept(NEVER)
public class FacesMessages implements Serializable
{
   private static final long serialVersionUID = -5395975397632138270L;
   private transient List<Runnable> tasks;
   
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
   
   /**
    * Get all faces messages that have already been added
    * to the context.
    * 
    * @return a list of messages
    */
   public List<FacesMessage> getCurrentMessages()
   {
      List<FacesMessage> result = new ArrayList<FacesMessage>();
      Iterator<FacesMessage> iter = FacesContext.getCurrentInstance().getMessages();
      while ( iter.hasNext() )
      {
         result.add( iter.next() );
      }
      return result;
   }
   
   /**
    * Get all faces global messages that have already been added
    * to the context.
    * 
    * @return a list of global messages
    */
   public List<FacesMessage> getCurrentGlobalMessages()
   {
      List<FacesMessage> result = new ArrayList<FacesMessage>();
      Iterator<FacesMessage> iter = FacesContext.getCurrentInstance().getMessages(null);
      while ( iter.hasNext() )
      {
         result.add( iter.next() );
      }
      return result;
   }
   
   private void runTasks()
   {
      if (tasks!=null)
      {
         for (Runnable task: tasks) task.run();
         tasks.clear();
      }
   }
   
   public static void afterPhase()
   {
      if ( Contexts.isConversationContextActive() )
      {
         FacesMessages instance = (FacesMessages) Component.getInstance(FacesMessages.class, ScopeType.CONVERSATION, false);
         if (instance!=null) instance.runTasks();
      }
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
   public void add(String id, String messageTemplate, Object... params)
   {
      add(id, FacesMessage.SEVERITY_INFO, messageTemplate, params);
   }
   
   /**
    * Add a templated FacesMessage to a particular component id
    * @param id a JSF component id
    */
   public void add(final String id, final Severity severity, final String messageTemplate, final Object... params)
   {
      getTasks().add( new Runnable() {
         public void run() { add( id, createFacesMessage(severity, messageTemplate, params) ); }
      } );
   }

   public static FacesMessages instance()
   {
      if ( !Contexts.isConversationContextActive() )
      {
         throw new IllegalStateException("No active conversation context");
      }
      return (FacesMessages) Component.getInstance(FacesMessages.class, ScopeType.CONVERSATION);
   }
   
   /**
    * Add a templated FacesMessage that will be used
    * the next time a page is rendered.
    */
   public void add(String messageTemplate, Object... params)
   {
      add(FacesMessage.SEVERITY_INFO, messageTemplate, params);
   }
   
   /**
    * Add a templated FacesMessage that will be used
    * the next time a page is rendered.
    */
   public void add(final Severity severity, final String messageTemplate, final Object... params)
   {
      getTasks().add( new Runnable() {
         public void run() { add( createFacesMessage(severity, messageTemplate, params) ); }
      } );
   }
   
   /**
    * Add a templated FacesMessage by looking for the message
    * template in the resource bundle. 
    */
   public void addFromResourceBundle(String key, Object... params)
   {
      addFromResourceBundle(FacesMessage.SEVERITY_INFO, key, params);
   }
   
   /**
    * Add a templated FacesMessage by looking for the message
    * template in the resource bundle. 
    */
   public void addFromResourceBundle(Severity severity, String key, Object... params)
   {
      addFromResourceBundle(severity, key, key, params);
   }
   
   /**
    * Add a templated FacesMessage to a particular component id by looking 
    * for the message template in the resource bundle. 
    */
   public void addFromResourceBundle(String id, String key, Object... params)
   {
      addFromResourceBundle(id, FacesMessage.SEVERITY_INFO, key, params);
   }
   
   /**
    * Add a templated FacesMessage to a particular component id by looking 
    * for the message template in the resource bundle. 
    */
   public void addFromResourceBundle(String id, Severity severity, String key, Object... params)
   {
      addFromResourceBundle(id, severity, key, key, params);
   }
   
   /**
    * Add a templated FacesMessage to a particular component id by looking 
    * for the message template in the resource bundle. If it is missing, use
    * the given message template.
    */
   public void addFromResourceBundle(Severity severity, String key, String defaultMessageTemplate, Object... params)
   {
      add( severity, interpolateBundleMessage(key, defaultMessageTemplate), params );
   }

   /**
    * Add a templated FacesMessage to a particular component id by looking 
    * for the message template in the resource bundle. If it is missing, use
    * the given message template.
    */
   public void addFromResourceBundle(String id, Severity severity, String key, String defaultMessageTemplate, Object... params)
   {
      add( id, severity, interpolateBundleMessage(key, defaultMessageTemplate), params );
   }

   private String interpolateBundleMessage(String key, String defaultMessageTemplate)
   {
      String messageTemplate = defaultMessageTemplate;
      java.util.ResourceBundle resourceBundle = ResourceBundle.instance();
      if ( resourceBundle!=null && key!=null ) 
      {
         try
         {
            String bundleMessage = resourceBundle.getString(key);
            if (bundleMessage!=null) messageTemplate = bundleMessage;
         }
         catch (MissingResourceException mre) {} //swallow
      }
      return messageTemplate;
   }

   public void add(String id, InvalidValue iv)
   {
      add( id, FacesMessage.SEVERITY_WARN, iv.getMessage() );
   }
   
   public void add(InvalidValue[] ivs)
   {
      for (InvalidValue iv: ivs)
      {
         add(iv);
      }
   }
   
   public void add(InvalidValue iv)
   {
      add( iv.getPropertyName(), iv );
   }
   
   public static FacesMessage createFacesMessage(Severity severity, String messageTemplate, Object... params)
   {
      return new FacesMessage( severity, Interpolator.instance().interpolate(messageTemplate, params), null );
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
   
   private List<Runnable> getTasks()
   {
      if (tasks==null)
      {
         tasks = new ArrayList<Runnable>();
      }
      return tasks;
   }
  
}
