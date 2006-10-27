package org.jboss.seam.core;

import java.io.Serializable;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

/**
 * Book-keeping component that persists information
 * about the conversation associated with the current
 * page.
 * 
 * @author Gavin King
 *
 */
@Name("org.jboss.seam.core.facesPage")
@Intercept(InterceptionType.NEVER)
@Scope(ScopeType.PAGE)
public class FacesPage implements Serializable
{
   private String pageflowName;
   private Integer pageflowCounter;
   private String pageflowNodeName;
   
   private String conversationId;
   private boolean conversationIsLongRunning;
   
   //private Map<String, Object> pageParameters;
   
   public String getConversationId()
   {
      return conversationId;
   }
   
   public void discardTemporaryConversation()
   {
      conversationId = null;
      conversationIsLongRunning = false;
   }
   
   public void discardNestedConversation(String outerConversationId)
   {
      conversationId = outerConversationId;
      conversationIsLongRunning = true;
   }
   
   public void storeConversation()
   {
      Manager manager = Manager.instance();
      if ( manager.isReallyLongRunningConversation() )
      {
         conversationId = manager.getCurrentConversationId();
         conversationIsLongRunning = true;
      }
      else
      {
         conversationId = null;
         conversationIsLongRunning = false;
      }
      
      if ( Init.instance().isJbpmInstalled() )
      {
         Pageflow pageflow = Pageflow.instance();
         if ( pageflow.isInProcess() )
         {
            pageflowName = pageflow.getProcessInstance().getProcessDefinition().getName();
            pageflowNodeName = pageflow.getNode().getName();
            pageflowCounter = pageflow.getPageflowCounter();
         }
         else
         {
            pageflowName = null;
            pageflowNodeName = null;
            pageflowCounter = null;
         }
      }
   }
   
   public static FacesPage instance()
   {
      if ( !Contexts.isPageContextActive() )
      {
         throw new IllegalStateException("No page context active");
      }
      return (FacesPage) Component.getInstance(FacesPage.class, ScopeType.PAGE, true);
   }

   public boolean isConversationLongRunning()
   {
      return conversationIsLongRunning;
   }

   public Integer getPageflowCounter()
   {
      return pageflowCounter;
   }

   public String getPageflowName()
   {
      return pageflowName;
   }

   public String getPageflowNodeName()
   {
      return pageflowNodeName;
   }

   /*public Map<String, Object> getPageParameters()
   {
      return pageParameters==null ? Collections.EMPTY_MAP : pageParameters;
   }

   public void setPageParameters(Map<String, Object> pageParameters)
   {
      this.pageParameters = pageParameters.isEmpty() ? null : pageParameters;
   }
   
   /**
    * Used by test harness
    * 
    * @param name the page parameter name
    * @param value the value
    */
   /*public void setPageParameter(String name, Object value)
   {
      if (pageParameters==null)
      {
         pageParameters = new HashMap<String, Object>();
      }
      pageParameters.put(name, value);
   }*/

}
