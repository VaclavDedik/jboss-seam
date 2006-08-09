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

import com.sun.facelets.compiler.SAXCompiler;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import org.jboss.seam.actionparam.MethodExpressionParser;

/**
 * This ExpressionFactory replaces the one normally used in Facelets.  It
 * recognizes if an EL expression is using params.  If so, it will return a
 * special implementation of MethodExpression or ValueExpression to handle it.
 *
 * @author Stan Silvert
 */
public class SeamExpressionFactory extends ExpressionFactory 
{
    private static ExpressionFactory faceletsExpressionFactory;
    
    static 
    {
        // wrap the ExpressionFactory that Facelets would have created
        SAXCompiler compiler = new SAXCompiler();
        faceletsExpressionFactory = compiler.createExpressionFactory();
    }
    
    public SeamExpressionFactory() 
    {
    }
    
    /**
     * Note default access.  Other classes in this package use this factory
     * to create EL Expressions.
     */
    static ExpressionFactory getFaceletsExpressionFactory()
    {
        return faceletsExpressionFactory;
    }
    
    @Override
    public Object coerceToType(Object obj, Class targetType) 
    {
        return faceletsExpressionFactory.coerceToType(obj, targetType);
    }

    @Override
    public MethodExpression createMethodExpression(ELContext elContext, 
                                                   String expression, 
                                                   Class returnType, 
                                                   Class[] paramTypes) 
    {
        MethodExpressionParser parser = new MethodExpressionParser(expression);
        if (parser.isParamExpression())
        {
            return new ParamMethodExpression(parser, elContext);
        }
        
        return faceletsExpressionFactory.createMethodExpression(elContext, expression, returnType, paramTypes);
    }
    
    @Override
    public ValueExpression createValueExpression(Object instance, Class expectedType) 
    {
        return faceletsExpressionFactory.createValueExpression(instance, expectedType);
    }

    @Override
    public ValueExpression createValueExpression(ELContext elContext,
                                                 String expression,
                                                 Class expectedType) 
    {
        MethodExpressionParser parser = new MethodExpressionParser(expression);
        if (parser.isParamExpression())
        {
            return new ParamValueExpression(parser, elContext, expectedType);
        }
        
        return faceletsExpressionFactory.createValueExpression(elContext, expression, expectedType);
    }
    
}