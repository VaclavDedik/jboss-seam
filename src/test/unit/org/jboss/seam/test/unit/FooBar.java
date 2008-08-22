package org.jboss.seam.test.unit;

import java.util.concurrent.CountDownLatch;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("fooBar")
@Scope(ScopeType.APPLICATION)
public class FooBar
{
   @In Foo foo;
   
   public Foo delayedGetFoo(CountDownLatch latch)
   {
      try
      {
         latch.await();
      }
      catch (InterruptedException ex) {}
      
      return foo;
   }
}
