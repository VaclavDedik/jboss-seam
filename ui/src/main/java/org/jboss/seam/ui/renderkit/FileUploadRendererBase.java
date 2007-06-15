package org.jboss.seam.ui.renderkit;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
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
         MultipartRequest multipartRequest = (MultipartRequest) request;

         String clientId = component.getClientId(context);

         fileUpload.setLocalContentType(multipartRequest.getFileContentType(clientId));
         fileUpload.setLocalFileName(multipartRequest.getFileName(clientId));
         fileUpload.setLocalFileSize(multipartRequest.getFileSize(clientId));
      }
   }

   /**
    * Finds an instance of MultipartRequest wrapped within a request or its
    * (recursively) wrapped requests.
    */
   private static ServletRequest unwrapMultipartRequest(ServletRequest request)
   {
      while (!(request instanceof MultipartRequest))
      {
         boolean found = false;

         for (Method m : request.getClass().getMethods())
         {
            if (ServletRequest.class.isAssignableFrom(m.getReturnType())
                     && m.getParameterTypes().length == 0)
            {
               try
               {
                  request = (ServletRequest) m.invoke(request);
                  found = true;
                  break;
               }
               catch (Exception ex)
               { /* Ignore, try the next one */
               }
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
                  catch (Exception ex)
                  { /* Ignore */
                  }
               }
            }
         }

         if (!found) break;
      }

      return request;
   }

}
