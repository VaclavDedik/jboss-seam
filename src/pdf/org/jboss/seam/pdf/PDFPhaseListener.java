package org.jboss.seam.pdf;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.util.Parameters;

//import org.jboss.seam.ui.facelet.*;

public class PDFPhaseListener 
    implements PhaseListener 
{
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }


    public void afterPhase(PhaseEvent phaseEvent) {
        String rootId = phaseEvent.getFacesContext().getViewRoot().getViewId();
    }


    public void beforePhase(PhaseEvent phaseEvent) {
        String rootId = phaseEvent.getFacesContext().getViewRoot().getViewId();
        
        if (rootId.startsWith("/seam-pdf")) {
            String id = (String)
                Parameters.convertMultiValueRequestParameter(Parameters.getRequestParameters(),
                                                             "pdfId",
                                                             String.class);
            sendPDF(phaseEvent.getFacesContext(), id);
        }
    }

    public void sendPDF(FacesContext context, String id) {
        try {
            PDFStore store = PDFStore.instance();
            byte[] data = store.getData(id);

            HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
            response.setContentType("application/pdf");
            if (data != null) {
                response.getOutputStream().write(data);
            }
            context.responseComplete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//     public void sendPDF(PhaseEvent phaseEvent) {
//         try {
//             FacesContext context = phaseEvent.getFacesContext();
//             UIViewRoot viewRoot = context.getViewRoot();

//             SeamFaceletViewHandler handler = (SeamFaceletViewHandler) 
//                 context.getApplication().getViewHandler();
//             handler.myBuildView(context, viewRoot);
            
//             ExternalContext externalContext = context.getExternalContext();            
//             HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
//             response.setContentType("application/pdf");
             
//             Document document = new Document();

//             PdfWriter.getInstance(document, response.getOutputStream());

//             document.open();
//             document.add(new Paragraph("Hello World " + new Date()));
//             document.close();

//             context.responseComplete();
//         } catch (DocumentException e) {
//             e.printStackTrace();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

}
