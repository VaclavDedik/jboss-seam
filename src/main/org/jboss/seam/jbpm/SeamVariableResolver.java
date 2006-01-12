package org.jboss.seam.jbpm;

import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.VariableResolver;

import org.jboss.seam.Component;

public class SeamVariableResolver implements VariableResolver {

	public Object resolveVariable(String name) throws ELException {
	   name = name.replace('$', '.');
	   return Component.getInstance(name, true);
	}

}
