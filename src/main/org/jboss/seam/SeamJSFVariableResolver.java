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
      return seamVariableResolver.resolveVariable(name, true);
   }

}
