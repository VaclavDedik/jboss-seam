package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jboss.logging.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.Resources;

@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup(depends="org.jboss.seam.core.microcontainer")
@Name("org.jboss.seam.core.pages")
public class Pages {
   
   private static final Logger log = Logger.getLogger(Pages.class);
   
   private Map<String, String> descriptionByViewId = new HashMap<String, String>();
   
   @Create
   public void initialize() throws DocumentException
   {
      InputStream stream = Resources.getResourceAsStream("WEB-INF/pages.xml");
      if (stream!=null)
      {
         SAXReader saxReader = new SAXReader();
         saxReader.setMergeAdjacentText(true);
         Document doc = saxReader.read(stream);
         List<Element> elements = doc.getRootElement().elements("page");
         for (Element page: elements)
         {
            descriptionByViewId.put( page.attributeValue("view-id"), page.getTextTrim() );
         }
      }
   }
   
   public boolean hasDescription(String viewId)
   {
      return descriptionByViewId.containsKey(viewId);
   }
   
   public String getDescription(String viewId)
   {
      FacesContext context = FacesContext.getCurrentInstance();
      String description = descriptionByViewId.get(viewId);
      StringTokenizer tokens = new StringTokenizer(description, "#{}", true);
      StringBuilder builder = new StringBuilder(description.length());
      while ( tokens.hasMoreTokens() )
      {
         String tok = tokens.nextToken();
         if ( "#".equals(tok) )
         {
            tokens.nextToken();
            String expression = "#{" + tokens.nextToken() + "}";
            tokens.nextToken();
            try
            {
               Object value = context.getApplication().createValueBinding(expression).getValue(context);
               if (value!=null) builder.append(value);
            }
            catch (Exception e)
            {
               log.warn("exception evaluating description: " + description, e);
            }
         }
         else
         {
            builder.append(tok);
         }
      }
      return builder.toString();
   }
   
   public static Pages instance()
   {
      return (Pages) Contexts.getApplicationContext().get(Pages.class);
   }
}
