package org.jboss.seam.excel;

import java.io.Serializable;

public class DocumentData implements Serializable
{
   private static final long serialVersionUID = -7473346073592279335L;
   byte[] data;
   DocumentType documentType;
   String baseName;

   String disposition = "inline";

   public DocumentData(String baseName, DocumentType documentType, byte[] data)
   {
      super();
      this.data = data;
      this.documentType = documentType;
      this.baseName = baseName;
   }

   public byte[] getData()
   {
      return data;
   }

   public DocumentType getDocumentType()
   {
      return documentType;
   }

   public String getBaseName()
   {
      return baseName;
   }

   public String getFileName()
   {
      return getBaseName() + "." + getDocumentType().getExtension();
   }

   public void setDisposition(String disposition)
   {
      this.disposition = disposition;
   }

   public String getDisposition()
   {
      return disposition;
   }

   static public class DocumentType implements Serializable
   {
      private static final long serialVersionUID = 1L;

      private String mimeType;
      private String extension;

      public DocumentType(String extension, String mimeType)
      {
         this.extension = extension;
         this.mimeType = mimeType;
      }

      public String getMimeType()
      {
         return mimeType;
      }

      public String getExtension()
      {
         return extension;
      }

   }
}