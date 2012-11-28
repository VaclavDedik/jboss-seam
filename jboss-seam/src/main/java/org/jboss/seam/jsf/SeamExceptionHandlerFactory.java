package org.jboss.seam.jsf;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;
import javax.faces.context.FacesContext;

import org.jboss.seam.web.ExceptionFilter;

import com.sun.faces.application.ApplicationAssociate;
import com.sun.faces.context.AjaxExceptionHandlerImpl;
import com.sun.faces.context.ExceptionHandlerImpl;



/**
 * Factory not to be used AjaxExceptionHandlerImpl class and 
 * always be an exception to be thrown by capturad ExceptionFilter
 * 
 * @see AjaxExceptionHandlerImpl
 * @see ExceptionFilter  
 * @author Tiago Peruzzo
 * 
 */
public class SeamExceptionHandlerFactory extends ExceptionHandlerFactory {

	private ApplicationAssociate associate;


	@Override
    public ExceptionHandler getExceptionHandler() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ApplicationAssociate associate = getAssociate(fc);
        return new ExceptionHandlerImpl(((associate != null) ? associate.isErrorPagePresent() : Boolean.TRUE));
    }


    // --------------------------------------------------------- Private Methods

    private ApplicationAssociate getAssociate(FacesContext ctx) {
        if (associate == null) {
            associate = ApplicationAssociate.getCurrentInstance();
            if (associate == null) {
                associate = ApplicationAssociate.getInstance(ctx.getExternalContext());
            }
        }
        return associate;
    }
	
}
