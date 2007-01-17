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

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Init;

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

   private static final LogProvider log = Logging.getLogProvider(SeamVariableResolver.class);

   private VariableResolver jsfVariableResolver;
   
   public SeamVariableResolver(VariableResolver jsfVariableResolver)
   {
      this.jsfVariableResolver = jsfVariableResolver;
   }

   @Override
   public Object resolveVariable(FacesContext facesContext, String name) throws EvaluationException
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         //if no Seam contexts, bypass straight through to JSF
         return jsfVariableResolver.resolveVariable(facesContext, name);
      }
      
      name = name.replace('$', '.');
      
      log.debug("resolving name: " + name);
      Object seamBean = Component.getInstance(name, true);
      if (seamBean==null)
      {
         Component component = Component.forName(name);
         if ( component==null )
         {
            Object managedBean = jsfVariableResolver.resolveVariable(facesContext, name);
            if (managedBean==null)
            {
               log.debug("could not resolve name");
               return Init.instance().getRootNamespace().getChild(name);
            }
            else 
            {
               log.debug("resolved name to JSF managed bean");
               return managedBean;  
            }
         }
         else
         {
            log.debug("Seam component resolved, but unwrap method returned null");
            return null;
         }
      }
      else
      {
         log.debug("resolved name to Seam component");
         return seamBean;
      }      
   }

}
