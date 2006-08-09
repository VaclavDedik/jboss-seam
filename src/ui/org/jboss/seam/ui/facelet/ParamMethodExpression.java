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
import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.MethodInfo;
import javax.el.MethodNotFoundException;
import javax.el.PropertyNotFoundException;
import org.jboss.seam.actionparam.MethodExpressionParser;

/**
 * TODO: make this into a StateHolder?
 *
 * @author Stan Silvert
 */
public class ParamMethodExpression extends MethodExpression 
{
    
    private MethodExpressionHelper helper;
    private String expString;
    
    public ParamMethodExpression(MethodExpressionParser parser,
                                 ELContext elContext) 
    {
        this.expString = parser.getUnparsedExpression();
        this.helper = new MethodExpressionHelper(elContext, parser);
    }
    
    @Override
    public MethodInfo getMethodInfo(ELContext elContext) throws NullPointerException, PropertyNotFoundException, MethodNotFoundException, ELException 
    {
        return this.helper.getMethodInfo(elContext);
    }

    @Override
    public Object invoke(ELContext elContext, Object[] params) throws NullPointerException, PropertyNotFoundException, MethodNotFoundException, ELException 
    {
        // note that the params are ignored - they were cached by the helper
        return this.helper.invokeTheExpression(elContext);
    }

    @Override
    public boolean isLiteralText() 
    {
        return this.helper.isLiteralText();
    }

    @Override
    public String getExpressionString() 
    {
        return this.expString;
    }

    @Override
    public boolean equals(Object object) 
    {
       if (this == object) return true;
       if (!(object instanceof ParamMethodExpression)) return false;
       ParamMethodExpression actionExpression = (ParamMethodExpression)object;
       
       return this.helper.equals(actionExpression.helper);
    }

    @Override
    public int hashCode() 
    {
        return this.helper.hashCode();
    }
}
