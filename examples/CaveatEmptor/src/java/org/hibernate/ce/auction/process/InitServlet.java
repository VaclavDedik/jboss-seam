/*
 * JBoss, the OpenSource webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.hibernate.ce.auction.process;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.ce.auction.dao.UserDAO;
import org.hibernate.ce.auction.model.User;
import org.hibernate.ce.auction.persistence.HibernateUtil;
import org.jbpm.db.JbpmSession;
import org.jbpm.graph.def.ProcessDefinition;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @version $Revision$
 */
public class InitServlet extends HttpServlet
{

   /** The serialVersionUID */
   private static final long serialVersionUID = 1L;

   public void service(HttpServletRequest request, HttpServletResponse response)
   {
      ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
                     "<process-definition name=\"" + Process.PROCESSNAME + "\">" +
                     "  <start-state name=\"start\">" +
                     "    <transition name=\"view\" to=\"draft\">" +
                     "      <action class=\"org.hibernate.ce.auction.process.SavingHandler\" />" +
                     "    </transition>" +
                     "  </start-state>" +
                     "  <state name=\"draft\">" +
                     "    <transition name=\"submit for approval\" to=\"pending\"></transition>" +
                     "    <transition name=\"edit\" to=\"user edition\"></transition>" +
                     "  </state>" +
                     "  <task-node name=\"user edition\">" +
                     "    <task name=\"edition\">" +
                     "    </task>" +
                     "    <transition name=\"save\" to=\"draft\">" +
                     "      <action class=\"org.hibernate.ce.auction.process.SavingHandler\" />" +
                     "    </transition>" +
                     "  </task-node>" +
                     "  <task-node name=\"pending\">" +
                     "    <task>" +
                     "      <assignment class=\"org.hibernate.ce.auction.process.AutoAssigner\" />" +
                     "    </task>" +
                     "    <transition name=\"approve\" to=\"approved\"></transition>" +
                     "    <transition name=\"edit\" to=\"admin edition\"></transition>" +
                     "  </task-node>" +
                     "  <task-node name=\"admin edition\">" +
                     "    <task name=\"edition\">" +
                     "    </task>" +
                     "    <transition name=\"save\" to=\"pending\"></transition>" +
                     "  </task-node>" +
                     "  <end-state name=\"approved\"></end-state>" +
                     "</process-definition>"
                   );
      JbpmSession jbpmSession = Process.jbpmSessionFactory.openJbpmSession();
      jbpmSession.beginTransaction();
      try
      {
         Process.jbpmSessionFactory.getJbpmSchema().createSchema();
         jbpmSession.getGraphSession().saveProcessDefinition(processDefinition);
      }
      finally
      {
         jbpmSession.commitTransactionAndClose();
      }
      
      UserDAO userDAO = new UserDAO();
      User user = new User("Thomas", "Heute", "theute", "password", "theute@jboss.org");
      userDAO.makePersistent(user);
      User admin =  new User("Admin", "admin", "admin", "password", "theute@jboss.org");
      userDAO.makePersistent(admin);
      HibernateUtil.commitTransaction();
      HibernateUtil.closeSession();
      
      
      try
      {
         response.getWriter().write("Done !");
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
}


