package org.jboss.seam.jbpm;

import org.jboss.seam.Component;
import org.jboss.seam.core.Init;
import org.jbpm.jpdl.el.ELException;
import org.jbpm.jpdl.el.VariableResolver;
import org.jbpm.jpdl.el.impl.JbpmVariableResolver;

public class SeamVariableResolver implements VariableResolver 
{
   
   private VariableResolver jbpmVariableResolver = new JbpmVariableResolver();

	public Object resolveVariable(String name) throws ELException 
   {
	   name = name.replace('$', '.');
	   Object instance = Component.getInstance(name, true);
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
