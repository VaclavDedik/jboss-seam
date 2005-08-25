/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.jsf;


import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.VariableResolver;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;

/**
 * Variable resolving: first the method tries to return an object
 * stored in the hierarchical context. If the object does not exist,
 * it is instanciated, stored in the correct context then returned.
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class SeamVariableResolver extends VariableResolver
{

   private static final Logger log = Logger.getLogger(SeamVariableResolver.class);

   private VariableResolver jsfVariableResolver;
   
   public SeamVariableResolver(VariableResolver jsfVariableResolver)
   {
      this.jsfVariableResolver = jsfVariableResolver;
   }

   public Object resolveVariable(FacesContext facesContext, String name) throws EvaluationException
   {
      name = name.replace('$', '.');
      
      log.info("resolving name: " + name);
      Object component = Component.getInstance(name, true);
      Object managedBean = jsfVariableResolver.resolveVariable(facesContext, name);
      if (component==null)
      {
         if (managedBean==null)
         {
            log.info("could not resolve name");
            return null;
         }
         else {
            log.info("resolved name to managed bean");
            return managedBean;  
         }
      }
      else
      {
         if ( managedBean!=null && managedBean!=component )
         {
            log.warn("Seam component hides managed bean with same name");
         }
         log.info("resolved name to seam component");
         return component;
      }      
   }

}
