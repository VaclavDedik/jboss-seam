package org.jboss.seam.drools;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;

import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.core.Mutable;
import org.jboss.seam.core.Expressions.ValueExpression;

/**
 * A conversation-scoped Drools WorkingMemory for a named RuleBase
 * 
 * @author Gavin King
 *
 */
@Scope(ScopeType.CONVERSATION)
@Intercept(NEVER)
public class ManagedWorkingMemory implements Mutable, Serializable
{
   private static final long serialVersionUID = -1746942080571374743L;
   
   private String ruleBaseName;
   private WorkingMemory workingMemory;
   private ValueExpression<RuleBase> ruleBase;
   public boolean clearDirty()
   {
      return true;
   }
   /**
    * The name of a Seam context variable holding an
    * instance of org.drools.RuleBase
    * 
    * @return a context variable name
    * @deprecated
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
    * @deprecated
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
         RuleBase ruleBase;
         if (this.ruleBase!=null)
         {
            ruleBase = this.ruleBase.getValue();
         }
         else if (ruleBaseName!=null)
         {
            //deprecated stuff
            ruleBase = (RuleBase) Component.getInstance(ruleBaseName, true);
         }
         else
         {
            throw new IllegalStateException("No RuleBase");
         }
                
         if (ruleBase==null)
         {
            throw new IllegalStateException("RuleBase not found: " + ruleBaseName);
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
   public ValueExpression<RuleBase> getRuleBase()
   {
      return ruleBase;
   }
   public void setRuleBase(ValueExpression<RuleBase> ruleBase)
   {
      this.ruleBase = ruleBase;
   }
}
