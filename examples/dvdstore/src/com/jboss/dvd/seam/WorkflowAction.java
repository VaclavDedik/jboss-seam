/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import javax.ejb.Interceptor;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.BeginTask;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.CreateProcess;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelectionIndex;
import org.jboss.seam.core.ManagedJbpmSession;
import org.jboss.seam.ejb.SeamInterceptor;
import org.jboss.seam.jsf.ListDataModel;

import org.jbpm.db.JbpmSession;
import org.jbpm.db.JbpmSessionFactory;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.def.ProcessDefinition;    

import org.jbpm.taskmgmt.exe.TaskInstance;

import javax.naming.*;

@Stateful
@Name("workflow")
@Conversational(ifNotBegunOutcome="admin")
@LoggedIn
@Interceptor(SeamInterceptor.class)
public class WorkflowAction
    implements Workflow,
               Serializable
{
    String track;

    public String getTrack() {return track;}
    public void   setTrack(String track) {this.track=track;}

    @In(create=true)
    JbpmHelper jbpmHelper;

    @In(value="currentUser",required=false)
    Admin admin;

    //     @In(scope=ScopeType.PROCESS, required=false)
    //    long orderId;

    @PersistenceContext(unitName="dvd")
    EntityManager em;

    @Out(required=false)
    ListDataModel model;
    List<TaskInstance> tasks;

    @Out(required=false)
    TaskInstance currentTask;

    @Out(required=false)
    Order order;

    public ListDataModel getTasks() {
        return model;
    }

    @Begin
    public String findTasks() {
        tasks = jbpmHelper.getTasksFor("shipper");
        model = new ListDataModel(tasks);

        return "admin";
    }
    
    public String viewTask() {
        currentTask = tasks.get(model.getRowIndex());
        order       = getOrder(currentTask);

        return "ship";
    }

    public String ship() {
        JbpmSession  jbpmSession = jbpmHelper.getJbpmSession();
        
        // refresh task since need to act on it
        currentTask = jbpmHelper.loadTask(jbpmSession, currentTask.getId());
        order       = getOrder(currentTask);

        order.ship(track);

        currentTask.end();
       
        jbpmSession.getSession().flush();

        // refresh list
        return findTasks();
    }

    private Order getOrder(TaskInstance task) {
        System.out.println("GET ORDER");
        long  orderId = (Long) jbpmHelper.getVariable(task, "orderId");
        Order order   = (Order) em.createQuery("from Order o JOIN FETCH o.orderLines where o.orderId = :orderId")
            .setParameter("orderId", orderId)
            .getSingleResult();

        return order;
    }

    @Begin
    public String loadProcess() {
        try {
            InitialContext ctx = new InitialContext();
            JbpmSessionFactory factory = (JbpmSessionFactory) ctx.lookup("java:/jbpm/JbpmSessionFactory");
            JbpmSession jbpmSession = factory.openJbpmSession();

            
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            ProcessDefinition processDefinition = 
                ProcessDefinition.parseXmlInputStream(loader.getResourceAsStream("jbpm-ordermanagement.xml"));

            //             Map vals = processDefinition.getDefinitions();
            //             for (Object key:vals.keySet()) {
            //                 System.out.println("*[" + key + "]: " + vals.get(key));
            //             }
                            
            String processDefinitionName = processDefinition.getName();
            if (processDefinitionName!=null) {
                ProcessDefinition previousLatestVersion = 
                    jbpmSession.getGraphSession().findLatestProcessDefinition(processDefinitionName);

                if (previousLatestVersion!=null) {
                    processDefinition.setVersion( previousLatestVersion.getVersion()+1 );
                } else {
                    processDefinition.setVersion(1);
                }
            }
            
            jbpmSession
                .getGraphSession()
                .saveProcessDefinition(processDefinition);

            jbpmSession.getSession().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @End
    public String reset() {
        return null;
    }

    @Destroy 
    @Remove
    public void destroy() {
    }
}
