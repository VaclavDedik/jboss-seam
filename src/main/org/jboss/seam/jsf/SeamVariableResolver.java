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
import org.jboss.seam.Finder;

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

   private Finder seamVariableResolver;
   private VariableResolver jsfVariableResolver;
   
   public SeamVariableResolver(VariableResolver jsfVariableResolver)
   {
      seamVariableResolver = new Finder();
      this.jsfVariableResolver = jsfVariableResolver;
   }

   @Override
   public Object resolveVariable(FacesContext facesContext, String name) throws EvaluationException
   {
      log.info("resolving name as a Seam component: " + name);
      Object component = seamVariableResolver.getComponentInstance(name, true);
      if (component==null)
      {
         log.info("resolving name as a managed bean: " + name);
         //delegate back to JSF to see if its a managed bean
         component = jsfVariableResolver.resolveVariable(facesContext, name);
      }
      if (component==null) 
      {
         log.info("not found: " + name);
      }
      else 
      {
         log.info("found: " + name);
      }  
      return component;
   }

}
