package org.jboss.seam.pages;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import javax.faces.context.FacesContext;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.core.Locale;
import org.jboss.seam.core.Pages;
import org.jboss.seam.security.Identity;
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
   private List<Input> inputs = new ArrayList<Input>();
   private List<Action> actions = new ArrayList<Action>();
   private Map<String, Navigation> navigations = new HashMap<String, Navigation>();
   private Navigation defaultNavigation;
   private boolean conversationRequired;
   private boolean loginRequired;
   private ConversationControl conversationControl = new ConversationControl();
   private TaskControl taskControl = new TaskControl();
   private ProcessControl processControl = new ProcessControl();
   private ConversationIdParameter conversationIdParameter;
   
   /**
    * The scheme (http/https) required by this page.
    */
   private String scheme;
   
   /**
    * Indicates whether this view id has a security restriction.  
    */
   private boolean restricted;
   
   /**
    * A security restriction expression to evaluate when requesting this view id.
    * If the view is restricted but no restriction expression is set, the implied
    * permission restriction will be name="[viewid]", action="[get or post]" 
    */
   private String restriction;
   
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
      
      conversationIdParameter = new SyntheticConversationIdParameter();
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
   public Map<String, Navigation> getNavigations()
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
   public Navigation getDefaultNavigation()
   {
      return defaultNavigation;
   }
   public void setDefaultNavigation(Navigation defaultActionOutcomeMapping)
   {
      this.defaultNavigation = defaultActionOutcomeMapping;
   }
   public ConversationControl getConversationControl()
   {
      return conversationControl;
   }
   public TaskControl getTaskControl()
   {
      return taskControl;
   }
   public ProcessControl getProcessControl()
   {
      return processControl;
   }  
   public List<Action> getActions()
   {
      return actions;
   }
   /**
    * Call page actions, in order they appear in XML, and
    * handle conversation begin/end 
    */
   public boolean enter(FacesContext facesContext)
   {
      if ( isRestricted() )
      {
         // If no expression is configured, create a default one
         if (restriction == null)
         {
            Identity.instance().checkPermission( Pages.getViewId(facesContext), "render" );
         }
         else
         {
            Identity.instance().checkRestriction(restriction);
         }
      }      
      
      boolean result = false;
      
      getConversationControl().beginOrEndConversation();
      getTaskControl().beginOrEndTask();
      getProcessControl().createOrResumeProcess();
      
      for ( Input in: getInputs() ) in.in();
   
      for ( Action action: getActions() )
      {
         if ( action.isExecutable() )
         {
            String outcome = action.getOutcome();
            String fromAction = outcome;
            
            if (outcome==null)
            {
               fromAction = action.getMethodBinding().getExpressionString();
               result = true;
               outcome = Pages.toString( action.getMethodBinding().invoke() );
               Pages.handleOutcome(facesContext, outcome, fromAction);
            }
            else
            {
               Pages.handleOutcome(facesContext, outcome, fromAction);
            }
         }
      }
      
      return result;
   
   }
   public List<Input> getInputs()
   {
      return inputs;
   }
   
   public boolean isRestricted()
   {
      return restricted;
   }
   
   public void setRestricted(boolean restricted)
   {
      this.restricted = restricted;
   }
   public String getRestriction()
   {
      return restriction;
   }
   
   public void setRestriction(String restriction)
   {
      this.restriction = restriction;
   }

   public boolean isLoginRequired()
   {
      return loginRequired;
   }

   public void setLoginRequired(boolean loginRequired)
   {
      this.loginRequired = loginRequired;
   }
   
   public String getScheme()
   {
      return scheme;
   }
   
   public void setScheme(String scheme)
   {
      this.scheme = scheme;
   }
   
   public ConversationIdParameter getConversationIdParameter()
   {
      return conversationIdParameter;
   }
   
   public void setConversationIdParameter(ConversationIdParameter param)
   {
      this.conversationIdParameter = param;
   }
   
   
}