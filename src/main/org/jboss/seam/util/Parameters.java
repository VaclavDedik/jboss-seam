package org.jboss.seam.util;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.servlet.ServletRequest;

import org.jboss.seam.contexts.Lifecycle;

public class Parameters
{

   private static Object convertRequestParameter(String requestParameter, Class type)
   {
      if ( String.class.equals(type) ) return requestParameter;
   
      FacesContext facesContext = FacesContext.getCurrentInstance();
      if (facesContext==null)
      {
         throw new IllegalStateException("No FacesContext associated with current thread, cannot convert request parameter type");
      }
      else
      {
         Converter converter = facesContext.getApplication().createConverter(type);
         if (converter==null)
         {
            throw new IllegalArgumentException("no converter for type: " + type);
         }
         UIViewRoot viewRoot = facesContext.getViewRoot();
         return converter.getAsObject( 
                  facesContext, 
                  viewRoot==null ? new UIViewRoot() : viewRoot, //have to pass something here, or get a totally useless NPE from JSF 
                  requestParameter );
      }
   }

   public static Map<String, String[]> getRequestParameters()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      if ( facesContext != null )
      {
         return facesContext.getExternalContext().getRequestParameterValuesMap();
      }
      
      ServletRequest servletRequest = Lifecycle.getServletRequest();
      if ( servletRequest != null )
      {
         return servletRequest.getParameterMap();
      }
      
      return Collections.EMPTY_MAP;
   }

   public static Object convertMultiValueRequestParameter(Map<String, String[]> requestParameters, String name, Class<?> type)
   {
      String[] array = requestParameters.get(name);
      if (array==null || array.length==0)
      {
         return null;
      }
      else
      {
         if ( type.isArray() )
         {
               int length = Array.getLength(array);
               Class<?> elementType = type.getComponentType();
               Object newInstance = Array.newInstance(elementType, length);
               for ( int i=0; i<length; i++ )
               {
                  Object element = convertRequestParameter( (String) Array.get(array, i), elementType );
                  Array.set( newInstance, i, element );
               }
               return newInstance;
         }
         else
         {
            return convertRequestParameter( array[0], type );
         }
      }
   }

}
