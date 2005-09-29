package com.jboss.dvd.seam;

import java.util.List;

import org.jbpm.context.exe.ContextInstance;

import org.jbpm.db.JbpmSession;
import org.jbpm.db.JbpmSessionFactory;
import org.jbpm.db.TaskMgmtSession;

import org.jbpm.graph.def.ProcessDefinition;    
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.ManagedJbpmSession;


@Name("jbpmHelper")
public class JbpmHelper
{
    static final String ORDER_MANAGEMENT = "OrderManagement";

    public List<TaskInstance> getShipperTasks() {
        return getTasksFor("shipper");
    }


    public String loadProcess() {
        try {
            JbpmSession jbpmSession = (JbpmSession)
                Component.getInstance(ManagedJbpmSession.class, true);

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            ProcessDefinition processDefinition = 
                ProcessDefinition.parseXmlInputStream(loader.getResourceAsStream("jbpm-ordermanagement.xml"));

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

    
    public void startOrderProcess(Order order) 
    {
        JbpmSession     jbpmSession     = getJbpmSession();
        ProcessInstance processInstance = createProcessInstance(jbpmSession,
                                                                ORDER_MANAGEMENT);

        setVariable(processInstance, "orderId",  order.getOrderId());
        setVariable(processInstance, "customer", order.getCustomer().getUserName());
        setVariable(processInstance, "amount",   order.getTotalAmount());

        processInstance.signal();

        jbpmSession.getGraphSession().saveProcessInstance(processInstance);
        jbpmSession.getSession().flush();
    }



    public List<TaskInstance> getTasksFor(String actor) {
        JbpmSession jbpmSession = getJbpmSession();
        TaskMgmtSession taskSession = new TaskMgmtSession(jbpmSession);

        return (List<TaskInstance>) taskSession.findTaskInstances("shipper");
    }

    public JbpmSession getJbpmSession() {
        return (JbpmSession) Component.getInstance(ManagedJbpmSession.class, true);
    }

    public ProcessInstance createProcessInstance(JbpmSession jbpmSession, String name) {
        ProcessDefinition processDefinition = 
            jbpmSession.getGraphSession().findLatestProcessDefinition(name);
        return new ProcessInstance(processDefinition);
    }

    public Object getVariable(ProcessInstance processInstance, String name) {
        return processInstance.getContextInstance().getVariable(name);
    }

    public Object getVariable(TaskInstance task, String name) {
        return getVariable(task.getTaskMgmtInstance().getProcessInstance(), name);
    }

    public void setVariable(ProcessInstance processInstance,
                            String name,
                            Object value) 
    {
        processInstance.getContextInstance().setVariable(name,value);
    }

    public TaskInstance loadTask(JbpmSession jbpmSession, long taskId) {
        return jbpmSession.getTaskMgmtSession().loadTaskInstance(taskId);
    }

}

