//$Id$
package org.jboss.seam.finders;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.jboss.seam.annotations.In;
import org.jboss.seam.contexts.BusinessProcessContext;
import org.jboss.seam.contexts.Contexts;

/**
 * Finds jBPM business process instances
 * 
 * @author Gavin King
 */
public class ProcessInstanceFinder implements Finder
{

   public String toName(In in, Method method)
   {
      String name = in.value();
      if (name==null || name.length() == 0)
      {
         name = method.getName().substring(3, 4).toLowerCase()
               + method.getName().substring(4);
      }
      return name;
   }

   public String toName(In in, Field field)
   {
      String name = in.value();
      if (name==null || name.length() == 0)
      {
         name = field.getName();
      }
      return name;
   }

   public Object find(In in, String name, Object bean)
   {
      // TODO : we *could* allow this to create a new ProcessInstance here
      // by assuming that the incoming name is the definition name.
      // However, not sure that is a good idea...
      if ( !Contexts.isBusinessProcessContextActive() )
      {
         throw new IllegalStateException( "No currently active business process context" );
      }

      return ( ( BusinessProcessContext ) Contexts.getBusinessProcessContext() ).getProcessInstance();
   }

}
