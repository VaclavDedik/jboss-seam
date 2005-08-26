// $Id$
package org.jboss.seam.core;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.contexts.BusinessProcessContext;
import org.jboss.seam.contexts.Contexts;
import org.jbpm.db.JbpmSession;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Implementation of JbpmTask.
 *
 * @author Steve Ebersole
 */
@Scope(ScopeType.CONVERSATION)
public class JbpmTask {

	private static final Logger log = Logger.getLogger( JbpmTask.class );

	private TaskInstance task;

	@Create()
	public void create(Component component) {
		log.trace( "creating jbpm task component [" + component + "]" );
		Long taskId = ( Long ) Contexts.getStatelessContext().get( BusinessProcessContext.TASK_ID_KEY );
      //TODO: the name looks wrong!!!
		JbpmSession jbpmSession = ( JbpmSession ) Component.getInstance( ManagedJbpmSession.class.getName(), true );
		task = jbpmSession.getTaskMgmtSession().loadTaskInstance( taskId );
	}

	@Unwrap
	public TaskInstance getTaskInstance() {
		return task;
	}

	@Destroy
	public void destory() {
		task = null;
	}
}
