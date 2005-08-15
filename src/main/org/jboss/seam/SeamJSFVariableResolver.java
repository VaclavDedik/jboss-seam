/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;


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

   static public final Logger log = Logger.getLogger(SeamJSFVariableResolver.class);

   private SeamVariableResolver seamVariableResolver;
   private VariableResolver jsfVariableResolver;
   
   public SeamJSFVariableResolver(VariableResolver jsfVariableResolver)
   {
      seamVariableResolver = new SeamVariableResolver();
      this.jsfVariableResolver = jsfVariableResolver;
   }

   @Override
   public Object resolveVariable(FacesContext facesContext, String name) throws EvaluationException
   {
      log.info("resolving name as a Seam component: " + name);
      Object component = seamVariableResolver.resolveVariable(name, true);
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
