//$Id$
package org.jboss.seam.faces;

import static org.jboss.seam.annotations.Install.FRAMEWORK;
import static org.jboss.seam.el.EL.EL_CONTEXT;

import javax.el.ELContext;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.hibernate.validator.InvalidValue;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Expressions;

/**
 * Factory for method and value bindings in a JSF environment.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Install(precedence=FRAMEWORK, classDependencies="javax.faces.context.FacesContext")
@Name("org.jboss.seam.core.expressions")
public class FacesExpressions extends Expressions
{
   
   /**
    * Get an appropriate ELContext. If there is an active JSF request,
    * use JSF's ELContext. Otherwise, use one that we created.
    */
   @Override
   public ELContext getELContext()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      return facesContext==null ? EL_CONTEXT : facesContext.getELContext();
   }
   
   /**
    * Validate that a value can be assigned to the property
    * identified by a value expression.
    * 
    * @param propertyExpression a value expression
    * @param value the value that is to be assigned
    * 
    * @throws ValidatorException is validation fails
    */
   public void validate(String propertyExpression, Object value)
   {
      InvalidValue[] ivs;
      try
      {
         ivs = getInvalidValues(propertyExpression, value );
      }
      catch (Exception e)
      {
         throw new ValidatorException( new FacesMessage(FacesMessage.SEVERITY_ERROR, "model validation failed:" + e.getMessage(), null), e );
      }
      if ( ivs.length>0 )
      {
         throw new ValidatorException( FacesMessages.createFacesMessage( FacesMessage.SEVERITY_ERROR, ivs[0].getMessage() ) );
      }
   }
   
   @Override
   protected boolean isFacesContextActive()
   { 
      return FacesContext.getCurrentInstance()==null; 
   }
   
   public static FacesExpressions instance()
   {
      return (FacesExpressions) Expressions.instance();
   }
   
}
