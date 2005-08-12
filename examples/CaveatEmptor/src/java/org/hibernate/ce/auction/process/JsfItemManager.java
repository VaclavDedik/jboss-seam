/*
 * JBoss, the OpenSource webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.hibernate.ce.auction.process;

import java.util.List;

import org.hibernate.ce.auction.model.Item;
import org.jbpm.db.JbpmSessionFactory;
import org.jbpm.graph.exe.ProcessInstance;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @version $Revision$
 */
public class JsfItemManager
{
   public static JbpmSessionFactory jbpmSessionFactory = JbpmSessionFactory.buildJbpmSessionFactory();
   
   public Item item;
   
   public long processInstanceId;
   
   public Process process = new Process();
   
   /**
    * Beginning of what would be a user conversation scope
    * 
    * @return
    */
   public String start()
   {
      ProcessInstance processInstance = Process.getNewProcessInstance();
      setProcessInstanceId(processInstance.getId());
      return "create";
   }
   
   public String createItem()
   {
      // Go to draft state
      process.signal(processInstanceId, "view");
      return "view";
   }

   public String editItem()
   {
      // Go to edit mode
      process.signal(processInstanceId, "edit");
      return "edit";
   }

   public String saveItem()
   {
      // Go to draft state
      process.signal(processInstanceId, "save");
      return "save";
   }

   /**
    * This would end a user conversation scope
    * 
    * @return
    */
   public String submitItem()
   {
      
//      item.setPendingForApproval();
      // Go to pending state
      process.signal(processInstanceId, "submit for approval");
      return "submit";
   }

   /**
    * This could start the admin conversation scope
    * 
    * @return
    */
   public List getPendingTasks()
   {
      return process.findPendingTasks();
   }

   /**
    * This could end the admin conversation scope
    * 
    * @return
    */
   public String approveItem()
   {
      process.approve(item);
      
      process.signal(processInstanceId, "approve");
      return "approve";
   }
   
   public Item getItem()
   {
      return item;
   }

   public void setItem(Item item)
   {
      this.item = item;
   }

   public long getProcessInstanceId()
   {
      return processInstanceId;
   }
   
   public void setProcessInstanceId(long processInstanceId)
   {
      this.processInstanceId = processInstanceId;
   }

}


