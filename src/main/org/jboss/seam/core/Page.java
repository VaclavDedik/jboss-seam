package org.jboss.seam.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.core.Expressions.MethodBinding;
import org.jboss.seam.core.Expressions.ValueBinding;

/**
 * Metadata about page actions, page parameters, resource bundle
 * etc, for a particular JSF view id.
 */
public final class Page
{
   public static final class PageParameter
   {
      PageParameter(String name)
      {
         this.name = name;
      }
      
      private final String name;
      private ValueBinding valueBinding;
      private ValueBinding converterValueBinding;
      private String converterId;
      
      Converter getConverter()
      {
         if (converterId!=null)
         {
            return FacesContext.getCurrentInstance().getApplication().createConverter(converterId);
         }
         else if (converterValueBinding!=null)
         {
            return (Converter) converterValueBinding.getValue();
         }
         else if (valueBinding==null)
         {
            return null;
         }
         else
         {
            Class<?> type = valueBinding.getType();
            return FacesContext.getCurrentInstance().getApplication().createConverter(type);           
         }
      }
   
      public String getName()
      {
         return name;
      }

      public void setValueBinding(ValueBinding valueBinding)
      {
         this.valueBinding = valueBinding;
      }

      public ValueBinding getValueBinding()
      {
         return valueBinding;
      }

      public void setConverterValueBinding(ValueBinding converterValueBinding)
      {
         this.converterValueBinding = converterValueBinding;
      }

      public ValueBinding getConverterValueBinding()
      {
         return converterValueBinding;
      }

      public void setConverterId(String converterId)
      {
         this.converterId = converterId;
      }

      public String getConverterId()
      {
         return converterId;
      }

      @Override
      public String toString()
      {
         return "PageParameter(" + name + ")";
      }

   }
   
   public static final class Navigation
   {
      private ValueBinding<Object> outcomeValueBinding;
      private Map<String, Case> cases = new HashMap<String, Case>();
      private Case defaultCase;
      
      public Map<String, Case> getCases()
      {
         return cases;
      }
      
      void setDefaultCase(Case defaultCase)
      {
         this.defaultCase = defaultCase;
      }
      public Case getDefaultCase()
      {
         return defaultCase;
      }
      
      void setOutcomeValueBinding(ValueBinding<Object> outcomeValueBinding)
      {
         this.outcomeValueBinding = outcomeValueBinding;
      }
      public ValueBinding<Object> getOutcomeValueBinding()
      {
         return outcomeValueBinding;
      }
   }
   
   public static final class Case
   {
      private Result result;
      private boolean isBeginConversation;
      private boolean isEndConversation;
      private boolean join;
      private boolean nested;
      private FlushModeType flushMode;
      private String pageflow;
      
      void setResult(Result result)
      {
         this.result = result;
      }
      Result getResult()
      {
         return result;
      }
      void setBeginConversation(boolean isBeginConversation)
      {
         this.isBeginConversation = isBeginConversation;
      }
      boolean isBeginConversation()
      {
         return isBeginConversation;
      }
      void setEndConversation(boolean isEndConversation)
      {
         this.isEndConversation = isEndConversation;
      }
      boolean isEndConversation()
      {
         return isEndConversation;
      }
      void setJoin(boolean join)
      {
         this.join = join;
      }
      boolean isJoin()
      {
         return join;
      }
      void setNested(boolean nested)
      {
         this.nested = nested;
      }
      boolean isNested()
      {
         return nested;
      }
      void setFlushMode(FlushModeType flushMode)
      {
         this.flushMode = flushMode;
      }
      FlushModeType getFlushMode()
      {
         return flushMode;
      }
      void setPageflow(String pageflow)
      {
         this.pageflow = pageflow;
      }
      String getPageflow()
      {
         return pageflow;
      }
   }
   
   public interface Result
   {
      public void navigate(FacesContext context);
   }

   private final String viewId;
   private String description;
   private Integer timeout;
   private MethodBinding action;
   private String outcome;
   private String noConversationViewId;
   private String resourceBundleName;
   private boolean switchEnabled = true;
   private List<PageParameter> pageParameters = new ArrayList<PageParameter>();
   private Map<String, Navigation> navigations = new HashMap<String, Navigation>();
   private Navigation defaultNavigation;
   private boolean isBeginConversation;
   private boolean isEndConversation;
   private boolean join;
   private boolean nested;
   private FlushModeType flushMode;
   private String pageflow;
   private boolean conversationRequired;
   
   Page(String viewId)
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
   
   java.util.ResourceBundle getResourceBundle()
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
   
   void setDescription(String description)
   {
      this.description = description;
   }

   public String getDescription()
   {
      return description;
   }

   void setTimeout(Integer timeout)
   {
      this.timeout = timeout;
   }

   public Integer getTimeout()
   {
      return timeout;
   }

   void setAction(MethodBinding action)
   {
      this.action = action;
   }

   public MethodBinding getAction()
   {
      return action;
   }

   void setOutcome(String outcome)
   {
      this.outcome = outcome;
   }

   public String getOutcome()
   {
      return outcome;
   }

   void setNoConversationViewId(String noConversationViewId)
   {
      this.noConversationViewId = noConversationViewId;
   }

   public String getNoConversationViewId()
   {
      return noConversationViewId;
   }

   void setResourceBundleName(String resourceBundleName)
   {
      this.resourceBundleName = resourceBundleName;
   }

   public String getResourceBundleName()
   {
      return resourceBundleName;
   }

   void setSwitchEnabled(boolean switchEnabled)
   {
      this.switchEnabled = switchEnabled;
   }

   public boolean isSwitchEnabled()
   {
      return switchEnabled;
   }

   public List<Page.PageParameter> getPageParameters()
   {
      return pageParameters;
   }

   public Map<String, Page.Navigation> getNavigations()
   {
      return navigations;
   }

   public boolean hasDescription()
   {
      return description!=null;
   }

   public boolean isBeginConversation()
   {
      return isBeginConversation;
   }

   void setBeginConversation(boolean isBeginConversation)
   {
      this.isBeginConversation = isBeginConversation;
   }

   public boolean isEndConversation()
   {
      return isEndConversation;
   }

   void setEndConversation(boolean isEndConversation)
   {
      this.isEndConversation = isEndConversation;
   }
   
   public void beginOrEndConversation()
   {
      if ( isEndConversation )
      {
         Conversation.instance().end();
      }
      if ( isBeginConversation )
      {
         Conversation.instance().begin(join, nested);
         if (flushMode!=null)
         {
            Conversation.instance().changeFlushMode(flushMode);
         }
         if (pageflow!=null)
         {
            Pageflow.instance().begin(pageflow);
         }
      }
   }

   public FlushModeType getFlushMode()
   {
      return flushMode;
   }

   void setFlushMode(FlushModeType flushMode)
   {
      this.flushMode = flushMode;
   }

   public boolean isJoin()
   {
      return join;
   }

   void setJoin(boolean join)
   {
      this.join = join;
   }

   public boolean isNested()
   {
      return nested;
   }

   void setNested(boolean nested)
   {
      this.nested = nested;
   }

   public String getPageflow()
   {
      return pageflow;
   }

   void setPageflow(String pageflow)
   {
      this.pageflow = pageflow;
   }

   public boolean isConversationRequired()
   {
      return conversationRequired;
   }

   void setConversationRequired(boolean conversationRequired)
   {
      this.conversationRequired = conversationRequired;
   }

   public Navigation getDefaultNavigation()
   {
      return defaultNavigation;
   }

   void setDefaultNavigation(Navigation defaultActionOutcomeMapping)
   {
      this.defaultNavigation = defaultActionOutcomeMapping;
   }

}