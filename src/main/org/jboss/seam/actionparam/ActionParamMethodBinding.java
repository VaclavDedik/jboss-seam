/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.seam.actionparam;

import javax.faces.application.Application;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.MethodNotFoundException;

import org.jboss.seam.jsf.SeamApplication;

/**
 * This class assumes that the entire expression is not a string literal.  If
 * it is, use SimpleActionMethodBinding instead.
 *
 * @author Stan Silvert
 */
public class ActionParamMethodBinding extends MethodBinding implements StateHolder {
    
    private String expWithParams;
    private ActionParamBindingHelper helper;
    private Application application;
    
    private boolean isTransient = false;

    public ActionParamMethodBinding()
    {
        // constructor needed for StateHolder
        application = ( (SeamApplication) FacesContext.getCurrentInstance().getApplication() ).getDelegate();
    }
    
    public ActionParamMethodBinding(Application application, String expWithParams) {
        if (MethodExpressionParser.isStringLiteral(expWithParams)) {
            throw new EvaluationException(expWithParams + " is not an EL expression");
        }
        
        this.expWithParams = expWithParams;
        this.application = application;
        this.helper = new ActionParamBindingHelper(application, expWithParams);
    }
    
    @Override
    public Class getType(FacesContext facesContext) throws MethodNotFoundException {
        return String.class; // since this is a JSF 1.1 style action, we assume it returns a String
    }

    @Override
    public Object invoke(FacesContext facesContext, Object[] object) throws EvaluationException, MethodNotFoundException {
        return this.helper.invokeTheExpression(facesContext);
    }

    @Override
    public String getExpressionString() {
        return expWithParams;
    }
    
    public void restoreState(FacesContext facesContext, Object object) {
        this.expWithParams = (String) object;
        this.helper = new ActionParamBindingHelper(application, expWithParams);
    }

    public Object saveState(FacesContext facesContext) {
        return this.expWithParams;
    }
    
    public void setTransient(boolean isTransient) {
        this.isTransient = isTransient;
    }

    public boolean isTransient() {
        return this.isTransient;
    }
}