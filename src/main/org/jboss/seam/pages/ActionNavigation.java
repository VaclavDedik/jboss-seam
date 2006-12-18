/**
 * 
 */
package org.jboss.seam.pages;

import java.util.ArrayList;
import java.util.List;

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

}