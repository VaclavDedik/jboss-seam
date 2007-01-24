package org.jboss.seam.pdf;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.util.Parameters;

public class DocumentStoreServlet 
    extends HttpServlet 
{

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, 
               IOException 
    {
        String contentId = (String)
        Parameters.convertMultiValueRequestParameter(Parameters.getRequestParameters(),
                "docId",
                String.class);        
        
        
        DocumentStore store = DocumentStore.instance();
        byte[] data = store.dataForId(contentId);          
          
        response.setContentType(store.typeForId(contentId));
        response.setHeader("Content-Disposition", 
                           "inline; filename=\"" + store.fileNameForId(contentId) + "\"");
                
        if (data != null) {
            response.getOutputStream().write(data);
        }
    }    
}
