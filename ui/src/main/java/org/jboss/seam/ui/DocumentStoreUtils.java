package org.jboss.seam.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.faces.context.FacesContext;

import org.jboss.seam.core.Manager;
import org.jboss.seam.document.DocumentData;
import org.jboss.seam.document.DocumentStore;
import org.jboss.seam.document.DocumentData.DocumentType;
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.ui.component.UIResource;

public class DocumentStoreUtils
{

   public static String addResourceToDataStore(FacesContext ctx, UIResource resource)
   {
      String baseName = baseNameForViewId(Pages.getCurrentViewId());
      String viewId = Pages.getViewId(ctx);

      DocumentStore store = DocumentStore.instance();
      String id = store.newId();

      DocumentType type = new DocumentType("", resource.getContentType());

      DocumentData documentData = new DownloadableDocumentData(baseName, type, resource.getData());
      documentData.setFilename(resource.getFileName());
      documentData.setDisposition(resource.getDisposition());

      String url = store.preferredUrlForContent(resource.getFileName(), type.getExtension(), id);
      url = Manager.instance().encodeConversationId(url, viewId);
      store.saveData(id, documentData);
      return url;
   }

   static class DownloadableDocumentData extends DocumentData
   {

      private Object data;

      public DownloadableDocumentData(String baseName, DocumentType type, Object data)
      {
         super(baseName, type);
         this.data = data;
      }

      @Override
      public void writeDataToStream(OutputStream os) throws IOException
      {
         if (data instanceof byte[])
         {
            os.write((byte[]) data);
         }
         else if (data instanceof File)
         {
            writeStream(os, new FileInputStream((File) data));
         }
         else if (data instanceof InputStream)
         {
            writeStream(os, (InputStream) data);
         }

      }

      private void writeStream(OutputStream os, InputStream is) throws IOException
      {
         while (is.available() > 0)
         {
            os.write(is.read());
         }
      }

   }

   /**
    * TODO: This part should be refactored in to Pages.java Used (at least)
    * here, in pdf module and excel
    * 
    */
   public static String baseNameForViewId(String viewId)
   {
      int pos = viewId.lastIndexOf("/");
      if (pos != -1)
      {
         viewId = viewId.substring(pos + 1);
      }

      pos = viewId.lastIndexOf(".");
      if (pos != -1)
      {
         viewId = viewId.substring(0, pos);
      }

      return viewId;
   }

}
