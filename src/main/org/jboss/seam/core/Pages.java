package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Expressions.MethodBinding;
import org.jboss.seam.core.Expressions.ValueBinding;
import org.jboss.seam.util.Parameters;
import org.jboss.seam.util.Resources;

/**
 * Holds metadata for pages defined in pages.xml, including
 * page actions and page descriptions.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
//@Startup(depends="org.jboss.seam.core.microcontainer") //don't make it a startup component 'cos it needs a faces context
@Name("org.jboss.seam.core.pages")
public class Pages 
{
   public static final String PAGE_PARAMETERS = "org.jboss.seam.core.pageParameters";
   
   private static final Log log = LogFactory.getLog(Pages.class);
   
   private Map<String, Page> pagesByViewId = new HashMap<String, Page>();   
   private String noConversationViewId;
   
   static final class Page
   {
      Page(String viewId)
      {
         this.viewId = viewId;
      }
      
      final String viewId;
      String description;
      Integer timeout;
      MethodBinding action;
      String outcome;
      String noConversationViewId;
      String resourceBundleName;
      List<PageParameter> pageParameters = new ArrayList<PageParameter>();
      
      java.util.ResourceBundle getResourceBundle()
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
      
      @Override
      public String toString()
      {
         return "Page(" + viewId + ")";
      }
   }
   
   static final class PageParameter
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
   
   private SortedSet<String> wildcardViewIds = new TreeSet<String>( 
         new Comparator<String>() {
            public int compare(String x, String y)
            {
               if ( x.length()<y.length() ) return -1;
               if ( x.length()> y.length() ) return 1;
               return x.compareTo(y);
            }
         } 
      );
   
   @Create
   public void initialize() throws DocumentException
   {
      InputStream stream = Resources.getResourceAsStream("/WEB-INF/pages.xml");      
      if (stream==null)
      {
         log.info("no pages.xml file found");
      }
      else
      {
         log.info("reading pages.xml");
         SAXReader saxReader = new SAXReader();
         saxReader.setMergeAdjacentText(true);
         Document doc = saxReader.read(stream);
         
         if (noConversationViewId==null) //let the setting in components.xml override the pages.xml
         {
            noConversationViewId = doc.getRootElement().attributeValue("no-conversation-view-id");
         }
         
         List<Element> elements = doc.getRootElement().elements("page");
         for (Element page: elements)
         {
            String viewId = page.attributeValue("view-id");
            if ( viewId.endsWith("*") )
            {
               wildcardViewIds.add(viewId);
            }
            Page entry = new Page(viewId);
            pagesByViewId.put(viewId, entry);
            
            String description = page.getTextTrim();
            if (description!=null && description.length()>0)
            {
               entry.description = description;
            }
            
            String timeoutString = page.attributeValue("timeout");
            if (timeoutString!=null)
            {
               entry.timeout = Integer.parseInt(timeoutString);
            }
            
            String noConversationViewId = page.attributeValue("no-conversation-view-id");
            entry.noConversationViewId = noConversationViewId;
            
            String action = page.attributeValue("action");
            if (action!=null)
            {
               if ( action.startsWith("#{") )
               {
                  MethodBinding methodBinding = Expressions.instance().createMethodBinding(action);
                  entry.action = methodBinding;
               }
               else
               {
                  entry.outcome = action;
               }
            }
            
            String bundle = page.attributeValue("bundle");
            entry.resourceBundleName = bundle==null ? viewId.substring(1, viewId.indexOf('.')) : bundle;
            
            List<Element> children = page.elements("param");
            for (Element param: children)
            {
               String valueExpression = param.attributeValue("value");
               if (valueExpression==null)
               {
                  throw new IllegalArgumentException("must specify value for page <param/> declaration");
               }
               String name = param.attributeValue("name");
               if (name==null)
               {
                  name = valueExpression.substring(2, valueExpression.length()-1);
               }
               PageParameter pageParameter = new PageParameter( name );
               pageParameter.valueBinding = Expressions.instance().createValueBinding( valueExpression );
               pageParameter.converterId = param.attributeValue("converterId");
               String converterExpression = param.attributeValue("converter");
               if (converterExpression!=null)
               {
                  pageParameter.converterValueBinding = Expressions.instance().createValueBinding( converterExpression );
               }
               entry.pageParameters.add(pageParameter);
            }
         }
      }
   }
   
   private Page getPage(String viewId)
   {
      Page result = null;
      if (viewId!=null)
      {
         result = pagesByViewId.get(viewId);
         if (result==null)
         {
            //workaround for what I believe is a bug in the JSF RI
            result = pagesByViewId.get( replaceExtension(viewId) );
         }
      }
      return result==null ? new Page(viewId) : result;
   }
   
   private static String replaceExtension(String viewId)
   {
      int loc = viewId.lastIndexOf('.');
      return loc<0 ? null : viewId.substring(0, loc) + getSuffix();
   }
   
   public boolean hasDescription(String viewId)
   {
      return getPage(viewId).description!=null;
   }
   
   public String getDescription(String viewId)
   {
      return Interpolator.instance().interpolate( getPage(viewId).description );
   }

   public Integer getTimeout(String viewId)
   {
      return getPage(viewId).timeout;
   }
   
   public boolean callAction()
   {
      boolean result = false;
      FacesContext facesContext = FacesContext.getCurrentInstance();
      String viewId = facesContext.getViewRoot().getViewId();
      if (viewId!=null)
      {
         for (String wildcard: wildcardViewIds)
         {
            if ( viewId.startsWith( wildcard.substring(0, wildcard.length()-1) ) )
            {
               result = callAction(facesContext, wildcard) || result;
            }
         }
      }
      result = callAction(facesContext, viewId) || result;
      return result;
   }

   private boolean callAction(FacesContext facesContext, String viewId)
   {
      boolean result = false;
      
      String outcome = getPage(viewId).outcome;
      String fromAction = outcome;
      
      if (outcome==null)
      {
         MethodBinding methodBinding = getPage(viewId).action;
         if (methodBinding!=null) 
         {
            fromAction = methodBinding.getExpressionString();
            result = true;
            outcome = toString( methodBinding.invoke() );
         }
      }
      
      handleOutcome(facesContext, outcome, fromAction);
      
      return result;

   }

   private static String toString(Object returnValue)
   {
      return returnValue == null ? null : returnValue.toString();
   }

   private static void handleOutcome(FacesContext facesContext, String outcome, String fromAction)
   {
      if (outcome!=null)
      {
         facesContext.getApplication().getNavigationHandler()
               .handleNavigation(facesContext, fromAction, outcome);
      }
   }
   
   public java.util.ResourceBundle getResourceBundle(String viewId)
   {
      return getPage(viewId).getResourceBundle();
   }
   
   public static Pages instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application context");
      }
      return (Pages) Component.getInstance(Pages.class, ScopeType.APPLICATION, true);
   }

   public static boolean callAction(FacesContext facesContext)
   {
      //TODO: refactor with Pages.instance().callAction()!!
      
      boolean result = false;
      
      String outcome = (String) facesContext.getExternalContext()
            .getRequestParameterMap()
            .get("actionOutcome");
      String fromAction = outcome;
      
      if (outcome==null)
      {
         String actionId = (String) facesContext.getExternalContext()
               .getRequestParameterMap()
               .get("actionMethod");
         if (actionId!=null)
         {
            if ( !SafeActions.instance().isActionSafe(actionId) ) return result;
            String expression = SafeActions.toAction(actionId);
            result = true;
            MethodBinding actionBinding = Expressions.instance().createMethodBinding(expression);
            outcome = toString( actionBinding.invoke() );
            fromAction = expression;
         }
      }
      
      handleOutcome(facesContext, outcome, fromAction);
      
      return result;
   }
   
   public String getNoConversationViewId(String viewId)
   {
      String result = getPage(viewId).noConversationViewId;
      return result==null ? noConversationViewId : result;
   }
   
   public Map<String, Object> getParameters(String viewId)
   {
      return getParameters(viewId, Collections.EMPTY_SET);
   }
   
   public Map<String, Object> getParameters(String viewId, Set<String> overridden)
   {
      Map<String, Object> parameters = new HashMap<String, Object>();
      for ( PageParameter pageParameter: getPage(viewId).pageParameters )
      {
         if ( !overridden.contains(pageParameter.name) )
         {
            Object value = pageParameter.valueBinding.getValue();
            if (value!=null)
            {
               parameters.put(pageParameter.name, value);
            }
         }
      }
      return parameters;
   }
   
   public void applyRequestParameterValues(FacesContext facesContext)
   {
      String viewId = facesContext.getViewRoot().getViewId();
      Map<String, String[]> requestParameters = Parameters.getRequestParameters();
      for ( PageParameter pageParameter: getPage(viewId).pageParameters )
      {         
         String[] parameterValues = requestParameters.get(pageParameter.name);
         if (parameterValues==null || parameterValues.length==0)
         {
            continue;
         }
         if (parameterValues.length>1)
         {
            throw new IllegalArgumentException("page parameter may not be multi-valued: " + pageParameter.name);
         }         
         String stringValue = parameterValues[0];

         Converter converter;
         try
         {
            converter = pageParameter.getConverter();
         }
         catch (RuntimeException re)
         {
            //YUCK! due to bad JSF/MyFaces error handling
            continue;
         }
         
         Object value = converter==null ? 
               stringValue :
               converter.getAsObject( facesContext, facesContext.getViewRoot(), stringValue );
         pageParameter.valueBinding.setValue(value);
      }
   }

   public void applyViewRootValues(FacesContext facesContext)
   {
      String viewId = facesContext.getViewRoot().getViewId();
      
      for (PageParameter pageParameter: getPage(viewId).pageParameters)
      {         
         Object object = Contexts.getPageContext().get(pageParameter.name);
         if (object!=null)
         {
            pageParameter.valueBinding.setValue(object);
         }
      }
   }

   public String getNoConversationViewId()
   {
      return noConversationViewId;
   }

   public void setNoConversationViewId(String noConversationViewId)
   {
      this.noConversationViewId = noConversationViewId;
   }
   
   /**
    * Encode page parameters into a URL
    * 
    * @param url the base URL
    * @param viewId the JSF view id of the page
    * @return the URL with parameters appended
    */
   public String encodePageParameters(String url, String viewId)
   {
      Map<String, Object> parameters = getParameters(viewId);
      return Manager.instance().encodeParameters(url, parameters);
   }

   /**
    * Store the page parameters to the JSF view root
    */
   public void storePageParameters(FacesContext facesContext)
   {
      String viewId = facesContext.getViewRoot().getViewId();
      if (viewId!=null)
      {
        for ( Map.Entry<String, Object> param: getParameters(viewId).entrySet() )
        {
           Contexts.getPageContext().set( param.getKey(), param.getValue() );
        }
      }
   }

   public static String getSuffix()
   {
      String defaultSuffix = FacesContext.getCurrentInstance().getExternalContext()
            .getInitParameter(ViewHandler.DEFAULT_SUFFIX_PARAM_NAME);
      return defaultSuffix == null ? ViewHandler.DEFAULT_SUFFIX : defaultSuffix;
   
   }

}
