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
   
   private List<Message> facesMessages = new ArrayList<Message>();
   private Map<String, List<Message>> keyedFacesMessages = new HashMap<String, List<Message>>();
   
   /**
    * Workaround for non-serializability of
    * JSF FacesMessage.Severity class.
    * 
    * @author Gavin King
    *
    */
   class Message implements Serializable
   {
      private String summary;
      private String detail;
      private int severityOrdinal;
      
      Message(FacesMessage fm)
      {
         summary = fm.getSummary();
         detail = fm.getDetail();
         severityOrdinal = fm.getSeverity().getOrdinal();
      }
      
      FacesMessage toFacesMessage()
      {
         Severity severity = null;
         for (Object o : FacesMessage.VALUES) {
            severity = (Severity) o;
            if (severity.getOrdinal() == severityOrdinal) {
               break;
            }
         }
         return new FacesMessage(severity, summary, detail );
      }
   }

   public void beforeRenderResponse() 
   {
      for (Message message: facesMessages)
      {
         FacesContext.getCurrentInstance().addMessage( null,message.toFacesMessage() );
      }
      for ( Map.Entry<String, List<Message>> entry: keyedFacesMessages.entrySet() )
      {
         for ( Message msg: entry.getValue() )
         {
            FacesContext.getCurrentInstance().addMessage( entry.getKey(), msg.toFacesMessage() );
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
      facesMessages.add( new Message(facesMessage) );
   }
   
   /**
    * Add a FacesMessage instance to a particular component id
    * @param id a JSF component id
    */
   public void addToControl(String id, FacesMessage facesMessage)
   {
      String clientId = getClientId(id);
      List<Message> list = keyedFacesMessages.get(clientId);
      if (list==null)
      {
         list = new ArrayList<Message>();
         keyedFacesMessages.put(clientId, list);
      }
      list.add( new Message(facesMessage) );
   }
   
   /**
    * Add a templated FacesMessage that will be used
    * the next time a page is rendered.
    */
   public void add(String messageTemplate, Object... params)
   {
      addToTasks(FacesMessage.SEVERITY_INFO, null, messageTemplate, params);
   }
   
   /**
    * Add a templated FacesMessage that will be used
    * the next time a page is rendered.
    */
   public void add(Severity severity, String messageTemplate, Object... params)
   {
      addToTasks(severity, null, messageTemplate, params);
   }
   
   /**
    * Add a templated FacesMessage to a particular JSF control
    * @param id a JSF component id
    */
   public void addToControl(String id, String messageTemplate, Object... params)
   {
      addToControl(id, FacesMessage.SEVERITY_INFO, messageTemplate, params);
   }
   
   /**
    * Add a templated FacesMessage to a particular JSF control
    * @param id a JSF component id
    */
   public void addToControl(String id, Severity severity, String messageTemplate, Object... params)
   {
      addToTasks(id, severity, null, messageTemplate, params);
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
      addFromResourceBundleOrDefault(severity, key, key, params);
   }
   
   /**
    * Add a templated FacesMessage to a particular component id by looking 
    * for the message template in the resource bundle. If it is missing, use
    * the given message template.
    */
   public void addFromResourceBundleOrDefault(String key, String defaultMessageTemplate, Object... params)
   {
      addFromResourceBundleOrDefault(FacesMessage.SEVERITY_INFO, key, defaultMessageTemplate, params);
   }

   /**
    * Add a templated FacesMessage to a particular component id by looking 
    * for the message template in the resource bundle. If it is missing, use
    * the given message template.
    */
   public void addFromResourceBundleOrDefault(Severity severity, String key, String defaultMessageTemplate, Object... params)
   {
      addToTasks(severity, key, defaultMessageTemplate, params);
   }

   /**
    * Add a templated FacesMessage to a particular component id by looking 
    * for the message template in the resource bundle. 
    */
   public void addToControlFromResourceBundle(String id, String key, Object... params)
   {
      addToControlFromResourceBundle(id, FacesMessage.SEVERITY_INFO, key, params);
   }
   
   /**
    * Add a templated FacesMessage to a particular component id by looking 
    * for the message template in the resource bundle. 
    */
   public void addToControlFromResourceBundle(String id, Severity severity, String key, Object... params)
   {
      addToControlFromResourceBundleOrDefault(id, severity, key, key, params);
   }
   
   /**
    * Add a templated FacesMessage to a particular component id by looking 
    * for the message template in the resource bundle. If it is missing, use
    * the given message template.
    */
   public void addToControlFromResourceBundleOrDefault(String id, String key, String defaultMessageTemplate, Object... params)
   {
      addToControlFromResourceBundleOrDefault(id, FacesMessage.SEVERITY_INFO, key, defaultMessageTemplate, params);
   }

   /**
    * Add a templated FacesMessage to a particular component id by looking 
    * for the message template in the resource bundle. If it is missing, use
    * the given message template.
    */
   public void addToControlFromResourceBundleOrDefault(String id, Severity severity, String key, String defaultMessageTemplate, Object... params)
   {
      addToTasks(id, severity, key, defaultMessageTemplate, params);
   }

   private static String interpolateBundleMessage(String key, String defaultMessageTemplate)
   {
      String messageTemplate = defaultMessageTemplate;
      if ( key!=null )
      {
         java.util.ResourceBundle resourceBundle = ResourceBundle.instance();
         if ( resourceBundle!=null ) 
         {
            try
            {
               String bundleMessage = resourceBundle.getString(key);
               if (bundleMessage!=null) messageTemplate = bundleMessage;
            }
            catch (MissingResourceException mre) {} //swallow
         }
      }
      return messageTemplate;
   }

   public void add(InvalidValue[] ivs)
   {
      for (InvalidValue iv: ivs)
      {
         add(iv);
      }
   }
   
   public void addToControls(InvalidValue[] ivs)
   {
      for (InvalidValue iv: ivs)
      {
         addToControl(iv);
      }
   }
   
   public void add(InvalidValue iv)
   {
      add( FacesMessage.SEVERITY_WARN, iv.getMessage() );
   }
   
   public void addToControl(InvalidValue iv)
   {
      addToControl( iv.getPropertyName(), iv );
   }
   
   public void addToControl(String id, InvalidValue iv)
   {
      addToControl( id, FacesMessage.SEVERITY_WARN, iv.getMessage() );
   }
   
   public static FacesMessage createFacesMessage(Severity severity, String messageTemplate, Object... params)
   {
      return new FacesMessage( severity, Interpolator.instance().interpolate(messageTemplate, params), null );
   }
   
   public static FacesMessage createFacesMessage(Severity severity, String key, String defaultMessageTemplate, Object... params)
   {
      return createFacesMessage( severity, interpolateBundleMessage(key, defaultMessageTemplate), params );
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
  
   private void addToTasks(final Severity severity, final String key, final String messageTemplate, final Object... params)
   {
      getTasks().add( new Runnable() {
         public void run() { add( createFacesMessage(severity, key, messageTemplate, params) ); }
      } );
   }
      
   private void addToTasks(final String id, final Severity severity, final String key, final String messageTemplate, final Object... params)
   {
      getTasks().add( new Runnable() {
         public void run() { addToControl( id, createFacesMessage(severity, key, messageTemplate, params) ); }
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
   
}
