package org.jboss.seam.test.integration;

import javax.ejb.Local;
import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.JndiName;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class FactoryLockTest extends JUnitSeamTest
{
   private volatile boolean exceptionOccured = false;
   
   @Deployment(name="FactoryLockTest")
   @OverProtocol("Servlet 3.0") 
   public static Archive<?> createDeployment()
   {
      return Deployments.defaultSeamDeployment()
            .addClasses(FactoryLockAction.class, FactoryLockLocal.class, TestProducer.class);
   }
   
   // JBSEAM-4993
   // The test starts two threads, one evaluates #{factoryLock.test.test()} and the other #{factoryLock.testString} 200ms later
   @Test
   public void factoryLock() 
       throws Exception 
   {
      exceptionOccured = false;
      Thread thread1 = new Thread() {
         @Override
         public void run()
         {
            try
            {
               FactoryLockTest.this.factoryLockTestPart1();
            }
            catch (Throwable e)
            {
               e.printStackTrace();
               FactoryLockTest.this.exceptionOccured = true;
            }
         }
      };

      Thread thread2 = new Thread() {
         @Override
         public void run()
         {
            try
            {
               FactoryLockTest.this.factoryLockTestPart2();
            }
            catch (Throwable e)
            {
               e.printStackTrace();
               FactoryLockTest.this.exceptionOccured = true;
            }
         }
      };

      thread1.start();
      thread2.start();
   
      thread1.join();
      thread2.join();
      
      assert !exceptionOccured;
   }
   
   private void factoryLockTestPart1() throws Exception {
      new ComponentTest() {
         @Override
         protected void testComponents() throws Exception {
            assertEquals("test", invokeMethod("#{factoryLock.test.test()}"));
         }
     }.run();
   }
   
   private void factoryLockTestPart2() throws Exception {
      new ComponentTest() {
         @Override
         protected void testComponents() throws Exception {
            Thread.sleep(200);
            assertEquals("testString", getValue("#{factoryLock.testString}"));
         }
     }.run();
   }
   
  
   @Local
   public static interface FactoryLockLocal
   {
      public String getTestString();
      public String test();
      public void remove();
   }

   
   @Stateful
   @Scope(ScopeType.SESSION)
   @Name("factoryLock.test")
   @JndiName("java:global/test/FactoryLockTest$FactoryLockAction")
   public static class FactoryLockAction implements FactoryLockLocal
   {
      public String test() {
         try
         {
            Thread.sleep(500);
         }
         
         catch (InterruptedException e)
         {
            e.printStackTrace();
         }
         Component.getInstance("factoryLock.foo", true);
         return "test";
      }
      
      @Factory(value="factoryLock.testString", scope=ScopeType.EVENT)
      public String getTestString() {
         return "testString";
      }
      @Remove
      public void remove() {}
   }
   
   @Name("factoryLock.testProducer")
   public static class TestProducer {
      @Factory(value="factoryLock.foo", scope=ScopeType.EVENT)
      public String getFoo() {
         return "foo";
      }
   }
}
