/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.contexts;

import org.jboss.logging.Logger;
import org.jboss.seam.Seam;
import org.jbpm.db.JbpmSession;
import org.jbpm.db.JbpmSessionFactory;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.context.exe.ContextInstance;

import java.util.Collection;

/**
 * Exposes the context variables associated with a jBPM {@link ProcessInstance}
 * for reading/writing.
 * <p/>
 * Also maintains a reference to a {@link ProcessInstance} and
 * possibly a current {@link TaskInstance} considered current
 * within this context.
 *
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @version $Revision$
 */
public class BusinessProcessContext implements Context {
	private static final Logger log = Logger.getLogger(BusinessProcessContext.class);
	private static JbpmSessionFactory jbpmSessionFactory = JbpmSessionFactory.buildJbpmSessionFactory();

	public JbpmSession jbpmSession;
	private ProcessInstance processInstance;
	private TaskInstance taskInstance;

	/**
	 * Constructs a new instance of BusinessProcessContext.
	 * <p/>
	 * Additionally, obtains a {@link JbpmSession} and starts its transaction.
	 * <p/>
	 * Must ensure that {@link #release()} is called after we are done with the
	 * this context...
	 */
	public BusinessProcessContext() {
		log.debug( "Begin BusinessProcessContext" );
		jbpmSession = jbpmSessionFactory.openJbpmSession();
		jbpmSession.beginTransaction();
	}

	/**
	 * Retrieve the {@link ProcessInstance} currently associated with this
	 * context, if one.
	 *
	 * @return The associated {@link ProcessInstance}, or null if no
	 * {@link ProcessInstance} currently associated with this context.
	 */
	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	/**
	 * Retrieve the {@link TaskInstance} currently associated with this
	 * context, if one.
	 *
	 * @return The associated {@link TaskInstance}, or null if no
	 * {@link TaskInstance} currently associated with this context.
	 */
	public TaskInstance getTaskInstance() {
		return taskInstance;
	}

	/**
	 * Prepares the context based on the {@link TaskInstance} defined by the
	 * given taskId.
	 * <p/>
	 * The {@link ProcessInstance} associated with that {@link TaskInstance} is
	 * also associated with this context ({@link #getProcessInstance()}).
	 *
	 * @param taskId The id of the {@link TaskInstance} to use to prepare
	 * this context.
	 */
	public void prepareForTask(long taskId) {
		taskInstance = jbpmSession.getTaskMgmtSession().loadTaskInstance( taskId );
		processInstance = taskInstance.getTaskMgmtInstance().getProcessInstance();
	}

	/**
	 * Prepares the context based on the {@link ProcessInstance} defined by the
	 * given processInstanceId.
	 * <p/>
	 * In this case, there may not be a {@link TaskInstance} associated with
	 * the context.  We do locate all tasks associated with the current state
	 * of the {@link ProcessInstance}; if there happens to be exactly one,
	 * then we use it; otherwise there will be no {@link TaskInstance}
	 * associated with this context.
	 *
	 * @param processInstanceId The id of the {@link TaskInstance} to use to prepare
	 * this context.
	 */
	public void prepareForProcessInstance(Long processInstanceId) {
		processInstance = jbpmSession.getGraphSession().loadProcessInstance( processInstanceId );
		taskInstance = determineInitialTaskFromProcess();
	}

	/**
	 * Creates a {@link ProcessInstance} based on the given processDefinitionName
	 * and prepares the context from that created {@link ProcessInstance}.
	 * <p/>
	 * In this case, there may not be a {@link TaskInstance} associated with
	 * the context.  We do locate all tasks associated with the current state
	 * of the {@link ProcessInstance}; if there happens to be exactly one,
	 * then we use it; otherwise there will be no {@link TaskInstance}
	 * associated with this context.
	 *
	 * @param processDefinitionName The name of the {@link ProcessDefinition}
	 * from which to create the {@link ProcessInstance} to be used to
	 * prepare this context.
	 */
	public void prepareForProcessInstance(String processDefinitionName) {
		ProcessDefinition processDefinition = jbpmSession
		        .getGraphSession()
		        .findLatestProcessDefinition( processDefinitionName );
		if (processDefinition != null) {
			processInstance = new ProcessInstance(processDefinition);
			processInstance.signal();
			jbpmSession.getGraphSession().saveProcessInstance( processInstance );
			taskInstance = determineInitialTaskFromProcess();
		}
		else {
			// TODO : this should be an error...
			log.warn("ProcessDefinition: " + processDefinitionName + " could be found");
		}
	}

	public void taskCompleted() {
		taskInstance = null;
	}

	private TaskInstance determineInitialTaskFromProcess() {
		// If there happens to be just a single task-instance associated to the
		// current state of the process-instance, then I guess go ahead and use
		// it...
		Collection tasks = processInstance.getTaskMgmtInstance().getTaskInstances();
		if ( tasks != null && tasks.size() == 1 ) {
			return ( TaskInstance ) tasks.iterator().next();
		}
		else {
			return null;
		}

	}

	/**
	 * To be called at the end of the phase cycle so that we can clean up the
	 * required resources...
	 */
	public void release() {
		jbpmSession.commitTransaction();
		jbpmSession.close();

		processInstance = null;
		taskInstance = null;
	}


	// Context impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public Object get(String name) {
		return ctx().getVariable( name );
	}

	public void set(String name, Object value) {
		ctx().setVariable( name, value );
	}

	public boolean isSet(String name) {
		return ctx().hasVariable( name );
	}

	public void remove(String name) {
		ctx().deleteVariable( name );
	}

	public String[] getNames() {
		return ( String[] ) ctx().getVariables().keySet().toArray( new String[]{} );
	}

	private ContextInstance ctx() {
		if ( processInstance == null ) {
			throw new IllegalStateException(
			        "ProcessInstance not associated with this BusinessProcessContext"
			);
		}
		return processInstance.getContextInstance();
	}
   
   public <T> T get(Class<T> clazz)
   {
      return (T) get( Seam.getComponentName(clazz) );
   }
}
