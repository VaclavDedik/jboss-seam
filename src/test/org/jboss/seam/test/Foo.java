/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.test;

import javax.ejb.Interceptor;
import javax.ejb.Remove;

import org.hibernate.validator.NotNull;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ejb.SeamInterceptor;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @version $Revision$
 */
@Interceptor(SeamInterceptor.class)
@Name("foo")
@Scope(ScopeType.SESSION)
public class Foo
{
   
   private String value;
   
   public String foo() { return "foo"; }

   @Remove
   public void destroy() {}

   @NotNull
   public String getValue()
   {
      return value;
   }

   public void setValue(String value)
   {
      this.value = value;
   }
   
   @IfInvalid(outcome="baz")
   public String bar()
   {
      return "bar";
   }
   
   @Begin
   public String begin()
   {
      return "begun";
   }
   @End
   public String end()
   {
      return "ended";
   }
   
}


