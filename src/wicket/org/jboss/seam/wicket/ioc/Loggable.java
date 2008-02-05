package org.jboss.seam.wicket.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.util.Reflections;

/**
 * Controls logging for a MetaModel
 *
 */
public class Loggable
{
   
   private List<Field> logFields = new ArrayList<Field>();
   private List<org.jboss.seam.log.Log> logInstances = new ArrayList<org.jboss.seam.log.Log>();
   
   private MetaModel metaModel;

   public Loggable(MetaModel metaModel)
   {
      this.metaModel = metaModel;
   }
   
   public void add(Field field)
   {
      if ( field.isAnnotationPresent(org.jboss.seam.annotations.Logger.class) )
      {
         String category = field.getAnnotation(org.jboss.seam.annotations.Logger.class).value();
         org.jboss.seam.log.Log logInstance;
         if ( "".equals( category ) )
         {
            logInstance = org.jboss.seam.log.Logging.getLog(metaModel.getBeanClass());
         }
         else
         {
            logInstance = org.jboss.seam.log.Logging.getLog(category);
         }
         if ( Modifier.isStatic( field.getModifiers() ) )
         {
            Reflections.setAndWrap(field, null, logInstance);
         }
         else
         {
            logFields.add(field);
            logInstances.add(logInstance);
         }
      }
   }
   
   public void inject(Object instance) throws Exception
   {
      for (int i=0; i<logFields.size(); i++)
      {
         metaModel.setFieldValue( instance, logFields.get(i), "log", logInstances.get(i) );
      }
   }
   
}
