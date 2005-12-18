package com.jboss.dvd.seam;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.ManagedJbpmSession;
import org.jbpm.db.JbpmSession;
import org.jbpm.db.TaskMgmtSession;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;


@Name("jbpmHelper")
public class JbpmHelper
{
    static final String ORDER_MANAGEMENT = "OrderManagement";
    static final String ORDER_DEF1       = "jbpm-ordermanagement1.xml";
    static final String ORDER_DEF2       = "jbpm-ordermanagement2.xml";
    static final String ORDER_DEF3       = "jbpm-ordermanagement3.xml";


    List<TaskInstance> shipTasks    = null;
    List<TaskInstance> approveTasks = null;

    public List<TaskInstance> getShipperTasks() {
        if (shipTasks == null) {
            shipTasks = tasksByType(getTasksFor("shipper"), "ship");
        }
        
        return shipTasks;
    }

    public List<TaskInstance> getApproveTasks() {
        if (approveTasks == null) {
            approveTasks = tasksByType(getTasksFor("shipper"), "approve");
        }
        
        return approveTasks;
    }

    public String loadProcess1() {
        return loadProcess(ORDER_DEF1);
    }
    public String loadProcess2() {
        return loadProcess(ORDER_DEF2);
    }
    public String loadProcess3() {
        return loadProcess(ORDER_DEF3);
    }

    private String loadProcess(String name) {
        try {
            JbpmSession jbpmSession = getJbpmSession();

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            ProcessDefinition processDefinition = 
                ProcessDefinition.parseXmlInputStream(loader.getResourceAsStream(name));
                                                      
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
            
            jbpmSession.getGraphSession().saveProcessDefinition(processDefinition);
            jbpmSession.getSession().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "admin";
    }
    
    
    private JbpmSession getJbpmSession() {
        return (JbpmSession) Component.getInstance(ManagedJbpmSession.class, true);
    }
    
    private List<TaskInstance> tasksByType(List<TaskInstance> taskList, String taskName) {
        List<TaskInstance> tasks = new ArrayList<TaskInstance>();
        for(TaskInstance task: taskList) {
            if (taskName.equals(task.getName())) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    private List<TaskInstance> getTasksFor(String actor) {
        JbpmSession     jbpmSession = getJbpmSession();
        TaskMgmtSession taskSession = new TaskMgmtSession(jbpmSession);

        return (List<TaskInstance>) taskSession.findTaskInstances(actor);
    }

    private List<TaskInstance> getPooledTasksFor(String actor) {
        JbpmSession     jbpmSession = getJbpmSession();
        TaskMgmtSession taskSession = new TaskMgmtSession(jbpmSession);

        return (List<TaskInstance>) taskSession.findPooledTaskInstances(actor);
    }

    private ProcessInstance createProcessInstance(JbpmSession jbpmSession, String name) {
        ProcessDefinition processDefinition = 
            jbpmSession.getGraphSession().findLatestProcessDefinition(name);
        return new ProcessInstance(processDefinition);
    }
}

