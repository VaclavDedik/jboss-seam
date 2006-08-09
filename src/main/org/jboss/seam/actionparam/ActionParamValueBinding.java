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

import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.ValueBinding;

/**
 * This is a sort of bastardized ValueBinding that takes an action expression.
 * It is used for s:link which wraps the action expression as a ValueBinding.
 *
 * The only method that it really makes sense to call is getExpressionString().
 * However, we wrap a ValueBinding and delegate for completeness.
 *
 * @author Stan Silvert
 */
public class ActionParamValueBinding extends ValueBinding implements StateHolder 
{
    
    private String expression;
    private ValueBinding binding;
    
    private boolean isTransient = false;
    
    public ActionParamValueBinding()
    {
        // needed for StateHolder
    }
    
    public ActionParamValueBinding(FacesContext facesContext, String expression)
    {
        this.expression = expression;
        setBinding(facesContext, expression);
    }
    
    private void setBinding(FacesContext facesContext, String expression)
    {
        MethodExpressionParser parser = new MethodExpressionParser(expression);
        this.binding = facesContext.getApplication().createValueBinding(parser.getCombinedExpression());
    }

    public void setValue(FacesContext facesContext, Object object) throws EvaluationException, PropertyNotFoundException 
    {
        this.binding.setValue(facesContext, object);
    }

    public boolean isReadOnly(FacesContext facesContext) throws EvaluationException, PropertyNotFoundException 
    {
        return this.binding.isReadOnly(facesContext);
    }

    public Object getValue(FacesContext facesContext) throws EvaluationException, PropertyNotFoundException 
    {
        return this.binding.getValue(facesContext);
    }

    public Class getType(FacesContext facesContext) throws EvaluationException, PropertyNotFoundException 
    {
        return this.binding.getType(facesContext);
    }

    public String getExpressionString() 
    {
        return this.expression;
    }

    public void restoreState(FacesContext facesContext, Object object) {
        this.expression = (String)object;
        setBinding(facesContext, this.expression);
    }

    public Object saveState(FacesContext facesContext) {
        return this.expression;
    }

    public void setTransient(boolean isTransient) {
        this.isTransient = isTransient;
    }

    public boolean isTransient() {
        return this.isTransient;
    }
}
