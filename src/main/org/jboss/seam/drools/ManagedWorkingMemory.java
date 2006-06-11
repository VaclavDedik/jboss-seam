package org.jboss.seam.drools;

import static org.jboss.seam.InterceptionType.NEVER;

import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

/**
 * A conversation-scoped Drools WorkingMemory for a named RuleBase
 * 
 * @author Gavin King
 *
 */
@Scope(ScopeType.CONVERSATION)
@Intercept(NEVER)
public class ManagedWorkingMemory
{
   private String ruleBaseName;
   private WorkingMemory workingMemory;

   /**
    * The name of a Seam context variable holding an
    * instance of org.drools.RuleBase
    * 
    * @return a context variable name
    */
   public String getRuleBaseName()
   {
      return ruleBaseName;
   }
   
   /**
    * The name of a Seam context variable holding an
    * instance of org.drools.RuleBase
    * 
    * @param ruleBaseName a context variable name
    */
   public void setRuleBaseName(String ruleBaseName)
   {
      this.ruleBaseName = ruleBaseName;
   }
   
   @Unwrap
   public WorkingMemory getWorkingMemory()
   {
      if (workingMemory==null)
      {
         RuleBase ruleBase = (RuleBase) Component.getInstance(ruleBaseName, true);
         if (ruleBase==null)
         {
            throw new IllegalArgumentException("RuleBase not found: " + ruleBaseName);
         }
         workingMemory = ruleBase.newWorkingMemory();
      }
      return workingMemory;
   }
   
   @Destroy
   public void destroy()
   {
      workingMemory.dispose();
   }

}
