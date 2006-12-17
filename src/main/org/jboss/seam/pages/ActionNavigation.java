/**
 * 
 */
package org.jboss.seam.pages;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.core.Expressions.ValueBinding;

public final class ActionNavigation
{
   private ValueBinding<Object> outcomeValueBinding;
   private Map<String, Outcome> outcomes = new HashMap<String, Outcome>();
   private Outcome nullOutcome;
   private Outcome anyOutcome;
   
   public Map<String, Outcome> getOutcomes()
   {
      return outcomes;
   }
   
   public void setNullOutcome(Outcome outcome)
   {
      this.nullOutcome = outcome;
   }
   public Outcome getNullOutcome()
   {
      return nullOutcome;
   }
   
   public void setOutcomeValueBinding(ValueBinding<Object> outcomeValueBinding)
   {
      this.outcomeValueBinding = outcomeValueBinding;
   }
   public ValueBinding<Object> getOutcomeValueBinding()
   {
      return outcomeValueBinding;
   }

   public Outcome getAnyOutcome()
   {
      return anyOutcome;
   }
   public void setAnyOutcome(Outcome outcome)
   {
      this.anyOutcome = outcome;
   }
}