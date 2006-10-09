package org.jboss.seam.core;

import javax.ejb.Local;
import javax.ejb.Timer;
import javax.interceptor.InvocationContext;

import org.jboss.seam.Component;

@Local
public interface LocalDispatcher
{
   public Timer schedule(InvocationContext invocation, Component component);
}
