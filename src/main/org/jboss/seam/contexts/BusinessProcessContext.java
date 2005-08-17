/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.contexts;

import org.jboss.logging.Logger;
import org.jbpm.db.JbpmSession;
import org.jbpm.db.JbpmSessionFactory;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @version $Revision$
 */
public class BusinessProcessContext implements Context
{
   private static final Logger log = Logger.getLogger(BusinessProcessContext.class);

   private ProcessInstance processInstance;
   
   public static JbpmSessionFactory jbpmSessionFactory = JbpmSessionFactory.buildJbpmSessionFactory();
   
   public JbpmSession jbpmSession; 
   
   
   public BusinessProcessContext()
   {
      log.info("Begin Business Process context");
      
      jbpmSession = jbpmSessionFactory.openJbpmSession();
   }
   
   public Object get(String name)
   {
      return processInstance.getContextInstance().getVariable(name);
   }

   public void set(String name, Object value)
   {
      processInstance.getContextInstance().setVariable(name, value);
   }

   public boolean isSet(String name)
   {
      return processInstance.getContextInstance().hasVariable(name);
   }

   public void setProcessInstance(ProcessInstance processInstance)
   {
      this.processInstance = processInstance;
   }

   public void remove(String name) 
   {
	   processInstance.getContextInstance().deleteVariable(name);
   }

   public String[] getNames() 
   {
	   return (String[]) processInstance.getContextInstance().getVariables()
	         .keySet().toArray( new String[]{} );
   }

   public ProcessInstance getProcessInstance(String name, boolean create)
   {
      jbpmSession.beginTransaction();
      try
      {
         ProcessDefinition processDefinition = jbpmSession.getGraphSession()
               .findLatestProcessDefinition(name);
         if (processDefinition != null)
         {
            processInstance = new ProcessInstance(processDefinition);
            jbpmSession.getGraphSession().saveProcessInstance(processInstance);
         } 
         else
         {
            log.warn("ProcessDefinition: " + name + " could be found");
         }
      } 
      finally
      {
         jbpmSession.commitTransaction();
      }
      return processInstance;
   }

   public void signal(String transitionName)
   {
      jbpmSession.beginTransaction();
      ProcessInstance myProcessInstance = jbpmSession.getGraphSession().loadProcessInstance(processInstance.getId());
      myProcessInstance.signal(transitionName);
      jbpmSession.commitTransactionAndClose();
   }
}


