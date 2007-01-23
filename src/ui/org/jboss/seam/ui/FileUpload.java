package org.jboss.seam.ui;

import java.io.IOException;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import org.jboss.seam.servlet.MultipartRequest;

/**
 * A file upload component.
 * 
 * @author Shane Bryzak
 */
public class FileUpload extends UIComponentBase
{
   public static final String COMPONENT_TYPE   = "org.jboss.seam.ui.FileUpload";
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.FileUpload";   
   
   @Override
   public void decode(FacesContext context)
   {
      super.decode(context);
      
      Object request = context.getExternalContext().getRequest();

      if (request instanceof MultipartRequest)
      {
         MultipartRequest req = (MultipartRequest) request;
         
         String clientId = getClientId(context);
         byte[] fileData = req.getFileBytes(clientId);
         String contentType = req.getFileContentType(clientId);
         String fileName = req.getFileName(clientId);
         
         getValueBinding("data").setValue(context, fileData);
         
         ValueBinding vb = getValueBinding("contentType");
         if (vb != null)
            vb.setValue(context, contentType);
         
         vb = getValueBinding("fileName");
         if (vb != null)
            vb.setValue(context, fileName);
      }      
   }
      
   @Override
   public void encodeEnd(FacesContext context) 
      throws IOException
   {
      super.encodeEnd(context);

      ResponseWriter writer = context.getResponseWriter();
      writer.startElement(HTML.INPUT_ELEM, this);
      writer.writeAttribute(HTML.TYPE_ATTR, HTML.FILE_ATTR, null);
      String clientId = this.getClientId(context);      
      writer.writeAttribute(HTML.ID_ATTR, clientId, null);     
      writer.writeAttribute(HTML.NAME_ATTR, clientId, null);
      writer.endElement(HTML.INPUT_ELEM);
   }
   
   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }

}
