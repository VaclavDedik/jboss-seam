package org.jboss.seam.remoting.gwt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.remoting.gwt.GWTToSeamAdapter.ReturnedObject;
import org.jboss.seam.servlet.ContextualHttpServletRequest;
import org.jboss.seam.web.AbstractResource;

import com.google.gwt.user.client.rpc.SerializableException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamReader;
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamWriter;

/**
 * Abstract base class for GWT integration.
 * 
 * @author Shane Bryzak
 */
public abstract class GWTService extends AbstractResource
{
   protected static final LogProvider log = Logging.getLogProvider(GWTService.class);
   
   /*
    * These members are used to get and set the different HttpServletResponse
    * and HttpServletRequest headers.
    */
   private static final String ACCEPT_ENCODING = "Accept-Encoding";
   private static final String CHARSET_UTF8 = "UTF-8";
   private static final String CONTENT_ENCODING = "Content-Encoding";
   private static final String CONTENT_ENCODING_GZIP = "gzip";
   private static final String CONTENT_TYPE_TEXT_PLAIN_UTF8 = "text/plain; charset=utf-8";
   private static final String GENERIC_FAILURE_MSG = "The call failed on the server; see server log for details";
   private static final HashMap TYPE_NAMES;

   /**
    * Controls the compression threshold at and below which no compression will
    * take place.
    */
   private static final int UNCOMPRESSED_BYTE_SIZE_LIMIT = 256;

   static
   {
      TYPE_NAMES = new HashMap();
      TYPE_NAMES.put("Z", boolean.class);
      TYPE_NAMES.put("B", byte.class);
      TYPE_NAMES.put("C", char.class);
      TYPE_NAMES.put("D", double.class);
      TYPE_NAMES.put("F", float.class);
      TYPE_NAMES.put("I", int.class);
      TYPE_NAMES.put("J", long.class);
      TYPE_NAMES.put("S", short.class);
   }

   @Override
   public String getResourcePath()
   {
      return "/gwt";
   }
   
   protected abstract ServerSerializationStreamReader getStreamReader();
   protected abstract ServerSerializationStreamWriter getStreamWriter();
   protected abstract String createResponse(ServerSerializationStreamWriter stream,
         Class responseType, Object responseObj, boolean isException);

   /**
    * Return true if the response object accepts Gzip encoding. This is done by
    * checking that the accept-encoding header specifies gzip as a supported
    * encoding.
    */
   private static boolean acceptsGzipEncoding(HttpServletRequest request)
   {
      assert (request != null);

      String acceptEncoding = request.getHeader(ACCEPT_ENCODING);
      if (null == acceptEncoding)
      {
         return false;
      }

      return (acceptEncoding.indexOf(CONTENT_ENCODING_GZIP) != -1);
   }

   /**
    * This method attempts to estimate the number of bytes that a string will
    * consume when it is sent out as part of an HttpServletResponse. This really
    * a hack since we are assuming that every character will consume two bytes
    * upon transmission. This is definitely not true since some characters
    * actually consume more than two bytes and some consume less. This is even
    * less accurate if the string is converted to UTF8. However, it does save us
    * from converting every string that we plan on sending back to UTF8 just to
    * determine that we should not compress it.
    */
   private static int estimateByteSize(final String buffer)
   {
      return (buffer.length() * 2);
   }

   // private final Set knownImplementedInterfaces = new HashSet();
   private final ThreadLocal perThreadRequest = new ThreadLocal();

   private final ThreadLocal perThreadResponse = new ThreadLocal();

   /**
    * This is called internally.
    */
   @Override
   public final void getResource(final HttpServletRequest request,
         final HttpServletResponse response) throws ServletException,
         IOException
   {
      try
      {
         // Store the request & response objects in thread-local storage.
         perThreadRequest.set(request);
         perThreadResponse.set(response);

         new ContextualHttpServletRequest(request) {
            @Override
            public void process() throws Exception
            {

               Throwable caught;
               try
               {
                  // Read the request fully.
                  //
                  String requestPayload = readPayloadAsUtf8(request);

                  // Invoke the core dispatching logic, which returns the
                  // serialized
                  // result.
                  //
                  String responsePayload = processCall(requestPayload);

                  // Write the response.
                  //
                  writeResponse(request, response, responsePayload);
                  return;
               } catch (IOException e)
               {
                  caught = e;
               } catch (ServletException e)
               {
                  caught = e;
               } catch (SerializationException e)
               {
                  caught = e;
               } catch (Throwable e)
               {
                  caught = e;
               }

               respondWithFailure(response, caught);
            }

            @Override
            protected void restoreConversationId()
            {
               // no conversation support for gwt requests
            }

            @Override
            protected void handleConversationPropagation()
            {
            }
         }.run();
      } finally
      {
         perThreadRequest.remove();
         perThreadResponse.remove();
      }
   }

   /**
    * This is public so that it can be unit tested easily without HTTP.
    */
   public String processCall(String payload) throws SerializationException
   {

      // Let subclasses see the serialized request.
      //
      onBeforeRequestDeserialized(payload);

      // Create a stream to deserialize the request.
      //
      ServerSerializationStreamReader streamReader = getStreamReader();
      streamReader.prepareToRead(payload);

      // Read the service interface
      //
      String serviceIntfName = streamReader.readString();

      // Read the method name.
      //
      String methodName = streamReader.readString();

      // Read the number and names of the parameter classes from the stream.
      // We have to do this so that we can find the correct overload of the
      // method.
      //
      int paramCount = streamReader.readInt();
      Class[] paramTypes = new Class[paramCount];
      for (int i = 0; i < paramTypes.length; i++)
      {
         String paramClassName = streamReader.readString();
         try
         {
            paramTypes[i] = getClassOrPrimitiveFromName(paramClassName);
         } catch (ClassNotFoundException e)
         {
            throw new SerializationException("Unknown parameter " + i
                  + " type '" + paramClassName + "'", e);
         }
      }

      // Deserialize the parameters.
      //
      Object[] args = new Object[paramCount];
      for (int i = 0; i < args.length; i++)
      {
         args[i] = streamReader.deserializeValue(paramTypes[i]);
      }

      GWTToSeamAdapter adapter = GWTToSeamAdapter.instance();

      // Make the call via reflection.
      //
      String responsePayload = GENERIC_FAILURE_MSG;
      ServerSerializationStreamWriter streamWriter = getStreamWriter();
      Throwable caught = null;
      try
      {
         ReturnedObject returnedObject = adapter.callWebRemoteMethod(
               serviceIntfName, methodName, paramTypes, args);
         Class returnType = returnedObject.returnType;
         Object returnVal = returnedObject.returnedObject;
         // Class returnType = serviceIntfMethod.getReturnType();
         // Object returnVal = serviceIntfMethod.invoke(this, args);
         responsePayload = createResponse(streamWriter, returnType, returnVal,
               false);
      } catch (IllegalArgumentException e)
      {
         caught = e;
      } catch (IllegalAccessException e)
      {
         caught = e;
      } catch (InvocationTargetException e)
      {
         // Try to serialize the caught exception if the client is expecting it,
         // otherwise log the exception server-side.
         caught = e;
         Throwable cause = e.getCause();
         if (cause != null)
         {
            // Update the caught exception to the underlying cause
            caught = cause;
            // Serialize the exception back to the client if it's a declared
            // exception
            if (cause instanceof SerializableException)
            {
               Class thrownClass = cause.getClass();
               responsePayload = createResponse(streamWriter, thrownClass,
                     cause, true);
               // Don't log the exception on the server
               caught = null;
            }
         }
      }

      if (caught != null)
      {
         responsePayload = GENERIC_FAILURE_MSG;
         ServletContext servletContext = getServletContext();
         // servletContext may be null (for example, when unit testing)
         if (servletContext != null)
         {
            // Log the exception server side
            servletContext.log("Exception while dispatching incoming RPC call",
                  caught);
         }
      }

      // Let subclasses see the serialized response.
      //
      onAfterResponseSerialized(responsePayload);

      return responsePayload;
   }

   /**
    * Gets the <code>HttpServletRequest</code> object for the current call. It
    * is stored thread-locally so that simultaneous invocations can have
    * different request objects.
    */
   protected final HttpServletRequest getThreadLocalRequest()
   {
      return (HttpServletRequest) perThreadRequest.get();
   }

   /**
    * Gets the <code>HttpServletResponse</code> object for the current call.
    * It is stored thread-locally so that simultaneous invocations can have
    * different response objects.
    */
   protected final HttpServletResponse getThreadLocalResponse()
   {
      return (HttpServletResponse) perThreadResponse.get();
   }

   /**
    * Override this method to examine the serialized response that will be
    * returned to the client. The default implementation does nothing and need
    * not be called by subclasses.
    */
   protected void onAfterResponseSerialized(String serializedResponse)
   {
   }

   /**
    * Override this method to examine the serialized version of the request
    * payload before it is deserialized into objects. The default implementation
    * does nothing and need not be called by subclasses.
    */
   protected void onBeforeRequestDeserialized(String serializedRequest)
   {
   }

   /**
    * Determines whether the response to a given servlet request should or
    * should not be GZIP compressed. This method is only called in cases where
    * the requestor accepts GZIP encoding.
    * <p>
    * This implementation currently returns <code>true</code> if the response
    * string's estimated byte length is longer than 256 bytes. Subclasses can
    * override this logic.
    * </p>
    * 
    * @param request
    *           the request being served
    * @param response
    *           the response that will be written into
    * @param responsePayload
    *           the payload that is about to be sent to the client
    * @return <code>true</code> if responsePayload should be GZIP compressed,
    *         otherwise <code>false</code>.
    */
   protected boolean shouldCompressResponse(HttpServletRequest request,
         HttpServletResponse response, String responsePayload)
   {
      return estimateByteSize(responsePayload) > UNCOMPRESSED_BYTE_SIZE_LIMIT;
   }

   /**
    * Returns the {@link Class} instance for the named class.
    * 
    * @param name
    *           the name of a class or primitive type
    * @return Class instance for the given type name
    * @throws ClassNotFoundException
    *            if the named type was not found
    */
   private Class getClassFromName(String name) throws ClassNotFoundException
   {
      return Class.forName(name, false, this.getClass().getClassLoader());
   }

   /**
    * Returns the {@link Class} instance for the named class or primitive type.
    * 
    * @param name
    *           the name of a class or primitive type
    * @return Class instance for the given type name
    * @throws ClassNotFoundException
    *            if the named type was not found
    */
   private Class getClassOrPrimitiveFromName(String name)
         throws ClassNotFoundException
   {
      Object value = TYPE_NAMES.get(name);
      if (value != null)
      {
         return (Class) value;
      }

      return getClassFromName(name);
   }

   /**
    * Obtain the special package-prefixes we use to check for custom serializers
    * that would like to live in a package that they cannot. For example,
    * "java.util.ArrayList" is in a sealed package, so instead we use this
    * prefix to check for a custom serializer in
    * "com.google.gwt.user.client.rpc.core.java.util.ArrayList". Right now, it's
    * hard-coded because we don't have a pressing need for this mechanism to be
    * extensible, but it is imaginable, which is why it's implemented this way.
    */
   protected String[] getPackagePaths()
   {
      return new String[] { "com.google.gwt.user.client.rpc.core" };
   }

   private String readPayloadAsUtf8(HttpServletRequest request)
         throws IOException, ServletException
   {
      int contentLength = request.getContentLength();
      if (contentLength == -1)
      {
         // Content length must be known.
         throw new ServletException("Content-Length must be specified");
      }

      String contentType = request.getContentType();
      boolean contentTypeIsOkay = false;
      // Content-Type must be specified.
      if (contentType != null)
      {
         // The type must be plain text or text/x-gwt-rpc
         if (contentType.startsWith("text/plain") || contentType.startsWith("text/x-gwt-rpc"))
         {
            // And it must be UTF-8 encoded (or unspecified, in which case we
            // assume that it's either UTF-8 or ASCII).
            if (contentType.indexOf("charset=") == -1)
            {
               contentTypeIsOkay = true;
            } else if (contentType.indexOf("charset=utf-8") != -1)
            {
               contentTypeIsOkay = true;
            }
         }
      }
      if (!contentTypeIsOkay)
      {
         throw new ServletException(
               "Content-Type must be 'text/plain' or 'text/x-gwt-rpc' with 'charset=utf-8' (or unspecified charset)");
      }
      InputStream in = request.getInputStream();
      try
      {
         byte[] payload = new byte[contentLength];
         int offset = 0;
         int len = contentLength;
         int byteCount;
         while (offset < contentLength)
         {
            byteCount = in.read(payload, offset, len);
            if (byteCount == -1)
            {
               throw new ServletException("Client did not send "
                     + contentLength + " bytes as expected");
            }
            offset += byteCount;
            len -= byteCount;
         }
         return new String(payload, "UTF-8");
      } finally
      {
         if (in != null)
         {
            in.close();
         }
      }
   }

   /**
    * Called when the machinery of this class itself has a problem, rather than
    * the invoked third-party method. It writes a simple 500 message back to the
    * client.
    */
   private void respondWithFailure(HttpServletResponse response,
         Throwable caught)
   {
      ServletContext servletContext = getServletContext();
      servletContext.log("Exception while dispatching incoming RPC call",
            caught);
      try
      {
         response.setContentType("text/plain");
         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         response.getWriter().write(GENERIC_FAILURE_MSG);
      } catch (IOException e)
      {
         servletContext
               .log(
                     "sendError() failed while sending the previous failure to the client",
                     caught);
      }
   }

   private void writeResponse(HttpServletRequest request,
         HttpServletResponse response, String responsePayload)
         throws IOException
   {

      byte[] reply = responsePayload.getBytes(CHARSET_UTF8);
      String contentType = CONTENT_TYPE_TEXT_PLAIN_UTF8;

      if (acceptsGzipEncoding(request)
            && shouldCompressResponse(request, response, responsePayload))
      {
         // Compress the reply and adjust headers.
         //
         ByteArrayOutputStream output = null;
         GZIPOutputStream gzipOutputStream = null;
         Throwable caught = null;
         try
         {
            output = new ByteArrayOutputStream(reply.length);
            gzipOutputStream = new GZIPOutputStream(output);
            gzipOutputStream.write(reply);
            gzipOutputStream.finish();
            gzipOutputStream.flush();
            response.setHeader(CONTENT_ENCODING, CONTENT_ENCODING_GZIP);
            reply = output.toByteArray();
         } catch (UnsupportedEncodingException e)
         {
            caught = e;
         } catch (IOException e)
         {
            caught = e;
         } finally
         {
            if (null != gzipOutputStream)
            {
               gzipOutputStream.close();
            }
            if (null != output)
            {
               output.close();
            }
         }

         if (caught != null)
         {
            getServletContext().log("Unable to compress response", caught);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
         }
      }

      // Send the reply.
      //
      response.setContentLength(reply.length);
      response.setContentType(contentType);
      response.setStatus(HttpServletResponse.SC_OK);
      response.getOutputStream().write(reply);
   }

}
