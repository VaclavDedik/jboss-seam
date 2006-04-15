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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;

/**
 * Resolves a variable name in the Seam contexts, according to
 * the context search priority, instantiating the component
 * if necessary.
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class SeamVariableResolver extends VariableResolver
{

   private static final Log log = LogFactory.getLog(SeamVariableResolver.class);

   private VariableResolver jsfVariableResolver;
   
   public SeamVariableResolver(VariableResolver jsfVariableResolver)
   {
      this.jsfVariableResolver = jsfVariableResolver;
   }

   public Object resolveVariable(FacesContext facesContext, String name) throws EvaluationException
   {
      name = name.replace('$', '.');
      
      log.debug("resolving name: " + name);
      Object component = Component.getInstance(name, true);
      Object managedBean = jsfVariableResolver.resolveVariable(facesContext, name);
      if (component==null)
      {
         if (managedBean==null)
         {
            log.debug("could not resolve name");
            return null;
         }
         else {
            log.debug("resolved name to managed bean");
            return managedBean;  
         }
      }
      else
      {
         if ( managedBean!=null && managedBean!=component )
         {
            log.warn("Seam component hides managed bean with same name");
         }
         log.debug("resolved name to seam component");
         return component;
      }      
   }

}
