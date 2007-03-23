package org.jboss.seam.ui.renderkit;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.servlet.ServletRequest;

import org.ajax4jsf.framework.renderer.AjaxComponentRendererBase;
import org.jboss.seam.ui.component.UIFileUpload;
import org.jboss.seam.web.MultipartRequest;

public class FileUploadRendererBase extends AjaxComponentRendererBase
{

   @Override
   protected Class getComponentClass()
   {
      return UIFileUpload.class;
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
         ValueBinding dataValueBinding = fileUpload.getValueBinding("data");  
         if (dataValueBinding != null)
         {
            
            Class cls = dataValueBinding.getType(context);
            if (cls.isAssignableFrom(InputStream.class))
            {
               dataValueBinding.setValue(context, req.getFileInputStream(clientId));
            }
            else if (cls.isAssignableFrom(byte[].class))
            {
               dataValueBinding.setValue(context, req.getFileBytes(clientId));
            }
         }
         
         ValueBinding contentTypeValueBinding = fileUpload.getValueBinding("contentType");
         if (contentTypeValueBinding != null)
         {
            contentTypeValueBinding.setValue(context, contentType);
         }
         
         ValueBinding fileNameValueBinding = fileUpload.getValueBinding("fileName");
         if (fileNameValueBinding != null)
         {
            fileNameValueBinding.setValue(context, fileName);
         }
         
         ValueBinding fileSizeValueBinding = fileUpload.getValueBinding("fileSize");
         if (fileSizeValueBinding != null)
         {
            fileSizeValueBinding.setValue(context, fileSize);
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
