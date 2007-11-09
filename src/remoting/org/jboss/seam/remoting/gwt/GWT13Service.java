package org.jboss.seam.remoting.gwt;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.lang.reflect.Constructor;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamReader;
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamWriter;

/**
 * 
 * @author Shane Bryzak
 */
@Scope(APPLICATION)
@Name("org.jboss.seam.remoting.gwt.gwtRemoteService")
@Install(value = false, precedence = BUILT_IN, classDependencies = {"com.google.gwt.user.client.rpc.RemoteService"})
@BypassInterceptors
@Deprecated
public class GWT13Service extends GWTService
{
   private static final String SERIALIZABLE_TYPE_CLASS = "com.google.gwt.user.server.rpc.impl.ServerSerializableTypeOracleImpl";
     
   private Object serializableTypeOracle;
   
   private Constructor streamReaderConstructor;
   private Constructor streamWriterConstructor;
   
   @Create
   public void startup() throws Exception
   {
      try
      {      
         log.trace("GWT13Service starting up");
         
         Class serializableType = Class.forName(SERIALIZABLE_TYPE_CLASS);
         String[] packagePaths = getPackagePaths();
         Constructor typeConstructor = serializableType.getConstructor(new Class[] { packagePaths.getClass() });
         
         serializableTypeOracle = typeConstructor.newInstance((Object[]) packagePaths);
         
         streamReaderConstructor = ServerSerializationStreamReader.class.getConstructor(
               new Class[] { serializableType } );
         streamWriterConstructor = ServerSerializationStreamWriter.class.getConstructor(
               new Class[] { serializableType } );
      }
      catch (Exception ex)
      {
         log.error("Error initializing GWT13Service.  Please ensure " +
               "the GWT 1.3 libraries are in the classpath.");
         throw ex;
      }         
   }

   @Override
   protected String createResponse(ServerSerializationStreamWriter stream,
         Class responseType, Object responseObj, boolean isException)
   {
      stream.prepareToWrite();
      if (responseType != void.class)
      {
         try
         {
            stream.serializeValue(responseObj, responseType);
         } catch (SerializationException e)
         {
            responseObj = e;
            isException = true;
         }
      }

      return (isException ? "{EX}" : "{OK}") + stream.toString();     
   }   
   
   @Override
   public ServerSerializationStreamReader getStreamReader()
   {
      try
      {
         return (ServerSerializationStreamReader) streamReaderConstructor.newInstance(serializableTypeOracle);
      }
      catch (Exception ex) 
      { 
         throw new RuntimeException("Unable to create stream reader", ex);
      }
   }
   
   @Override
   public ServerSerializationStreamWriter getStreamWriter()
   {
      try
      {
         return (ServerSerializationStreamWriter) streamWriterConstructor.newInstance(serializableTypeOracle);
      }
      catch (Exception ex)
      {
         throw new RuntimeException("Unable to create stream writer", ex);
      }
   }
}
