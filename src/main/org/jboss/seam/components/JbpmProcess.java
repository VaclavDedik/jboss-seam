/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.components;

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
import org.jbpm.graph.exe.ProcessInstance;

/**
 * Implementation of JbpmProcess.
 *
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @version $Revision$
 */
@Scope(ScopeType.CONVERSATION)
public class JbpmProcess {

	private static final Logger log = Logger.getLogger( JbpmProcess.class );

	private ProcessInstance process;

	@Create()
	public void create(Component component) {
		log.trace( "creating jbpm task component [" + component + "]" );
		// first try to find a PI id...
		Long processId = ( Long ) Contexts.getStatelessContext().get( BusinessProcessContext.PROCESS_ID_KEY );
		if ( processId != null ) {
			process = getJbpmSession().getGraphSession().loadProcessInstance( processId );
		}
		else {
			// otherwise try to create a new PI from the PD-name
			String processDefinitionName = ( String ) Contexts.getStatelessContext().get( BusinessProcessContext.PROCESS_DEF_KEY );
			JbpmSession jbpmSession = getJbpmSession();
			process = new ProcessInstance(
			        jbpmSession.getGraphSession().findLatestProcessDefinition( processDefinitionName )
			);
			process.signal();
			jbpmSession.getGraphSession().saveProcessInstance( process );
		}
	}

	@Unwrap
	public ProcessInstance getProcessInstance() {
		return process;
	}

	@Destroy()
	public void cleanup() {
		process = null;
	}

	private JbpmSession getJbpmSession() {
		return ( JbpmSession ) Component.getInstance( ManagedJbpmSession.class.getName(), true );
	}
}
