/*
 * JBoss, the OpenSource webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.hibernate.ce.auction.process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.ce.auction.dao.ItemDAO;
import org.hibernate.ce.auction.dao.UserDAO;
import org.hibernate.ce.auction.model.Item;
import org.hibernate.ce.auction.model.User;
import org.hibernate.ce.auction.persistence.HibernateUtil;
import org.jbpm.db.GraphSession;
import org.jbpm.db.JbpmSession;
import org.jbpm.db.JbpmSessionFactory;
import org.jbpm.db.TaskMgmtSession;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @version $Revision$
 */
public class Process
{
   private static ProcessDefinition processDefinition = null;
   
   public static JbpmSessionFactory jbpmSessionFactory = JbpmSessionFactory.buildJbpmSessionFactory();
   
   public static final String PROCESSNAME = "process";
   
   public static ProcessInstance getNewProcessInstance()
   {
      JbpmSession jbpmSession = jbpmSessionFactory.openJbpmSession();
      jbpmSession.beginTransaction();
      ProcessInstance processInstance = null;
      try
      {
         processDefinition = jbpmSession.getGraphSession().findLatestProcessDefinition(PROCESSNAME);
         processInstance = new ProcessInstance(processDefinition);
         jbpmSession.getGraphSession().saveProcessInstance(processInstance);
      }
      finally
      {
         jbpmSession.commitTransactionAndClose();
      }
      return processInstance;
   }

   public void signal(long processInstanceId)
   {
      JbpmSession jbpmSession = jbpmSessionFactory.openJbpmSession();
      jbpmSession.beginTransaction();
      try
      {
         GraphSession graphSession = jbpmSession.getGraphSession();
         ProcessInstance processInstance = graphSession.loadProcessInstance(processInstanceId);
         processInstance.signal();
         jbpmSession.getGraphSession().saveProcessInstance(processInstance);
      }
      finally
      {
         jbpmSession.commitTransactionAndClose();
      }
   }
   
   public void signal(long processInstanceId, String transitionName)
   {
      JbpmSession jbpmSession = jbpmSessionFactory.openJbpmSession();
      jbpmSession.beginTransaction();
      try
      {
         GraphSession graphSession = jbpmSession.getGraphSession();
         ProcessInstance processInstance = graphSession.loadProcessInstance(processInstanceId);
         processInstance.signal(transitionName);
         jbpmSession.getGraphSession().saveProcessInstance(processInstance);
      }
      finally
      {
         jbpmSession.commitTransactionAndClose();
      }
   }

   /*
   public ProcessInstance getProcessInstance(long id)
   {
      JbpmSession jbpmSession = jbpmSessionFactory.openJbpmSession();
      jbpmSession.beginTransaction();
      ProcessInstance processInstance = null;
      try
      {
         processInstance = jbpmSession.getGraphSession().loadProcessInstance(id);
      }
      finally
      {
         jbpmSession.commitTransactionAndClose();
      }
      return processInstance;
   }
   */
   
   public List findPendingTasks()
   {
      JbpmSession jbpmSession = jbpmSessionFactory.openJbpmSession();
      jbpmSession.beginTransaction();
      List<Item> returnList = new ArrayList<Item>();
      try
      {
         TaskMgmtSession taskMgmtSession = jbpmSession.getTaskMgmtSession();
         List list = taskMgmtSession.findTaskInstances("admin");
         Iterator it = list.iterator();
         while (it.hasNext())
         {
            TaskInstance taskInstance = (TaskInstance)it.next();
            Long itemId = (Long)taskInstance.getToken().getProcessInstance().getContextInstance().getVariable("itemId");
            ItemDAO itemDAO = new ItemDAO();
            Item item = itemDAO.getItemById(itemId, false);
            returnList.add(item);
         }
      }
      finally
      {
         jbpmSession.commitTransactionAndClose();
      }
      return returnList;
   }

   public static void saveItem(Item item)
   {
      UserDAO userDAO = new UserDAO();
      User user = userDAO.getUserById(new Long(1), false);

      item.setSeller(user);
      ItemDAO itemDAO = new ItemDAO();
      itemDAO.makePersistent(item);

      HibernateUtil.commitTransaction();
      HibernateUtil.closeSession();
   }
   
   
   public static void save(long processInstanceId, Item item)
   {
      saveItem(item);
//
      JbpmSession jbpmSession = jbpmSessionFactory.openJbpmSession();
      jbpmSession.beginTransaction();
      try
      {
         GraphSession graphSession = jbpmSession.getGraphSession();
         ProcessInstance processInstance = graphSession.loadProcessInstance(processInstanceId);
         System.out.println("processInstanceId: " + processInstanceId);
         processInstance.getContextInstance().createVariable("itemId", item.getId());
         jbpmSession.getGraphSession().saveProcessInstance(processInstance);
      }
      finally
      {
         jbpmSession.commitTransactionAndClose();
      }
   }

   public void approve(Item item)
   {
      UserDAO userDAO = new UserDAO();
      User admin = userDAO.getUserById(new Long(2), false);
      item.approve(admin);
      HibernateUtil.closeSession();
      
   }
   
}


