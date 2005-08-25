/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.test;

import javax.ejb.Interceptor;
import javax.ejb.Remove;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ejb.SeamInterceptor;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @version $Revision$
 */
@Interceptor(SeamInterceptor.class)
@Name("foo")
@Scope(ScopeType.SESSION)
public class Foo
{
   @In
   private ProcessInstance processInstance;
   
   @In
   private String test;
      
   public ProcessInstance getProcessInstance()
   {
      return processInstance;
   }

   public String getTest()
   {
      return test;
   }
   
   public String foo() { return "foo"; }

   @Remove
   public void destroy() {}
   
}


