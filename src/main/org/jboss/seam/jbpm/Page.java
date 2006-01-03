package org.jboss.seam.jbpm;

import org.dom4j.Element;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Process;
import org.jboss.seam.core.Transition;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.jpdl.xml.JpdlXmlReader;
import org.jbpm.jpdl.xml.Parsable;
import org.jbpm.taskmgmt.exe.TaskInstance;

public class Page extends Node implements Parsable 
{
   
   // This classname is configured in the jbpm configuration 
   // file : org/jbpm/graph/node/node.types.xml inside 
   // the jbpm-{version}.jar
   
   // In case it would be necessary, that file, can be customized
   // by updating the reference to it in the central jbpm configuration 
   // file 'jbpm.cfg.xml'

   private static final long serialVersionUID = 1L;
   
   private String viewId;
   private boolean isConversationEnd = false;
   private String transition;

   /**
    * parses the dom4j element that corresponds to this page.
    */
   public void read(Element pageElement, JpdlXmlReader jpdlXmlReader) 
   {
      viewId = pageElement.attributeValue("view-id");
      Element conversationEndElement = pageElement.element("end-conversation");
      if (conversationEndElement!=null) 
      {
         isConversationEnd = true;
         transition = conversationEndElement.attributeValue("transition");
      }
   }

   /**
    * is executed when execution arrives in this page at runtime.
    */
   public void execute(ExecutionContext executionContext) 
   {
      if (isConversationEnd) {

         TaskInstance task = org.jboss.seam.core.TaskInstance.instance();
         if ( task != null )
         {
            
            if ( transition==null || "".equals(transition) )
            {
               transition = Transition.instance().getName();
            }
            
            if ( transition == null )
            {
               task.end();
            }
            else
            {
               task.end(transition);
            }
         
            Process.instance().setTaskId(null);
            
         }
         
         Manager.instance().endConversation();
      }
   }

   public boolean isConversationEnd() 
   {
      return isConversationEnd;
   }
   
   public String getTransition() 
   {    
      return transition;
   }
   
   public String getViewId() 
   {
      return viewId;
   }
}
