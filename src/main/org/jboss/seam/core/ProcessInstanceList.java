package org.jboss.seam.core;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.Unwrap;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * Support for the process list.
 * 
 * @author Gavin King
 */
@Name("org.jboss.seam.core.processInstanceList")
@Install(precedence=BUILT_IN, dependencies="org.jboss.seam.core.jbpm")
public class ProcessInstanceList
{
   
   @Unwrap
   @Transactional
   public List<ProcessInstance> getTaskInstanceList()
   {
      return getProcessInstanceList();
   }

   private List<ProcessInstance> getProcessInstanceList()
   {
      return ManagedJbpmContext.instance()
            .getSession()
            .createCriteria(ProcessInstance.class)
            .add( Restrictions.isNull("end") )
            .createCriteria("rootToken")
               .addOrder( Order.asc("nodeEnter") )
            .list();
   }
   
}
