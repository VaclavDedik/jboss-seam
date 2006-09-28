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

import javax.el.ELContext;
import javax.el.ValueExpression;

import org.jboss.seam.actionparam.MethodExpressionParser;

/**
 * This is a sort of bastardized ValueExpression that takes an action expression.
 * It is used for s:link which wraps the action expression as a ValueBinding.
 *
 * The only method that it really makes sense to call is getExpressionString().
 * However, we wrap a ValueExpression and delegate for completeness.
 *
 * @author Stan Silvert
 */
public class ParamValueExpression extends ValueExpression 
{
 
    private MethodExpressionParser parser;
    private ValueExpression wrappedExpression;
    
    public ParamValueExpression(MethodExpressionParser parser,
                                      ELContext elContext, 
                                      Class expectedType)
    {
        this.parser = parser;
        this.wrappedExpression = SeamExpressionFactory.getFaceletsExpressionFactory()
                                          .createValueExpression(elContext, parser.getCombinedExpression(), expectedType);
    }

    public void setValue(ELContext elContext, Object object) 
    {
        this.wrappedExpression.setValue(elContext, object);
    }

    public boolean isReadOnly(ELContext elContext) 
    {
        return this.wrappedExpression.isReadOnly(elContext);
    }

    public Object getValue(ELContext elContext) 
    {
        return this.wrappedExpression.getValue(elContext);
    }

    public Class getType(ELContext elContext) 
    {
        return this.wrappedExpression.getType(elContext);
    }

    public boolean isLiteralText() 
    {
        return this.wrappedExpression.isLiteralText();
    }

    public String getExpressionString() 
    {
        return this.parser.getUnparsedExpression();
    }

    public Class getExpectedType() 
    {
        return this.wrappedExpression.getExpectedType();
    }
    
    @Override
    public boolean equals(Object object) 
    {
       if (this == object) return true;
       if (!(object instanceof ParamValueExpression)) return false;
       ParamValueExpression valueExpression = (ParamValueExpression)object;
       
       return this.parser.getCombinedExpression().equals(valueExpression.parser.getCombinedExpression());
    }

    @Override
    public int hashCode() 
    {
        return getExpressionString().hashCode();
    }
    
}
