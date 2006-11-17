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

package org.jboss.seam.test;

import org.jboss.seam.actionparam.MethodExpressionParser;

import org.testng.annotations.Test;

/**
 * Test the MethodExpressionParser
 *
 * @author Stan Silvert
 */
public class MethodExpressionParserTest 
{
   
   @Test 
   public void testStaticMEParserMethods()
   {   
       // is a param a quoted String or a value expression?
       assert MethodExpressionParser.isQuotedString("'foo'");
       assert !MethodExpressionParser.isQuotedString("foo");
       assert MethodExpressionParser.isQuotedString("\"foo\"");
       
       // is the entire expression just a String literal?
       assert MethodExpressionParser.isStringLiteral("foo");
       assert MethodExpressionParser.isStringLiteral("#{foo");
       assert MethodExpressionParser.isStringLiteral("\\#{foo}");
       assert !MethodExpressionParser.isStringLiteral("${foo}");
       assert !MethodExpressionParser.isStringLiteral("#{foo}");
   }
    
   @Test
   public void testMEParser()
   {
       MethodExpressionParser parser = new MethodExpressionParser("");
       assert parser.getUnparsedExpression().equals("");
       assert parser.getBaseExpression().equals("");
       assert !parser.hasParamsInExpression();
       assert parser.getParams().length == 0;
       assert parser.getMethodName().equals("");
       assert parser.getCombinedExpression().equals("");
       assert !parser.isParamExpression();
       
       parser = new MethodExpressionParser("foobar");
       assert parser.getUnparsedExpression().equals("foobar");
       assert parser.getBaseExpression().equals("foobar");
       assert !parser.hasParamsInExpression();
       assert parser.getParams().length == 0;
       assert parser.getMethodName().equals("");
       assert parser.getCombinedExpression().equals("foobar");
       assert !parser.isParamExpression();
       
       parser = new MethodExpressionParser("${foo}");
       assert parser.getUnparsedExpression().equals("${foo}");
       assert parser.getBaseExpression().equals("foo");
       assert !parser.hasParamsInExpression();
       assert parser.getParams().length == 0;
       assert parser.getMethodName().equals("");
       assert parser.getCombinedExpression().equals("#{foo}");
       assert !parser.isParamExpression();
       
       parser = new MethodExpressionParser("  #{foo}   ");
       assert parser.getUnparsedExpression().equals("  #{foo}   ");
       assert parser.getBaseExpression().equals("foo");
       assert !parser.hasParamsInExpression();
       assert parser.getParams().length == 0;
       assert parser.getMethodName().equals("");
       assert parser.getCombinedExpression().equals("#{foo}");
       assert !parser.isParamExpression();

       parser = new MethodExpressionParser("#{foo.bar}");
       assert parser.getUnparsedExpression().equals("#{foo.bar}");
       assert parser.getBaseExpression().equals("foo");
       assert parser.getMethodName().equals("bar");
       assert !parser.hasParamsInExpression();
       assert parser.getParams().length == 0;
       assert parser.getCombinedExpression().equals("#{foo.bar}");
       assert !parser.isParamExpression();
       
       parser = new MethodExpressionParser("#{foo[bar]}");
       assert parser.getUnparsedExpression().equals("#{foo[bar]}");
       assert parser.getBaseExpression().equals("foo");
       assert parser.getMethodName().equals("bar");
       assert !parser.hasParamsInExpression();
       assert parser.getParams().length == 0;
       assert parser.getCombinedExpression().equals("#{foo.bar}");
       assert !parser.isParamExpression();
       
       parser = new MethodExpressionParser("#{foo[bar()]}");
       assert parser.getUnparsedExpression().equals("#{foo[bar()]}");
       assert parser.getBaseExpression().equals("foo");
       assert parser.getMethodName().equals("bar");
       assert !parser.hasParamsInExpression();
       assert parser.getParams().length == 0;
       assert parser.getCombinedExpression().equals("#{foo.bar}");
       assert parser.isParamExpression();
       
       parser = new MethodExpressionParser("#{foo.bar(baz)}");
       assert parser.getUnparsedExpression().equals("#{foo.bar(baz)}");
       assert parser.getBaseExpression().equals("foo");
       assert parser.getMethodName().equals("bar");
       assert parser.hasParamsInExpression();
       assert parser.getParams().length == 1;
       assert parser.getParams()[0].equals("baz");
       assert parser.getCombinedExpression().equals("#{foo.bar}");
       assert parser.isParamExpression();
       
       parser = new MethodExpressionParser("#{foo.bar.baz(  )}");
       assert parser.getUnparsedExpression().equals("#{foo.bar.baz(  )}");
       assert parser.getBaseExpression().equals("foo.bar");
       assert parser.getMethodName().equals("baz");
       assert !parser.hasParamsInExpression();
       assert parser.getParams().length == 0;
       assert parser.getCombinedExpression().equals("#{foo.bar.baz}");
       assert parser.isParamExpression();
       
       parser = new MethodExpressionParser("#{foo.bar.baz(bum)}");
       assert parser.getUnparsedExpression().equals("#{foo.bar.baz(bum)}");
       assert parser.getBaseExpression().equals("foo.bar");
       assert parser.getMethodName().equals("baz");
       assert parser.hasParamsInExpression();
       assert parser.getParams().length == 1;
       assert parser.getParams()[0].equals("bum");
       assert parser.getCombinedExpression().equals("#{foo.bar.baz}");
       assert parser.isParamExpression();
       
       parser = new MethodExpressionParser("#{foo.bar[baz(bum)]}");
       assert parser.getUnparsedExpression().equals("#{foo.bar[baz(bum)]}");
       assert parser.getBaseExpression().equals("foo.bar");
       assert parser.getMethodName().equals("baz");
       assert parser.hasParamsInExpression();
       assert parser.getParams().length == 1;
       assert parser.getParams()[0].equals("bum");
       assert parser.getCombinedExpression().equals("#{foo.bar.baz}");
       assert parser.isParamExpression();
       
       parser = new MethodExpressionParser("#{foo.bar.baz(\"bum\")}");
       assert parser.getUnparsedExpression().equals("#{foo.bar.baz(\"bum\")}");
       assert parser.getBaseExpression().equals("foo.bar");
       assert parser.getMethodName().equals("baz");
       assert parser.hasParamsInExpression();
       assert parser.getParams().length == 1;
       assert parser.getParams()[0].equals("\"bum\"");
       assert parser.getCombinedExpression().equals("#{foo.bar.baz}");
       assert parser.isParamExpression();
       
       parser = new MethodExpressionParser("#{foo.bar.baz(bum, booger, 'foo')}");
       assert parser.getUnparsedExpression().equals("#{foo.bar.baz(bum, booger, 'foo')}");
       assert parser.getBaseExpression().equals("foo.bar");
       assert parser.getMethodName().equals("baz");
       assert parser.hasParamsInExpression();
       assert parser.getParams().length == 3;
       assert parser.getParams()[0].equals("bum");
       assert parser.getParams()[1].equals("booger");
       assert parser.getParams()[2].equals("'foo'");
       assert parser.getCombinedExpression().equals("#{foo.bar.baz}");
       assert parser.isParamExpression();
       
       parser = new MethodExpressionParser("#{foo[1].bar[2].baz(bum, booger, 'foo')}");
       assert parser.getUnparsedExpression().equals("#{foo[1].bar[2].baz(bum, booger, 'foo')}");
       assert parser.getBaseExpression().equals("foo[1].bar[2]");
       assert parser.getMethodName().equals("baz");
       assert parser.hasParamsInExpression();
       assert parser.getParams().length == 3;
       assert parser.getParams()[0].equals("bum");
       assert parser.getParams()[1].equals("booger");
       assert parser.getParams()[2].equals("'foo'");
       assert parser.getCombinedExpression().equals("#{foo[1].bar[2].baz}");
       assert parser.isParamExpression();
       
       parser = new MethodExpressionParser("#{foo[\"one\"].bar[\"two\"].baz(bum, booger, 'foo')}");
       assert parser.getUnparsedExpression().equals("#{foo[\"one\"].bar[\"two\"].baz(bum, booger, 'foo')}");
       assert parser.getBaseExpression().equals("foo[\"one\"].bar[\"two\"]");
       assert parser.getMethodName().equals("baz");
       assert parser.hasParamsInExpression();
       assert parser.getParams().length == 3;
       assert parser.getParams()[0].equals("bum");
       assert parser.getParams()[1].equals("booger");
       assert parser.getParams()[2].equals("'foo'");
       assert parser.getCombinedExpression().equals("#{foo[\"one\"].bar[\"two\"].baz}");
       assert parser.isParamExpression();
       
       parser = new MethodExpressionParser("#{foo[1].bar[2][baz(bum, booger, 'foo')]}");
       assert parser.getUnparsedExpression().equals("#{foo[1].bar[2][baz(bum, booger, 'foo')]}");
       assert parser.getBaseExpression().equals("foo[1].bar[2]");
       assert parser.getMethodName().equals("baz");
       assert parser.hasParamsInExpression();
       assert parser.getParams().length == 3;
       assert parser.getParams()[0].equals("bum");
       assert parser.getParams()[1].equals("booger");
       assert parser.getParams()[2].equals("'foo'");
       assert parser.getCombinedExpression().equals("#{foo[1].bar[2].baz}");
       assert parser.isParamExpression();
       
   }
    
}
