package org.jboss.seam.bpm;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Init;
import org.jbpm.jpdl.el.ELException;
import org.jbpm.jpdl.el.VariableResolver;
import org.jbpm.jpdl.el.impl.JbpmVariableResolver;

/**
 * Resolves Seam context variables for jBPM.
 * 
 * @author Gavin King
 *
 */
class SeamVariableResolver implements VariableResolver 
{
   
   private VariableResolver jbpmVariableResolver = new JbpmVariableResolver();

	public Object resolveVariable(String name) throws ELException 
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         //if no Seam contexts, bypass straight through to jBPM
         return jbpmVariableResolver.resolveVariable(name);
      }
      else
      {
   	   Object instance = Component.getInstance(name);
         if (instance==null)
         {
            instance = jbpmVariableResolver.resolveVariable(name);
            if (instance==null)
            {
               return Init.instance().getRootNamespace().getChild(name);
            }
            else
            {
               return instance;
            }
         }
         else
         {
            return instance;
         }
      }
	}

}
