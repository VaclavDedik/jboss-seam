package org.jboss.seam.core;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;

/**
 * Allows the application to determine whether the JSF validation
 * phase completed successfully, or if a validation failure
 * occurred.
 * 
 * @author Gavin king
 *
 */
@Name("org.jboss.seam.core.validation")
@Intercept(InterceptionType.NEVER)
@Install(precedence=BUILT_IN)
public class Validation
{

   private boolean succeeded;
   private boolean failed;

   public static Validation instance()
   {
      if ( !Contexts.isEventContextActive() )
      {
         throw new IllegalStateException("No active event scope");
      }
      return (Validation) Component.getInstance(Validation.class, ScopeType.EVENT);
   }
   
   public void afterProcessValidations(FacesContext facesContext)
   {
      failed = facesContext.getRenderResponse();
      if (failed)
      {
         Events.instance().raiseEvent("org.jboss.seam.validationFailed");
      }
      succeeded = !failed;
   }

   public boolean isSucceeded()
   {
      return succeeded;
   }

   public boolean isFailed()
   {
      return failed;
   }

   public void fail()
   {
      failed = true;
      succeeded = false;
   }

}
