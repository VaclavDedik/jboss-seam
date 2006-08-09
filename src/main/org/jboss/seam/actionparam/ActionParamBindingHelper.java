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

import java.lang.reflect.Method;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

/**
 * This is a helper class for parsing and invoking the action.
 *
 * @author Stan Silvert
 */
class ActionParamBindingHelper 
{
    
    private Application application;
    private String expWithParams;

    private MethodExpressionParser parser;
    
    ActionParamBindingHelper(Application application,
                      String expWithParams) 
    {
        this.application = application;
        this.expWithParams = expWithParams;
        this.parser = new MethodExpressionParser(expWithParams);    
    }
    
    Object invokeTheExpression(FacesContext facesContext) 
    {
        MethodBinding mb = makeMethodBinding(findParamTypes(facesContext));
        
        Object[] evaluatedParams = evaluateParams(facesContext);
        return mb.invoke(facesContext, evaluatedParams);
    }
    
    // evaluate each param as a value expression and return the array of results
    private Object[] evaluateParams(FacesContext facesContext) 
    {
        String[] params = parser.getParams();
        Object[] results = new Object[params.length];
        
        for (int i=0; i < results.length; i++) 
        {
            String param = params[i].trim();
            
            if (parser.isQuotedString(param)) 
            {
                // strip quotes
                results[i] = param.substring(1, param.length() - 1);
                continue;
            }
            
            ValueBinding vb = application.createValueBinding("#{" + param + "}");
            
            results[i] = vb.getValue(facesContext);
        }
        
        return results;
    }
    
    // For each param, finds a public method that 
    // matches the method name and number of params
    private Class[] findParamTypes(FacesContext facesContext) 
    {
        if (parser.getParams().length == 0) return new Class[0];
        
        String expression = "#{" + parser.getBaseExpression() + "}";
        ValueBinding vb = application.createValueBinding(expression);
        Object obj = vb.getValue(facesContext);
        Method[] publicMethods = obj.getClass().getMethods();
        
        Method methodMatch = null;
        
        for (int i=0; i < publicMethods.length; i++) {
            if (methodMatches(publicMethods[i])) 
            {
                if (methodMatch != null) 
                {
                    throw new EvaluationException("More than one method matched " + 
                            this.expWithParams + ". Method name or number of params must be unique.");
                }
                
                methodMatch = publicMethods[i];
            }
        }
        
        if (methodMatch == null) 
        {
            throw new EvaluationException("No method found for expression " + 
                                          this.expWithParams +
                                          ".  Method name and number of params must match.");
        }
        
        return methodMatch.getParameterTypes();
    }
    
    // To match, method must match method name and match 
    // number of params
    private boolean methodMatches(Method method) 
    {
        return method.getName().equals(parser.getMethodName()) && 
              (method.getParameterTypes().length == parser.getParams().length);
    }
    
    private MethodBinding makeMethodBinding(Class[] paramTypes) 
    {
        return application.createMethodBinding(parser.getCombinedExpression(), paramTypes);
    }
    
}