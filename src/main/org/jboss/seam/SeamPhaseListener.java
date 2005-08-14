//$Id$
package org.jboss.seam;

import java.util.Map;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.jboss.logging.Logger;

public class SeamPhaseListener implements PhaseListener {

	private static final String CONVERSATION_ID = "org.jboss.seam.ConversationId";
	
	private static Logger log = Logger.getLogger( SeamPhaseListener.class );

	public void afterPhase(PhaseEvent event) {}

	public void beforePhase(PhaseEvent event) {
		if ( event.getPhaseId()==PhaseId.RENDER_RESPONSE ) {
			log.debug("Before render response");
			getAttributes( event ).put( 
					CONVERSATION_ID, 
					Contexts.getConversationContextId() 
				);
		}
		else if ( event.getPhaseId()==PhaseId.RESTORE_VIEW ) {
			log.debug("Before restore view");
			Contexts.setConversationId( 
					(String) getAttributes(event).get(CONVERSATION_ID) 
				);
		}
	}

	private Map getAttributes(PhaseEvent event) {
		return event.getFacesContext().getViewRoot().getAttributes();
	}

	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

}
