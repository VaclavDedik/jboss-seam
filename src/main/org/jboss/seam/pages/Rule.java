/**
 * 
 */
package org.jboss.seam.pages;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.jboss.seam.core.Expressions.ValueBinding;

public final class Rule
{
   private String outcomeValue;
   private ValueBinding condition;
   private List<Output> outputs = new ArrayList<Output>();
   private ConversationControl conversationControl = new ConversationControl();
   private TaskControl taskControl = new TaskControl();
   private ProcessControl processControl = new ProcessControl();
   private NavigationHandler navigationHandler = new NavigationHandler() { 
      @Override
      public boolean navigate(FacesContext context) 
      {
         return false;
      }
   };

   public boolean matches(String actualValue)
   {
      return ( actualValue!=null || condition!=null ) &&
            ( outcomeValue==null || outcomeValue.equals(actualValue) ) &&
            ( condition==null || Boolean.TRUE.equals( condition.getValue() ) );
   }
   
   public NavigationHandler getNavigationHandler()
   {
      return navigationHandler;
   }

   public void setNavigationHandler(NavigationHandler result)
   {
      this.navigationHandler = result;
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

   public ValueBinding getCondition()
   {
      return condition;
   }

   public void setCondition(ValueBinding expression)
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
      for ( Output output: getOutputs() ) output.out();
      return getNavigationHandler().navigate(context);
   }
}