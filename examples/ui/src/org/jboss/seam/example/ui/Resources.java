package org.jboss.seam.example.ui;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;

@Name("resources")
@Scope(ScopeType.EVENT)
public class Resources
{

   private static Map<String, ResourceItem> resources = new HashMap<String, ResourceItem>();

   static
   {
      resources.put("1", new ResourceItem("text.txt", new byte[] { 'a', 'b', 'c' }, null, "text/plain"));
      ByteArrayInputStream str = new ByteArrayInputStream(new byte[] { '1', '2', '3' });
      resources.put("2", new ResourceItem("numbers.txt", str, null, "text/plain"));
   }

   @RequestParameter
   private String id;

   private ResourceItem item;

   @Create
   public void create()
   {
      item = resources.get(id);
   }

   public static class ResourceItem
   {

      public ResourceItem(String fileName, Object data, String disposition, String contentType)
      {
         this.fileName = fileName;
         this.data = data;
         this.disposition = disposition;
         this.contentType = contentType;
      }

      public String fileName;
      public Object data;
      public String disposition;
      public String contentType;

      public String getFileName()
      {
         return fileName;
      }

      public Object getData()
      {
         return data;
      }

      public String getDisposition()
      {
         return disposition;
      }

      public String getContentType()
      {
         return contentType;
      }

   }

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public ResourceItem getItem()
   {
      return item;
   }

   public void setItem(ResourceItem item)
   {
      this.item = item;
   }

   public ResourceItem getTextItem()
   {
      return resources.get("1");
   }

}
