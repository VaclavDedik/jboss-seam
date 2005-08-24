/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.interceptors;

import org.jboss.seam.annotations.BeginProcess;
import org.jboss.seam.annotations.StartTask;
import org.jboss.seam.annotations.CompleteTask;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.BusinessProcessContext;
import org.jboss.seam.components.JbpmTask;
import org.jboss.seam.components.JbpmProcess;
import org.jboss.seam.finders.ComponentFinder;
import org.jboss.logging.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;
import java.lang.reflect.Method;

/**
 * Interceptor which handles interpretation of jBPM-related annotations.
 *
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @version $Revision$
 */
public class BusinessProcessInterceptor extends AbstractInterceptor {

	private static final Logger log = Logger.getLogger( BusinessProcessInterceptor.class );

	@AroundInvoke
	public Object manageBusinessProcessContext(InvocationContext invocation) throws Exception {
		Method method = invocation.getMethod();
		log.trace( "Starting method interception : " + method.getDeclaringClass().getName() + "." + method.getName() );

		Object result = invocation.proceed();

		if ( method.isAnnotationPresent( BeginProcess.class ) ) {
			log.trace( "encountered @BeginProcess" );
			beginProcess( method.getAnnotation( BeginProcess.class ).name() );
		}
		else if ( method.isAnnotationPresent( StartTask.class ) ) {
			log.trace( "encountered @StartTask" );
			Long id = ( Long ) Contexts.lookup( method.getAnnotation( StartTask.class ).contextName() );
			startTask( id );
		}
		else if ( method.isAnnotationPresent( CompleteTask.class ) ) {
			log.trace( "encountered @CompleteTask" );
			CompleteTask tag = method.getAnnotation( CompleteTask.class );
			Long id = ( Long ) Contexts.lookup( tag.contextName() );
			String transitionName = determineCompleteTaskTransition( result, tag.transitionMap() );
			completeTask( id, transitionName );
		}

		return result;
	}

	private void beginProcess(String processDefinitionName) {
		Contexts.getStatelessContext().set( BusinessProcessContext.PROCESS_DEF_KEY, processDefinitionName );
		ComponentFinder.getComponentInstance( JbpmProcess.class.getName(), true );
	}

	private void startTask(Long id) {
		Contexts.getStatelessContext().set( BusinessProcessContext.TASK_ID_KEY, id );
		TaskInstance task = ( TaskInstance ) ComponentFinder.getComponentInstance( JbpmTask.class.getName(), true );
		task.start();
	}

	private void completeTask(Long id, String transitionName) {
		Contexts.getStatelessContext().set( BusinessProcessContext.TASK_ID_KEY, id );
		TaskInstance task = ( TaskInstance ) ComponentFinder.getComponentInstance( JbpmTask.class.getName(), true );

		if ( transitionName == null ) {
			task.end();
		}
		else {
			task.end( transitionName );
		}
	}

	private String determineCompleteTaskTransition(Object result, String[] mappings) {
		final String resultKey = result + "=>";
		String transition = null;
		for ( final String mapping : mappings ) {
			if ( mapping.startsWith( resultKey ) ) {
				transition = mapping.substring( resultKey.length() );
				break;
			}
		}
		return transition;
	}
}
