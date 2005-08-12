/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.test;

import javax.ejb.Interceptor;

import org.jboss.seam.SeamInterceptor;
import org.jboss.seam.annotations.Inject;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.ProcessInstance;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.ScopeType;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @version $Revision$
 */
@Interceptor(SeamInterceptor.class)
@Name("userManagement")
@Scope(ScopeType.APPLICATION)
public class Mock
{
   @ProcessInstance("MyProcessDefinition")
   private ProcessInstance processInstance;
   
   @Inject("test")
   private String test;
      
   public ProcessInstance getProcessInstance()
   {
      return processInstance;
   }

   public String getTest()
   {
      return test;
   }

}


