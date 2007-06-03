/**
 * 
 */
package org.jboss.seam.pages;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.jboss.seam.core.Events;
import org.jboss.seam.core.Expressions.ValueExpression;

public final class Rule
{
   private String outcomeValue;
   private ValueExpression condition;
   private List<Output> outputs = new ArrayList<Output>();
   private ConversationControl conversationControl = new ConversationControl();
   private TaskControl taskControl = new TaskControl();
   private ProcessControl processControl = new ProcessControl();
   private List<NavigationHandler> navigationHandlers = new ArrayList<NavigationHandler>();
   private String eventType;

   public boolean matches(String actualValue)
   {
      return ( actualValue!=null || condition!=null ) &&
            ( outcomeValue==null || outcomeValue.equals(actualValue) ) &&
            ( condition==null || Boolean.TRUE.equals( condition.getValue() ) );
   }
   
   public List<NavigationHandler> getNavigationHandlers()
   {
      return navigationHandlers;
   }

   public void addNavigationHandler(NavigationHandler navigationHandler)
   {
      this.navigationHandlers.add(navigationHandler);
   }

   public ConversationControl getConversationControl()
   {
      return conversationControl;
   }
   
   public TaskControl getTaskControl()
   {
      return taskControl;
   }
   
   public ProcessControl getProcessControl()
   {
      return processControl;
   }

   public ValueExpression getCondition()
   {
      return condition;
   }

   public void setCondition(ValueExpression expression)
   {
      this.condition = expression;
   }

   public String getOutcomeValue()
   {
      return outcomeValue;
   }

   public void setOutcomeValue(String value)
   {
      this.outcomeValue = value;
   }

   public List<Output> getOutputs()
   {
      return outputs;
   }

   public boolean execute(FacesContext context)
   {
      getConversationControl().beginOrEndConversation();
      getTaskControl().beginOrEndTask();
      getProcessControl().createOrResumeProcess();
      for ( Output output: getOutputs() ) 
      {
         output.out();
      }
      if (eventType!=null)
      {
         Events.instance().raiseEvent(eventType);
      }
      for ( NavigationHandler nh: getNavigationHandlers() )
      {
         if ( nh.navigate(context) ) return true;
      }
      return false;
   }

   public String getEventType()
   {
      return eventType;
   }

   public void setEventType(String event)
   {
      this.eventType = event;
   }
}