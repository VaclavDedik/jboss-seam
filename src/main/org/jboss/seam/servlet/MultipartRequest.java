package org.jboss.seam.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Request wrapper for supporting multipart requests, used for file uploading.
 * 
 * @author Shane Bryzak
 */
public class MultipartRequest extends HttpServletRequestWrapper
{
   public static final String WWW_FORM_URLENCODED_TYPE = "application/x-www-form-urlencoded";
   
   private class PartWrapper 
   {
      private Map<String,Object> params;
      private byte[] data;
      private String contentType;
      private String fileName;
      
      public PartWrapper()
      {
         params = new HashMap<String,Object>();
      }
      
      public Map<String,Object> getParams()
      {
         return params;
      }
      
      public void setData(byte[] data)
      {
         this.data = data;
      }
      
      public byte[] getData()
      {
         return data;
      }
      
      public String getContentType()
      {
         return contentType;
      }
      
      public void setContentType(String contentType)
      {
         this.contentType = contentType;
      }
      
      public String getFileName()
      {
         return fileName;
      }
      
      public void setFileName(String fileName)
      {
         this.fileName = fileName;
      }
   }

   private HttpServletRequest request;

   private Map<String,PartWrapper> parameters;

   public MultipartRequest(HttpServletRequest request)
   {
      super(request);
      this.request = request;
   }

   private void parseRequest()
   {     
      parameters = new HashMap<String,PartWrapper>();        
      
      boundaryMarker = getBoundary(request.getContentType());
      if (boundaryMarker == null)
      {
         throw new RuntimeException("the request was rejected because "
                  + "no multipart boundary was found");
      }
      
//      String charEncoding = request.getCharacterEncoding();    
      
      try
      {
         buffer = new byte[4096];
         bufferEnd = 0;
         input = request.getInputStream();
                  
         pos = 0;
         
         fillBuffer();
         
         byte[] boundary = readNextBoundary();

         while (boundary != null || fillBuffer())
         {
            if (boundary != null)
               parseBoundary(boundary);
            boundary = readNextBoundary();            
         }
      }
      catch (IOException ex)
      {
         
      }
   }
   
   private static final String FILE_CONTENT_TYPE = "Content-Type";
   private static final String FILE_NAME = "filename";
   
   private void parseBoundary(byte[] boundary)
   {
      PartWrapper entry = new PartWrapper();
      
      int start = 0;
      
      for (int i = 0; i < boundary.length; i++)
      {   
         if (checkSequence(boundary, i, CR_LF))
         {
            if (start < i - CR_LF.length)            
            {
               String line = new String(boundary, start, i - CR_LF.length - start);            
               parseParams(line, ";", entry.getParams());
               start = i;
            }
            
            if (checkSequence(boundary, i + CR_LF.length, CR_LF))
            {
               start = i + CR_LF.length;
               break;
            }            
         }         
      }
      
      // Extract the content type and filename
      for (String key : entry.getParams().keySet())
      {
         Object val = entry.getParams().get(key);
         if (val instanceof String)
         {
            String s = (String) entry.getParams().get(key);
            if (s != null)
            {        
               if (entry.getContentType() == null && FILE_CONTENT_TYPE.equalsIgnoreCase(key))
                  entry.setContentType(s);
               else if (entry.getFileName() == null && FILE_NAME.equalsIgnoreCase(key))
                  entry.setFileName(s);
            }
         }
      }
      
      byte[] data = new byte[boundary.length - start];
      System.arraycopy(boundary, start, data, 0, data.length);
      entry.setData(data);
      
      if (entry.getParams().containsKey("name"))
         parameters.put((String) entry.getParams().get("name"), entry);
   }
   
   /**
    * The boundary marker bytes.  Each part is separated by a boundary marker
    */
   private byte[] boundaryMarker; 
   
   /**
    * 
    */
   private InputStream input;
   
   /**
    * Read buffer
    */
   private byte[] buffer;
   
   /**
    * The read position in the buffer
    */
   private int pos;   
   
   /**
    * Total bytes read
    */
   private int totalRead;
   
   /**
    * The last written byte position in the buffer
    */
   private int bufferEnd;
   
   /**
    * Reads more data into the buffer if possible, shuffling the buffer
    * contents if required
    *
    */
   private boolean fillBuffer()
      throws IOException
   {
      if (totalRead >= request.getContentLength())
         return false;
      
      // If pos > 0, move the contents to the front of the buffer to make space
      if (pos > 0)
      {
         System.arraycopy(buffer, pos, buffer, 0, bufferEnd - pos);
         bufferEnd -= pos;
         pos = 0;
      }
      
      // If the buffer is full, extend it
      if (pos == 0 && bufferEnd >= buffer.length - 1)
      {
         byte[] newBuffer = new byte[buffer.length * 2];
         System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
         buffer = newBuffer;         
      }
      
      int read = input.read(buffer, bufferEnd, buffer.length - bufferEnd);
      
      if (read != -1)
      {
         bufferEnd += read;
         totalRead += read;
      }
      
      return read != -1;
   }
   
   private static final byte CR = 0x0d;
   private static final byte LF = 0x0a;   
   private static final byte[] CR_LF = {CR,LF};
      
   private byte[] readNextBoundary()
      throws IOException
   {

      int boundaryStart = -1;
      
      for (int i = pos; i < bufferEnd; i++)
      {         
         if (boundaryStart == -1 && checkSequence(buffer, i, boundaryMarker) &&
                  checkSequence(buffer, i + CR_LF.length, CR_LF))
         {
            // First boundary marker
            boundaryStart = i + CR_LF.length;
         }
         else if (boundaryStart != -1 && checkSequence(buffer, i, boundaryMarker)) 
         {
            // Second boundary marker
            byte[] boundary = new byte[i - boundaryMarker.length - boundaryStart - 4];
            System.arraycopy(buffer, boundaryStart, boundary, 0, boundary.length);
            pos = i - boundaryMarker.length;
            return boundary;
         }            
      }
      
      return null;
   }
   
   /**
    * Checks if a specified sequence of bytes ends at a specific position
    * within a byte array.
    * 
    * @param data
    * @param pos
    * @param seq
    * @return boolean indicating if the sequence was found at the specified position
    */
   private boolean checkSequence(byte[] data, int pos, byte[] seq)
   {
      if (pos - seq.length < 0 || pos > data.length)
         return false;
      
      for (int i = 0; i < seq.length; i++)
      {
         if (data[pos - seq.length + i] != seq[i])
            return false;
      }
      
      return true;
   }
   
   private byte[] getBoundary(String contentType)
   {
      Map<String, Object> params = parseParams(contentType, ";");
      String boundaryStr = (String) params.get("boundary");

      if (boundaryStr == null) return null;

      try
      {
         return boundaryStr.getBytes("ISO-8859-1");
      }
      catch (UnsupportedEncodingException e)
      {
         return boundaryStr.getBytes();
      }
   }

   private static final Pattern PARAM_VALUE_PATTERN = Pattern
            .compile("^\\s*([^\\s=]+)\\s*[=:]\\s*([^\\s]+)\\s*$");

   private void parseParams(String paramStr, String separator, Map<String,Object> target)
   {
      String[] parts = paramStr.split("[" + separator + "]");

      for (String part : parts)
      {
         Matcher m = PARAM_VALUE_PATTERN.matcher(part);
         if (m.matches())
         {
            String key = m.group(1);
            String value = m.group(2);
            
            // Strip double quotes
            if (value.startsWith("\"") && value.endsWith("\""))
               value = value.substring(1, value.length() - 1);
            
            if (target.containsKey(key))
            {
               Object v = target.get(key);
               if (v instanceof List)
                  ((List) v).add(value);
               else if (v instanceof String)
               {
                  List<String> vals = new ArrayList<String>();
                  vals.add((String) v);
                  vals.add(value);
                  target.put(key, value);
               }
               else
               {
                  List vals = new ArrayList();
                  vals.add(v);
                  vals.add(value);
                  target.put(key, value);
               }
            }
            else
               target.put(key, value);
         }
      }      
   }
   
   private Map<String, Object> parseParams(String paramStr, String separator)
   {
      Map<String, Object> target = new HashMap<String, Object>();
      parseParams(paramStr, separator, target);
      return target;      
   }

   @Override
   public Enumeration getParameterNames()
   {
      if (parameters == null) 
         parseRequest();

      return Collections.enumeration(parameters.keySet());
   }
   
   public byte[] getFileBytes(String name)
   {
      if (parameters == null)
         parseRequest();
      
      PartWrapper wrapper = parameters.get(name);
      return wrapper != null ? wrapper.getData() : null;
   }
   
   public String getFileContentType(String name)
   {
      if (parameters == null)
         parseRequest();
      
      PartWrapper wrapper = parameters.get(name);      
      return wrapper != null ? wrapper.getContentType() : null;
   }
   
   public String getFileName(String name)
   {
      if (parameters == null)
         parseRequest();
      
      PartWrapper wrapper = parameters.get(name);
      return wrapper != null ? wrapper.getFileName() : null;
   }   
   
   @Override
   public String getParameter(String name)
   {
      if (parameters == null) 
         parseRequest();

      PartWrapper wrapper = parameters.get(name);
      return wrapper != null ? new String(wrapper.getData()) : super.getParameter(name);

//      String[] values = (String[]) parameters.get(name);
//
//      if (values == null) 
//         return null;
//
//      return values[0];
   }

   @Override
   public String[] getParameterValues(String name)
   {
      if (parameters == null) 
         parseRequest();

//      return (String[]) parameters.get(name);
      
      PartWrapper wrapper = parameters.get(name);
      return wrapper != null ? new String[] { new String(parameters.get(name).getData()) } : 
         super.getParameterValues(name);
   }

   @Override
   public Map getParameterMap()
   {
      if (parameters == null)
         parseRequest();

      Map<String,Object> params = new HashMap<String,Object>();
      
      for (String name : parameters.keySet())
      {
         PartWrapper w = parameters.get(name);         
         params.put(name, new String(w.getData()));
      }
      
      return params;
   }

   @Override
   public Object getAttribute(String string)
   {
      return super.getAttribute(string);
   }

   @Override
   public String getContentType()
   {
      return WWW_FORM_URLENCODED_TYPE;
   }
}
