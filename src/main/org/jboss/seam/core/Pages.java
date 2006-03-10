package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jboss.logging.Logger;
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
   
   private static final Logger log = Logger.getLogger(Pages.class);
   
   private Map<String, String> descriptionByViewId = new HashMap<String, String>();
   private Map<String, Integer> timeoutsByViewId = new HashMap<String, Integer>();
   
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
            descriptionByViewId.put( viewId, page.getTextTrim() );
            String timeoutString = page.attributeValue("timeout");
            if (timeoutString!=null)
            {
               timeoutsByViewId.put( viewId, Integer.parseInt(timeoutString) );
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
   
   public static Pages instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application context");
      }
      return (Pages) Component.getInstance(Pages.class, ScopeType.APPLICATION, true);
   }
}
