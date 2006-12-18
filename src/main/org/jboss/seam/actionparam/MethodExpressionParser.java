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

import java.io.Serializable;
import javax.faces.el.EvaluationException;

/**
 * Parses an action expression.  This expression is assumed to be of the form
 * #{value-expression.method-name(param-list)}
 * 
 * The (param-list) is optional.
 * The (param-list) can be empty.
 * ${ } notation may be used instead of #{ }
 * Square bracket notation: i.e. value-expression[method-name] is also allowed.
 *
 * String literals surrounded by single or double quotes can be passed as params.
 * example: #{register.register(user, 'login page')}
 *
 * Also, the entire expression can be a simple String.  In this case, there is no
 * bracket notation at all. i.e. foo
 *
 * @author Stan Silvert
 */
public class MethodExpressionParser implements Serializable 
{
    
    private String unparsedExpression;
    private String baseExpression = "";
    private String methodName = "";
    private String[] params;
    private String combinedExpression;
    private boolean hasParamsInExpression;
    
    public MethodExpressionParser(String expression) 
    {
        this.unparsedExpression = expression;
        
        try 
        {
            String noBrackets = stripBrackets(expression);
            noBrackets = convertSquareBrackets(noBrackets);
            
            this.hasParamsInExpression = hasParams(noBrackets);

            if ( isStringLiteral(expression) ) 
            {
                this.params = new String[0];
                this.baseExpression = expression.trim();
                this.methodName = "";
                this.combinedExpression = this.baseExpression;
                return;
            }
            
            if ( hasNoMethod(noBrackets) )
            {
                this.params = new String[0];
                this.baseExpression = noBrackets;
                this.methodName = "";
                this.combinedExpression = "#{" + this.baseExpression + "}";
                return;
            }
            
            this.params = getParams(noBrackets);
            String beforeParams = beforeParams(noBrackets);

            this.baseExpression = extractBaseExpression(beforeParams).trim();
            this.methodName = extractMethod(beforeParams).trim();

            this.combinedExpression = "#{" + baseExpression + "." + methodName + "}";
        } 
        catch (Exception e) 
        {
            throw new EvaluationException("Unable to parse method expression: " + expression, e);
        }
    }
    
    public String getUnparsedExpression() 
    {
        return this.unparsedExpression;
    }
    
    public boolean hasParamsInExpression()
    {
        return this.hasParamsInExpression;
    }
    
    public String getBaseExpression() 
    {
        return this.baseExpression;
    }
    
    public String getMethodName() 
    {
        return this.methodName;
    }
    
    public String[] getParams() 
    {
        return this.params;
    }
    
    /**
     * MethodExpression without the params.  Combines baseExpression and
     * methodName in the form #{expression.method}.
     *
     * For string literals, this just returns the literal.
     */
    public String getCombinedExpression() 
    {
        return this.combinedExpression;
    }
    
    public boolean isParamExpression()
    {
        return (!isStringLiteral(this.unparsedExpression)) && 
                (hasParamsInExpression() || hasNoArgParens(this.unparsedExpression));
    }
    
    //---------- Static methods used for parsing ---------------------------------
    /**
     * This tells if a method param is a quoted String.  If it is not a quoted
     * String then it is treated as a ValueExpression.
     */
    public static boolean isQuotedString(String str) 
    {
        str = str.trim();
        if ((str.charAt(0) == '"') && (str.charAt(str.length() - 1) == '"') ) {
            return true;
        }
        
        if ((str.charAt(0) == '\'') && (str.charAt(str.length() -1) == '\'')) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Tells if the whole expression is just a String literal.
     */
    public static boolean isStringLiteral(String expression) 
    {
        String trimmed = expression.trim();
        return !( (trimmed.startsWith("${") || trimmed.startsWith("#{")) &&
                   trimmed.endsWith("}") );
    }
    
    // tells if expression is of the form #{foo} 
    // If so, it is really a ValueExpression, but we handle it anyway
    private static boolean hasNoMethod(String expression) 
    {
        String beforeParams = beforeParams(expression);
        return (beforeParams.lastIndexOf('.') == -1);
    }
    
    private static String extractBaseExpression(String expression) 
    {
        return expression.substring(0, expression.lastIndexOf('.'));
    }
    
    private static String extractMethod(String expression) 
    {
        return expression.substring(expression.lastIndexOf('.') + 1, expression.length());
    }
    
    private static String beforeParams(String expression) 
    {
        if (!hasParams(expression) && noOpenParen(expression)) return expression;
        
        return expression.substring(0, expression.indexOf('('));
    }
    
    private static boolean noOpenParen(String expression) 
    {
        return expression.indexOf('(') == -1;
    }
    
    private static String stripBrackets(String expression) 
    {
        String trimmed = expression.trim();
        
        if (trimmed.length() < 3)
        {
            return trimmed;
        }
        
        return trimmed.substring(2, trimmed.length() - 1);
    }
    
    // expects full expression with brackets stripped
    private static boolean hasParams(String expression) 
    {
        int openParen = expression.indexOf('(');
        int closeParen = expression.lastIndexOf(')');
        
        if ( (openParen == -1) || (closeParen == -1) ) return false;
        return !expression.substring(openParen + 1, closeParen).trim().equals("");
    }

    // expects full expression
    // see if expression contains a no-arg method call
    private static boolean hasNoArgParens(String expression) 
    {
        int openParen = expression.indexOf('(');
        int closeParen = expression.lastIndexOf(')');
        
        if ( (openParen == -1) || (closeParen == -1) ) return false;
        return expression.substring(openParen + 1, closeParen).trim().equals("");
    }
    
    // extract each param
    private static String[] getParams(String exp) 
    {
        if (!hasParams(exp)) return new String[0];
        
        String params = paramsOnly(exp);
        
        String[] split = params.split(",");
        for (int i=0; i < split.length; i++) split[i] = split[i].trim();
        
        return split;
    }
    
    // returns everything inside the outter parens 
    // for expmple, foo.bar(param1, param2) would return "param1, param2"
    private static String paramsOnly(String exp) 
    {
        int start = exp.indexOf('(') + 1;
        int end = exp.lastIndexOf(')');
        return exp.substring(start, end);
    }
    
    // convert "object[method(param1, param2)]" to "object.method(param1, param2)"
    private static String convertSquareBrackets(String exp) 
    {
        String trimmed = exp.trim();
        if (!trimmed.endsWith("]")) return exp;
        
        // remove ending bracket
        trimmed = trimmed.substring(0, trimmed.lastIndexOf(']'));
        
        int openBracket = trimmed.lastIndexOf('[');
        if (openBracket == -1) return exp;
        
        StringBuilder builder = new StringBuilder(trimmed.substring(0, openBracket));
        builder.append('.');
        builder.append(trimmed.substring(openBracket + 1, trimmed.length()));
        
        return builder.toString();
    }
    
}
