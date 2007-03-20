package org.jboss.seam.ui.renderkit;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.ServletRequest;

import org.ajax4jsf.framework.renderer.AjaxComponentRendererBase;
import org.jboss.seam.ui.component.UIFileUpload;
import org.jboss.seam.ui.util.HTML;
import org.jboss.seam.web.MultipartRequest;

public class FileUploadRendererBase extends AjaxComponentRendererBase
{

   @Override
   protected Class getComponentClass()
   {
      return UIFileUpload.class;
   }
   
   @Override
   protected void doEncodeEnd(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException
   {
      UIFileUpload fileUpload = (UIFileUpload) component;
      
      writer.startElement(HTML.INPUT_ELEM, component);      
      writer.writeAttribute(HTML.TYPE_ATTR, HTML.FILE_ATTR, null);      
      
      String clientId = component.getClientId(context);      
      writer.writeAttribute(HTML.ID_ATTR, clientId, null);     
      writer.writeAttribute(HTML.NAME_ATTR, clientId, null);
      if (fileUpload.getAccept() != null)
      {
         writer.writeAttribute(HTML.ACCEPT_ATTR, fileUpload.getAccept(), null);
      }
      if (fileUpload.getStyleClass() != null)
      {
         writer.writeAttribute(HTML.STYLE_CLASS_ATTR, fileUpload.getStyleClass(), null);
      }
      if (fileUpload.getStyle() != null)
      {
         writer.writeAttribute(HTML.STYLE_CLASS_ATTR, fileUpload.getStyle(), null);
      }
      writer.endElement(HTML.INPUT_ELEM);
   }
      
   
   
   @Override
   protected void doDecode(FacesContext context, UIComponent component)
   {
      UIFileUpload fileUpload = (UIFileUpload) component;
      ServletRequest request = (ServletRequest) context.getExternalContext().getRequest();
      
      if (!(request instanceof MultipartRequest))
      {
         request = unwrapMultipartRequest(request);
      }

      if (request instanceof MultipartRequest)
      {
         MultipartRequest req = (MultipartRequest) request;
         
         String clientId = component.getClientId(context);         
         String contentType = req.getFileContentType(clientId);
         String fileName = req.getFileName(clientId);
         int fileSize = req.getFileSize(clientId);
                  
         if (fileUpload.getData() != null)
         {
            
            Class cls = fileUpload.getData().getType(context);
            if (cls.isAssignableFrom(InputStream.class))
            {
               fileUpload.getData().setValue(context, req.getFileInputStream(clientId));
            }
            else if (cls.isAssignableFrom(byte[].class))
            {
               fileUpload.getData().setValue(context, req.getFileBytes(clientId));
            }
         }
         
         
         if (fileUpload.getContentType() != null)
         {
            fileUpload.getContentType().setValue(context, contentType);
         }
         if (fileUpload.getFileName() != null)
         {
            fileUpload.getFileName().setValue(context, fileName);
         }
         
         if (fileUpload.getFileSize() != null)
         {
            fileUpload.getFileSize().setValue(context, fileSize);
         }
      }      
   }
   
   private static ServletRequest unwrapMultipartRequest(ServletRequest request)
   {      
      while (!(request instanceof MultipartRequest))
      {
         boolean found = false;
         
         for (Method m : request.getClass().getMethods())
         {
            if (ServletRequest.class.isAssignableFrom(m.getReturnType()) && 
                m.getParameterTypes().length == 0)
            {
               try
               {
                  request = (ServletRequest) m.invoke(request);
                  found = true;
                  break;
               }
               catch (Exception ex) { /* Ignore, try the next one */ } 
            }
         }
         
         if (!found)
         {
            for (Field f : request.getClass().getDeclaredFields())
            {
               if (ServletRequest.class.isAssignableFrom(f.getType()))
               {
                  try
                  {
                     request = (ServletRequest) f.get(request);
                  }
                  catch (Exception ex) { /* Ignore */}
               }
            }
         }
         
         if (!found) break;
      }
      
      return request;     
   }

}
