package org.jboss.seam.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import org.jboss.seam.core.Interpolator;
import org.jboss.seam.core.Locale;

/**
 * Metadata about page actions, page parameters, action navigation,
 * resource bundle, etc, for a particular JSF view id.
 */
public final class Page
{
   private final String viewId;
   private String description;
   private Integer timeout;
   private String noConversationViewId;
   private String resourceBundleName;
   private boolean switchEnabled = true;
   private List<Param> parameters = new ArrayList<Param>();
   private List<Action> actions = new ArrayList<Action>();
   private Map<String, ActionNavigation> navigations = new HashMap<String, ActionNavigation>();
   private ActionNavigation defaultNavigation;
   private boolean conversationRequired;
   private ConversationControl conversationControl = new ConversationControl();
   
   public Page(String viewId)
   {
      this.viewId = viewId;
      if (viewId!=null)
      {
         int loc = viewId.lastIndexOf('.');
         if ( loc>0 && viewId.startsWith("/") )
         {
            this.setResourceBundleName( viewId.substring(1, loc) );
         }
      }
   }
   
   public java.util.ResourceBundle getResourceBundle()
   {
      String resourceBundleName = getResourceBundleName();
      if (resourceBundleName==null)
      {
         return null;
      }
      else
      {
         try
         {
            return java.util.ResourceBundle.getBundle(
                  resourceBundleName, 
                  Locale.instance(), 
                  Thread.currentThread().getContextClassLoader()
               );
         }
         catch (MissingResourceException mre)
         {
            return null;
         }
      }
   }
   
   @Override
   public String toString()
   {
      return "Page(" + getViewId() + ")";
   }

   public String getViewId()
   {
      return viewId;
   }

   public String renderDescription()
   {
      return Interpolator.instance().interpolate( getDescription() );
   }
   
   public void setDescription(String description)
   {
      this.description = description;
   }

   public String getDescription()
   {
      return description;
   }

   public void setTimeout(Integer timeout)
   {
      this.timeout = timeout;
   }

   public Integer getTimeout()
   {
      return timeout;
   }

   public void setNoConversationViewId(String noConversationViewId)
   {
      this.noConversationViewId = noConversationViewId;
   }

   public String getNoConversationViewId()
   {
      return noConversationViewId;
   }

   public void setResourceBundleName(String resourceBundleName)
   {
      this.resourceBundleName = resourceBundleName;
   }

   public String getResourceBundleName()
   {
      return resourceBundleName;
   }

   public void setSwitchEnabled(boolean switchEnabled)
   {
      this.switchEnabled = switchEnabled;
   }

   public boolean isSwitchEnabled()
   {
      return switchEnabled;
   }

   public List<Param> getParameters()
   {
      return parameters;
   }

   public Map<String, ActionNavigation> getNavigations()
   {
      return navigations;
   }

   public boolean hasDescription()
   {
      return description!=null;
   }
   
   public boolean isConversationRequired()
   {
      return conversationRequired;
   }

   public void setConversationRequired(boolean conversationRequired)
   {
      this.conversationRequired = conversationRequired;
   }

   public ActionNavigation getDefaultNavigation()
   {
      return defaultNavigation;
   }

   public void setDefaultNavigation(ActionNavigation defaultActionOutcomeMapping)
   {
      this.defaultNavigation = defaultActionOutcomeMapping;
   }

   public ConversationControl getConversationControl()
   {
      return conversationControl;
   }

   public List<Action> getActions()
   {
      return actions;
   }

}