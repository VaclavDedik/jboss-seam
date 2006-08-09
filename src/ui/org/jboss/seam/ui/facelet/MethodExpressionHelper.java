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

package org.jboss.seam.ui.facelet;

import java.io.Serializable;
import java.lang.reflect.Method;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.MethodInfo;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import org.jboss.seam.actionparam.MethodExpressionParser;

/**
 * This is a helper class for the ParamMethodExpression.
 *
 * @author Stan Silvert
 */
class MethodExpressionHelper implements Serializable 
{
    
    private String expWithParams;
    private boolean isStringLiteral = false;

    private MethodExpressionParser parser;
    
    // The wrapped expression
    private MethodExpression methodExp;
    
    // used for equals and hashCode
    private boolean expressionInitializedInCtor = false;
    private String hashString;
    
    
    MethodExpressionHelper(ELContext elContext, 
                           MethodExpressionParser parser) 
    {
        this.expWithParams = parser.getUnparsedExpression();
        this.parser = parser;
        
        if (parser.isStringLiteral(expWithParams)) 
        {
            setMethodExpression(elContext, new Class[0]);
            this.isStringLiteral = true;
            this.expressionInitializedInCtor = true;
            return;
        }
        
        // if there are no params, we can go ahead and create the expression
        if (parser.getParams().length == 0) 
        {
            setMethodExpression(elContext, new Class[0]);
            this.expressionInitializedInCtor = true;
        }
    }
    
    boolean isLiteralText() 
    {
        return this.isStringLiteral;
    }
    
    MethodInfo getMethodInfo(ELContext elContext) 
    {
        if (this.methodExp == null) 
        {
            return new PartialMethodInfo(parser.getMethodName());
        }
        
        return this.methodExp.getMethodInfo(elContext);
    }
    
    Object invokeTheExpression(ELContext elContext) 
    {
        if (this.methodExp == null) 
        {
            setMethodExpression(elContext, findParamTypes(elContext));
        }
        
        Object[] evaluatedParams = evaluateParams(elContext);
        return this.methodExp.invoke(elContext, evaluatedParams);
    }
    
    MethodExpression getExpression() 
    {
        return this.methodExp;
    }
    
    // evaluate each param as a value expression and return the array of results
    private Object[] evaluateParams(ELContext elContext) 
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
            
            ValueExpression ve = SeamExpressionFactory.getFaceletsExpressionFactory()
                                         .createValueExpression(elContext, "#{" + param + "}", Object.class);
            results[i] = ve.getValue(elContext);
        }
        
        return results;
    }
    
    // finds a public method that matches the method
    // name and number of params
    private Class[] findParamTypes(ELContext elContext) 
    {
        if (parser.getParams().length == 0) return new Class[0];
        
        String expression = "#{" + parser.getBaseExpression() + "}";
        ValueExpression ve = SeamExpressionFactory.getFaceletsExpressionFactory().
                                     createValueExpression(elContext, expression, Object.class);
        Object obj = ve.getValue(elContext);
        Method[] publicMethods = obj.getClass().getMethods();
        
        Method methodMatch = null;
        
        for (int i=0; i < publicMethods.length; i++) 
        {
            if (methodMatches(publicMethods[i])) 
            {
                if (methodMatch != null) 
                {
                    throw new ELException("More than one method matched " + 
                            this.expWithParams + ". Method name or number of params must be unique.");
                }
                
                methodMatch = publicMethods[i];
            }
        }
        
        if (methodMatch == null) 
        {
            throw new ELException("No method found for expression " + 
                                  this.expWithParams +
                                  ".  Method name and number of params must match.");
        }
        
        return methodMatch.getParameterTypes();
    }
    
    // To match, method must return a String, match method name, and match 
    // number of params
    private boolean methodMatches(Method method) 
    {
        return method.getName().equals(parser.getMethodName()) && 
              (method.getParameterTypes().length == parser.getParams().length);
    }
    
    private void setMethodExpression(ELContext elContext, Class[] paramTypes) 
    {
        // note: returnType is always a String because this is an action method
        this.methodExp = SeamExpressionFactory.getFaceletsExpressionFactory().
                                    createMethodExpression(elContext, 
                                                           parser.getCombinedExpression(), 
                                                           String.class, 
                                                           paramTypes);
    }
    
    private String getHashString() 
    {
        if (this.hashString == null) 
        {
            this.hashString = parser.getBaseExpression() + "." +
                              parser.getMethodName() + "." +
                              parser.getParams().length;
        }
        
        return this.hashString;
    }
    
    @Override
    public boolean equals(Object object) 
    {
       if (this == object) return true;
       
       if (this.expressionInitializedInCtor) 
       {
           return this.methodExp.equals(object);
       }
       
       if (!(object instanceof MethodExpressionHelper)) return false;
       MethodExpressionHelper exp = (MethodExpressionHelper)object;
              
       return getHashString().equals(exp.getHashString());
    }

    @Override
    public int hashCode() {
        if (this.expressionInitializedInCtor) 
        {
            return this.methodExp.hashCode();
        }
        
        return getHashString().hashCode();
    }

    /**
     * Limited MethodInfo implementation that can be used when the param types are
     * not yet known.
     */
    private static class PartialMethodInfo extends MethodInfo 
    {
        private PartialMethodInfo(String methodName) 
        {
            super(methodName, Object.class, new Class[0]);
        }
        
        public Class<?>[] getParamTypes() 
        {
            throw new IllegalStateException("paramTypes unknown until MethodExpression is invoked.");
        }
    }
}