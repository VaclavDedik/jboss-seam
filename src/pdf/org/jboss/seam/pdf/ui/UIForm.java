package org.jboss.seam.pdf.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;
import org.jboss.seam.document.DocumentData;
import org.jboss.seam.document.DocumentStore;
import org.jboss.seam.document.DocumentData.DocumentType;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.navigation.Pages;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

public class UIForm extends FormComponent
{
   public static final String COMPONENT_FAMILY = "org.jboss.seam.pdf.UIForm";

   private Log log = Logging.getLog(getClass());

   private String URL;

   PdfReader reader;
   PdfStamper stamper;
   AcroFields fields;
   ByteArrayOutputStream buffer;

   public String getURL()
   {
      return (String) valueOf("URL", URL);
   }

   public void setURL(String url)
   {
      URL = url;
   }

   @Override
   public void encodeBegin(FacesContext facesContext) throws IOException
   {
      reader = new PdfReader(new URL(getURL()));
      buffer = new ByteArrayOutputStream();
      try
      {
         stamper = new PdfStamper(reader, buffer);
      }
      catch (DocumentException e)
      {
         throw new FacesException("Could not create PDF stamper", e);
      }
      fields = stamper.getAcroFields();
      Contexts.getEventContext().set(FIELDS_KEY, fields);
   }

   @Override
   public void encodeEnd(FacesContext facesContext) throws IOException
   {
      stamper.setFormFlattening(true);
      try
      {
         stamper.close();
      }
      catch (DocumentException e)
      {
         throw new FacesException("Could not flush PDF", e);
      }

      String viewId = Pages.getViewId(facesContext);
      String baseName = baseNameForViewId(viewId);
      DocumentStore store = DocumentStore.instance();
      DocumentType documentType = new DocumentData.DocumentType("pdf", "application/pdf");
      DocumentData documentData = new DocumentData(baseName, documentType, buffer.toByteArray());
      String id = store.newId();
      String url = store.preferredUrlForContent(baseName, documentType.getExtension(), id);
      url = Manager.instance().encodeConversationId(url, viewId);
      store.saveData(id, documentData);
      log.info("Redirecting to #0", url);
      facesContext.getExternalContext().redirect(url);
   }

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

   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }

}
