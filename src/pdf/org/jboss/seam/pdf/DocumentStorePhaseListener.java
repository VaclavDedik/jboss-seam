package org.jboss.seam.pdf;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.util.Parameters;


public class DocumentStorePhaseListener 
    implements PhaseListener 
{
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }


    public void afterPhase(PhaseEvent phaseEvent) {
        // ...
    }


    public void beforePhase(PhaseEvent phaseEvent) {
        String rootId = phaseEvent.getFacesContext().getViewRoot().getViewId();
        
        String id = (String)
        Parameters.convertMultiValueRequestParameter(Parameters.getRequestParameters(),
                                                     "docId",
                                                     String.class);              
        if (rootId.startsWith("/seam-doc")) {            
            sendContent(phaseEvent.getFacesContext(), id);
        }
    }

    public void sendContent(FacesContext context, String contentId) {
        try {
            DocumentStore store = DocumentStore.instance();
            byte[] data = store.dataForId(contentId);

            HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
            response.setContentType(store.typeForId(contentId));
            if (data != null) {
                response.getOutputStream().write(data);
            }
            context.responseComplete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
