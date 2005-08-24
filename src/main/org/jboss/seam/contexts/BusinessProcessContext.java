/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.contexts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jboss.logging.Logger;
import org.jboss.seam.Seam;
import org.jbpm.context.exe.ContextInstance;

/**
 * Exposes a jbpm variable context instance for reading/writing.
 *
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @version $Revision$
 */
public class BusinessProcessContext implements Context {

	public static final String TASK_ID_KEY = "org.jboss.seam.bpm.taskId";
	public static final String PROCESS_ID_KEY = "org.jboss.seam.bpm.processId";
	public static final String PROCESS_DEF_KEY = "org.jboss.seam.bpm.processDefinitionName";

	private static final Logger log = Logger.getLogger(BusinessProcessContext.class);

	private Map tempContext;
	private ContextInstance jbpmContext;

	/**
	 * Constructs a new instance of BusinessProcessContext.
	 */
	public BusinessProcessContext() {
		log.debug( "Begin BusinessProcessContext" );
	}

	/**
	 * Retrieves a map of all data needed to recover the current state of this context
	 * upon a later request (within the same conversation).  This includes the
	 * processInstance and taskInstance ids as well as any temporary state.
	 *
	 * @return
	 */
	public Map getRecoverableState() {
		Map map = new HashMap();

		if ( tempContext != null ) {
			map.putAll( tempContext );
			tempContext.clear();
			tempContext = null;
		}

		if ( log.isTraceEnabled() ) {
			log.trace( "recoverable state : " + map );
		}
		return map;
	}

	public void recover(Map previousState) {
		if ( log.isTraceEnabled() ) {
			log.trace( "recovering from state : " + previousState );
		}

		tempContext = new HashMap();
		tempContext.putAll( previousState );
	}

	public void release() {
		jbpmContext = null;
	}

	private void convertTempToPersistent() {
		if ( tempContext == null ) return;

		if ( log.isTraceEnabled() ) {
			log.trace( "converting temp contex to be persistent [" + jbpmContext.getProcessInstance().getId() + "] : " + tempContext );
		}
		Iterator itr = tempContext.entrySet().iterator();
		while ( itr.hasNext() ) {
			final Map.Entry entry = ( Map.Entry ) itr.next();
			jbpmContext.setVariable( ( String ) entry.getKey(), entry.getValue() );
		}

		tempContext.clear();
		tempContext = null;
	}


	// Context impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public Object get(String name) {
		if ( jbpmContext != null ) {
			return jbpmContext.getVariable( name );
		}
		else {
			return temp().get( name );
		}
	}

	public void set(String name, Object value) {
		if ( jbpmContext != null ) {
			jbpmContext.setVariable( name, value );
		}
		else {
			temp().put( name, value );
		}
	}

	public boolean isSet(String name) {
		if ( jbpmContext != null ) {
			return jbpmContext.hasVariable( name );
		}
		else {
			return temp().containsKey( name );
		}
	}

	public void remove(String name) {
		if ( jbpmContext != null ) {
			jbpmContext.deleteVariable( name );
		}
		else {
			temp().remove( name );
		}
	}

	public String[] getNames() {
		if ( jbpmContext != null ) {
			return ( String[] ) jbpmContext.getVariables().keySet().toArray( new String[]{} );
		}
		else {
			return ( String[] ) temp().keySet().toArray( new String[]{} );
		}
	}

	private Map temp() {
		if ( tempContext == null ) {
			tempContext = new HashMap();
		}
		return tempContext;
	}

	public Object get(Class clazz) {
		return get( Seam.getComponentName( clazz ) );
	}

   public void flush()
   {
      //TODO
   }
}
