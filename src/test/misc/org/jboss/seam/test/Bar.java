/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.test;

import javax.ejb.Interceptor;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ejb.SeamInterceptor;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @version $Revision$
 */
@Interceptor(SeamInterceptor.class)
@Name("bar")
@Scope(ScopeType.CONVERSATION)
@Conversational(ifNotBegunOutcome="error")
public class Bar
{
   
   @In(required=true)
   Foo otherFoo;
   
   @In(create=true)
   Foo foo;
   
   @Out
   String string;
   
   @Begin
   public String begin()
   {
      return "begun";
   }
   public String foo()
   {
      string = "out";
      return "foo";
   }
   
   @End
   public String end()
   {
      return "ended";
   }
   
   @Destroy
   public void destroy(){}
   @Create
   public void create(){}
   
}


