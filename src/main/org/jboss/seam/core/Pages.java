package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.faces.context.FacesContext;

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
   
   private static final Log log = LogFactory.getLog(Pages.class);
   
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
      Map<String, ValueBinding> parameterValueBindings = new HashMap<String, ValueBinding>();
   }
   
   private Map<String, Page> pagesByViewId = new HashMap<String, Page>();
   
   private String noConversationViewId;
   
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
            
            List<Element> children = page.elements("param");
            for (Element param: children)
            {
               ValueBinding valueBinding = Expressions.instance().createValueBinding( param.attributeValue("value") );
               entry.parameterValueBindings.put( param.attributeValue("name"), valueBinding );
            }
         }
      }
   }
   
   private Page getPage(String viewId)
   {
      Page result = pagesByViewId.get(viewId);
      return result==null ? new Page(viewId) : result;
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
            outcome = toString( methodBinding.invoke(null) );
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
         String action = (String) facesContext.getExternalContext()
               .getRequestParameterMap()
               .get("actionMethod");
         if (action!=null)
         {
            String expression = "#{" + action + "}";
            if ( !isActionAllowed(facesContext, expression) ) return result;
            result = true;
            MethodBinding actionBinding = Expressions.instance().createMethodBinding(expression);
            outcome = toString( actionBinding.invoke(null) );
            fromAction = expression;
         }
      }
      
      handleOutcome(facesContext, outcome, fromAction);
      
      return result;
   }

   private static boolean isActionAllowed(FacesContext facesContext, String expression)
   {
      Map applicationMap = facesContext.getExternalContext().getApplicationMap();
      Set actions = (Set) applicationMap.get("org.jboss.seam.actions");
      if (actions==null) return false;
      synchronized (actions)
      {
         return actions.contains(expression);
      }
   }
   
   public String getNoConversationViewId(String viewId)
   {
      String result = getPage(viewId).noConversationViewId;
      return result==null ? noConversationViewId : result;
   }
   
   private Collection<Map.Entry<String, ValueBinding>> getParameterValueBindings(String viewId)
   {
      return getPage(viewId).parameterValueBindings.entrySet();
   }
   
   public Map<String, Object> getParameters(String viewId)
   {
      return getParameters(viewId, Collections.EMPTY_SET);
   }
   
   public Map<String, Object> getParameters(String viewId, Set<String> overridden)
   {
      Map<String, Object> parameters = new HashMap<String, Object>();
      for (Map.Entry<String, ValueBinding> me: getParameterValueBindings(viewId))
      {
         if ( !overridden.contains( me.getKey() ) )
         {
            Object value = me.getValue().getValue();
            //TODO: handle multi-values!
            if (value!=null)
            {
               parameters.put( me.getKey(), value );
            }
         }
      }
      return parameters;
   }
   
   public void applyParameterValues(String viewId)
   {
      Map<String, String[]> parameters = Parameters.getRequestParameters();
      for (Map.Entry<String, ValueBinding> me: getParameterValueBindings(viewId))
      {
         Class type = me.getValue().getType();
         Object value = Parameters.convertMultiValueRequestParameter( parameters, me.getKey(), type );
         if (value!=null) 
         {
            me.getValue().setValue(value);
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

}
