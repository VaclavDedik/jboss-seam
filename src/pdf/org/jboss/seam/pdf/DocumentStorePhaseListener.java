package org.jboss.seam.pdf;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.core.Pages;
import org.jboss.seam.util.Parameters;


public class DocumentStorePhaseListener 
    implements PhaseListener 
{
    private static final long serialVersionUID = 7308251684939658978L;

    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }


    public void afterPhase(PhaseEvent phaseEvent) {
        // ...
    }


    public void beforePhase(PhaseEvent phaseEvent) {
        String rootId = Pages.getViewId( phaseEvent.getFacesContext() );
        
        String id = (String)
        Parameters.convertMultiValueRequestParameter(Parameters.getRequestParameters(),
                                                     "docId",
                                                     String.class);              
        if (rootId.contains("/seam-doc")) {            
            sendContent(phaseEvent.getFacesContext(), id);
        }
    }

    public void sendContent(FacesContext context, String contentId) {
        try {            
            DocumentData documentData = DocumentStore.instance().getDocumentData(contentId);
            
            byte[] data = documentData.getData();

            HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
            response.setContentType(documentData.getDocType().getMimeType());
            
            response.setHeader("Content-Disposition", 
                               "inline; filename=\"" + documentData.getFileName() + "\"");
            
            if (data != null) {
                response.getOutputStream().write(data);
            }
            context.responseComplete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
