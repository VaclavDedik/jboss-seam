/*
 * JBoss, the OpenSource webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.example.bpm;

import javax.servlet.http.HttpServlet;

import org.jbpm.db.JbpmSession;
import org.jbpm.db.JbpmSessionFactory;
import org.jbpm.graph.def.ProcessDefinition;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @version $Revision$
 */
public class InitServlet extends HttpServlet
{

   /** The serialVersionUID */
   private static final long serialVersionUID = 1L;
   
   public static JbpmSessionFactory jbpmSessionFactory = JbpmSessionFactory.buildJbpmSessionFactory();
   
   private static final String PROCESSNAME = "MyProcessDefinition";

   @Override
   public void init()
   {
      ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
                     "<process-definition name=\"" + PROCESSNAME + "\">" +
                     "  <start-state name=\"start\">" +
                     "    <transition name=\"view\" to=\"draft\">" +
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
                     "    </transition>" +
                     "  </task-node>" +
                     "  <task-node name=\"pending\">" +
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
      JbpmSession jbpmSession = jbpmSessionFactory.openJbpmSession();
      jbpmSession.beginTransaction();
      try
      {
         jbpmSessionFactory.getJbpmSchema().createSchema();
         jbpmSession.getGraphSession().saveProcessDefinition(processDefinition);
      }
      finally
      {
         jbpmSession.commitTransactionAndClose();
      }
   }
}


