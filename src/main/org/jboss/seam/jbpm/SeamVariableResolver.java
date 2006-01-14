package org.jboss.seam.jbpm;

import org.jboss.seam.Component;
import org.jbpm.jpdl.el.ELException;
import org.jbpm.jpdl.el.VariableResolver;

public class SeamVariableResolver implements VariableResolver {

	public Object resolveVariable(String name) throws ELException {
	   name = name.replace('$', '.');
	   return Component.getInstance(name, true);
	}

}
