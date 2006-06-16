package org.jboss.seam.util;

import org.hibernate.validator.ClassValidator;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.core.ResourceBundle;

public class Validation
{

   public static ClassValidator getValidator(Class modelClass)
   {
      String componentName = Seam.getComponentName(modelClass);
      if (componentName==null)
      {
         java.util.ResourceBundle bundle = ResourceBundle.instance();
         return bundle==null ? 
               new ClassValidator(modelClass) : 
               new ClassValidator(modelClass, bundle);
      }
      else
      {
         return Component.forName(componentName).getValidator();
      }
   }

}
