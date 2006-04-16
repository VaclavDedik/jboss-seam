package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Name("events")
public class Events 
{
   
   private static final Log log = LogFactory.getLog(Events.class);
   
   private final Map<String, List<MethodBinding>> listeners = new HashMap<String, List<MethodBinding>>();
   
   @Create
   public void initialize() throws DocumentException
   {
      InputStream stream = Resources.getResourceAsStream("/WEB-INF/events.xml");      
      if (stream==null)
      {
         log.info("no events.xml file found");
      }
      else
      {
         log.info("reading events.xml");
         SAXReader saxReader = new SAXReader();
         saxReader.setMergeAdjacentText(true);
         Document doc = saxReader.read(stream);
         List<Element> elements = doc.getRootElement().elements("event");
         for (Element event: elements)
         {
            String type = event.attributeValue("type");
            List<MethodBinding> methodBindings = new ArrayList<MethodBinding>();
            listeners.put(type, methodBindings);
            
            List<Element> actions = event.elements("action");
            for (Element action: actions)
            {
               String actionExpression = action.attributeValue("expression");
               MethodBinding methodBinding = FacesContext.getCurrentInstance().getApplication()
                     .createMethodBinding(actionExpression, null);
               methodBindings.add(methodBinding);
            }
         }
      }
   }
   
   public void addListener(String type, String methodBindingExpression)
   {
      MethodBinding methodBinding = FacesContext.getCurrentInstance().getApplication()
            .createMethodBinding(methodBindingExpression, null);
      List<MethodBinding> list = listeners.get(type);
      if (list==null)
      {
         list = new ArrayList<MethodBinding>();
         listeners.put(type, list);
      }
      list.add(methodBinding);
   }
   
   public void raiseEvent(String type)
   {
      log.debug("Processing event:" + type);
      List<MethodBinding> list = listeners.get(type);
      if (list!=null)
      {
         for (MethodBinding listener: list )
         {
            listener.invoke( FacesContext.getCurrentInstance(), null );
         }
      }
   }
   
   public static Events instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application context");
      }
      return (Events) Component.getInstance(Events.class, ScopeType.APPLICATION, true);
   }
   
}
