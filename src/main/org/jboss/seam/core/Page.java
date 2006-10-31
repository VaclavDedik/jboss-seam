package org.jboss.seam.core;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

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
      
      final String name;
      ValueBinding valueBinding;
      ValueBinding converterValueBinding;
      String converterId;
      
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
   
   Page(String viewId)
   {
      this.viewId = viewId;
      if (viewId!=null)
      {
         int loc = viewId.lastIndexOf('.');
         if ( loc>0 && viewId.startsWith("/") )
         {
            this.setResourceBundleName(viewId.substring(1, loc));
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
      return noConversationViewId==null ?
            Pages.instance().getNoConversationViewId() :
            noConversationViewId;
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
}