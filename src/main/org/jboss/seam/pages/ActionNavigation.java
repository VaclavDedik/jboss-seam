/**
 * 
 */
package org.jboss.seam.pages;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.jboss.seam.core.Expressions.ValueBinding;

public final class ActionNavigation
{
   private ValueBinding<Object> outcomeValueBinding;
   private List<Outcome> outcomes = new ArrayList<Outcome>();
   private Outcome outcome;
   
   public List<Outcome> getOutcomes()
   {
      return outcomes;
   }
   
   public void setOutcomeValueBinding(ValueBinding<Object> outcomeValueBinding)
   {
      this.outcomeValueBinding = outcomeValueBinding;
   }
   
   public ValueBinding<Object> getOutcomeValueBinding()
   {
      return outcomeValueBinding;
   }

   public Outcome getOutcome()
   {
      return outcome;
   }

   public void setOutcome(Outcome outcome)
   {
      this.outcome = outcome;
   }

   public boolean navigate(FacesContext context, final String actionOutcomeValue)
   {
      String outcomeValue;
      if ( getOutcomeValueBinding()==null )
      {
         outcomeValue = actionOutcomeValue;
      }
      else
      {
         Object value = getOutcomeValueBinding().getValue();
         outcomeValue = value==null ? null : value.toString();
      }
      
      for ( Outcome outcome: getOutcomes() )
      {
         if ( outcome.matches(outcomeValue) )
         {
            return outcome.execute(context);
         }
      }
      
      return getOutcome().execute(context);
   }

}