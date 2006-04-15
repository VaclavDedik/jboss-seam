package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;

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
import org.jboss.seam.util.Resources;
import org.jboss.seam.util.Template;

@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
//@Startup(depends="org.jboss.seam.core.microcontainer") //don't make it a startup component 'cos it needs a faces context
@Name("org.jboss.seam.core.pages")
public class Pages 
{
   
   private static final Log log = LogFactory.getLog(Pages.class);
   
   //TODO: move into a single map:
   private Map<String, String> descriptionByViewId = new HashMap<String, String>();
   private Map<String, Integer> timeoutsByViewId = new HashMap<String, Integer>();
   private Map<String, MethodBinding> actionsByViewId = new HashMap<String, MethodBinding>();
   private Map<String, String> outcomesByViewId = new HashMap<String, String>();
   
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
            String description = page.getTextTrim();
            if (description!=null && description.length()>0)
            {
               descriptionByViewId.put( viewId, description );
            }
            String timeoutString = page.attributeValue("timeout");
            if (timeoutString!=null)
            {
               timeoutsByViewId.put( viewId, Integer.parseInt(timeoutString) );
            }
            String action = page.attributeValue("action");
            if (action!=null)
            {
               if ( action.startsWith("#{") )
               {
                  MethodBinding methodBinding = FacesContext.getCurrentInstance().getApplication().createMethodBinding(action, null);
                  actionsByViewId.put(viewId, methodBinding);
               }
               else
               {
                  outcomesByViewId.put(viewId, action);
               }
            }
         }
      }
   }
   
   public boolean hasDescription(String viewId)
   {
      return descriptionByViewId.containsKey(viewId);
   }
   
   public String getDescription(String viewId)
   {
      return Template.render( descriptionByViewId.get(viewId) );
   }

   public Integer getTimeout(String viewId)
   {
      return timeoutsByViewId.get(viewId);
   }
   
   public boolean callAction()
   {
      boolean result = false;
      FacesContext facesContext = FacesContext.getCurrentInstance();
      String viewId = facesContext.getViewRoot().getViewId();
      for (String wildcard: wildcardViewIds)
      {
         if ( viewId.startsWith( wildcard.substring(0, wildcard.length()-1) ) )
         {
            result = callAction(facesContext, wildcard) || result;
         }
      }
      result = callAction(facesContext, viewId) || result;
      return result;
   }

   private boolean callAction(FacesContext facesContext, String viewId)
   {
      boolean result = false;
      
      String outcome = outcomesByViewId.get(viewId);
      String fromAction = outcome;
      
      if (outcome==null)
      {
         MethodBinding methodBinding = actionsByViewId.get(viewId);
         if (methodBinding!=null) 
         {
            fromAction = methodBinding.getExpressionString();
            result = true;
            outcome = (String) methodBinding.invoke(facesContext, null);
         }
      }
      
      if (outcome!=null)
      {
         facesContext.getApplication().getNavigationHandler()
               .handleNavigation(facesContext, fromAction, outcome);
      }
      
      return result;

   }
   
   public static Pages instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application context");
      }
      return (Pages) Component.getInstance(Pages.class, ScopeType.APPLICATION, true);
   }
}
