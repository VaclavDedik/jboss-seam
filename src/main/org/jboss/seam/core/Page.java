package org.jboss.seam.core;

import java.util.ArrayList;
import java.util.List;
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

   private final String viewId;
   private String description;
   private Integer timeout;
   private MethodBinding action;
   private String outcome;
   private String noConversationViewId;
   private String resourceBundleName;
   private boolean switchEnabled = true;
   private List<Page.PageParameter> pageParameters = new ArrayList<Page.PageParameter>();
   private boolean isBeginConversation;
   private boolean isEndConversation;
   private boolean join;
   private boolean nested;
   private FlushModeType flushMode;
   private String pageflow;
   
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
      try
      {
         return java.util.ResourceBundle.getBundle(
               getResourceBundleName(), 
               Locale.instance(), 
               Thread.currentThread().getContextClassLoader()
            );
      }
      catch (MissingResourceException mre)
      {
         return null;
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

   public void setAction(MethodBinding action)
   {
      this.action = action;
   }

   public MethodBinding getAction()
   {
      return action;
   }

   public void setOutcome(String outcome)
   {
      this.outcome = outcome;
   }

   public String getOutcome()
   {
      return outcome;
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

   public List<Page.PageParameter> getPageParameters()
   {
      return pageParameters;
   }

   public boolean hasDescription()
   {
      return description!=null;
   }

   public boolean isBeginConversation()
   {
      return isBeginConversation;
   }

   public void setBeginConversation(boolean isBeginConversation)
   {
      this.isBeginConversation = isBeginConversation;
   }

   public boolean isEndConversation()
   {
      return isEndConversation;
   }

   public void setEndConversation(boolean isEndConversation)
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

   protected FlushModeType getFlushMode()
   {
      return flushMode;
   }

   protected void setFlushMode(FlushModeType flushMode)
   {
      this.flushMode = flushMode;
   }

   protected boolean isJoin()
   {
      return join;
   }

   protected void setJoin(boolean join)
   {
      this.join = join;
   }

   protected boolean isNested()
   {
      return nested;
   }

   protected void setNested(boolean nested)
   {
      this.nested = nested;
   }

   protected String getPageflow()
   {
      return pageflow;
   }

   protected void setPageflow(String pageflow)
   {
      this.pageflow = pageflow;
   }

}