/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.VariableResolver;

import org.jboss.logging.Logger;

/**
 * Variable resolving: first the method tries to return an object
 * stored in the hierarchical context. If the object does not exist,
 * it is instanciated, stored in the correct context then returned.
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class SeamJSFVariableResolver extends VariableResolver
{

   private static final Logger log = Logger.getLogger(SeamJSFVariableResolver.class);

   private SeamVariableResolver seamVariableResolver;
   
   public SeamJSFVariableResolver()
   {
      seamVariableResolver = new SeamVariableResolver();
   }

   @Override
   public Object resolveVariable(FacesContext facesContext, String name) throws EvaluationException
   {
      log.info("resolving name: " + name);
      Object component = seamVariableResolver.resolveVariable(name, true);
      if (component==null) 
      {
         log.info("not found: " + name);
      }
      else 
      {
         log.info("found: " + name);
         print(component);
      }  
      return component;
   }

   private void print(Object component)
   {
      try {
         PropertyDescriptor[] props = Introspector.getBeanInfo( component.getClass() )
               .getPropertyDescriptors();
         for (PropertyDescriptor descriptor : props)
         {
            log.info( descriptor.getName() + " = " + descriptor.getReadMethod().invoke(component) );
         }
      }
      catch (Exception e) {}
   }

}
