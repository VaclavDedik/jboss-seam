package org.jboss.seam.jbpm;

import org.jboss.seam.Component;
import org.jboss.seam.core.Init;
import org.jbpm.jpdl.el.ELException;
import org.jbpm.jpdl.el.VariableResolver;

public class SeamVariableResolver implements VariableResolver {

	public Object resolveVariable(String name) throws ELException {
	   name = name.replace('$', '.');
	   Object instance = Component.getInstance(name, true);
      if (instance==null)
      {
         return Init.instance().getRootNamespace().getChild(name);
      }
      else
      {
         return instance;
      }
	}

}
