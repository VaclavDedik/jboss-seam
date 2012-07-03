package org.jboss.seam.test.integration;

import java.io.Serializable;

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
import org.jboss.seam.annotations.Synchronized;
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
            .addClasses(FactoryLockAction.class, FactoryLockLocal.class, TestProducer.class, SeamSynchronizedFactoryLockAction.class);
   }
   
   private abstract class TestThread extends Thread {
      public abstract void runTest() throws Exception;
      
      @Override
      public void run()
      {
         try
         {
            runTest();
         }
         catch (Throwable e)
         {
            e.printStackTrace();
            FactoryLockTest.this.exceptionOccured = true;
         }
      }
   }
   
   private void multiThreadedTest(Thread... threads) throws InterruptedException {
      exceptionOccured = false;
      
      for (Thread thread : threads) {
         thread.start();
      }
      
      for (Thread thread : threads) {
         thread.join();
      }
      
      assert !exceptionOccured;
   }
   
   // JBSEAM-4993
   // The test starts two threads, one evaluates #{factoryLock.test.testOtherFactory()} and the other #{factoryLock.testString} 200ms later
   @Test
   public void factoryLock() 
       throws Exception 
   {
      multiThreadedTest(new TestThread() {
         @Override
         public void runTest() throws Exception
         {
            FactoryLockTest.this.invokeMethod("foo", "#{factoryLock.test.testOtherFactory()}");
         }
      },

      new TestThread() {
         @Override
         public void runTest() throws Exception
         {
            Thread.sleep(200);
            FactoryLockTest.this.getValue("testString", "#{factoryLock.testString}");
         }
      });
   }
   
   // This test is the same as factoryLock test, except it uses the same factory in both threads.
   @Test
   public void sameFactoryLock() 
       throws Exception 
   {
      multiThreadedTest(new TestThread() {
         @Override
         public void runTest() throws Exception
         {
            FactoryLockTest.this.invokeMethod("testString", "#{factoryLock.test.testSameFactory()}");
         }
      },
      
      new TestThread() {
         @Override
         public void runTest() throws Exception
         {
            Thread.sleep(200);
            FactoryLockTest.this.getValue("testString", "#{factoryLock.testString}");
         }
      });
   }
   
   // This test is the same as sameFactoryLock test, except it uses a @Syncrhonized Seam component, instead of an SFSB
   @Test
   public void seamSynchronizedFactoryLock() 
       throws Exception 
   {
      multiThreadedTest(new TestThread() {
         @Override
         public void runTest() throws Exception
         {
            FactoryLockTest.this.invokeMethod("testString", "#{seamSynchronizedFactoryLock.test.testFactory()}");
         }
      },
      
      new TestThread() {
         @Override
         public void runTest() throws Exception
         {
            Thread.sleep(200);
            FactoryLockTest.this.getValue("testString", "#{seamSynchronizedFactoryLock.testString}");
         }
      });
   }
   
   private void invokeMethod(final String expected, final String el) throws Exception {
      new ComponentTest() {
         @Override
         protected void testComponents() throws Exception {
            assertEquals(expected, invokeMethod(el));
         }
     }.run();
   }
   
   private void getValue(final String expected, final String el) throws Exception {
      new ComponentTest() {
         @Override
         protected void testComponents() throws Exception {
            assertEquals(expected, getValue(el));
         }
     }.run();
   }

   @Local
   public static interface FactoryLockLocal
   {
      public String getTestString();
      public String testOtherFactory();
      public String testSameFactory();
      public void remove();
   }

   
   @Stateful
   @Scope(ScopeType.SESSION)
   @Name("factoryLock.test")
   @JndiName("java:global/test/FactoryLockTest$FactoryLockAction")
   public static class FactoryLockAction implements FactoryLockLocal
   {
      public String testOtherFactory() {
         try
         {
            Thread.sleep(500);
         }
         catch (InterruptedException e)
         {
            e.printStackTrace();
         }
         return (String)Component.getInstance("factoryLock.foo", true);
      }
      
      // gets instance produced by this component's factory 
      public String testSameFactory() {
         try
         {
            Thread.sleep(500);
         }
         catch (InterruptedException e)
         {
            e.printStackTrace();
         }
         return (String)Component.getInstance("factoryLock.testString", true);
      }
      
      @Factory(value="factoryLock.testString", scope=ScopeType.SESSION)
      public String getTestString() {
         return "testString";
      }
      @Remove
      public void remove() {}
   }
   
   // Mostly the same as FactoryLockAction, except not a SFSB
   @SuppressWarnings("serial")
   @Scope(ScopeType.SESSION)
   @Name("seamSynchronizedFactoryLock.test")
   @Synchronized(timeout=10000)
   public static class SeamSynchronizedFactoryLockAction implements Serializable
   {
      // gets instance produced by this component's factory 
      public String testFactory() {
         try
         {
            Thread.sleep(500);
         }
         catch (InterruptedException e)
         {
            e.printStackTrace();
         }
         return (String)Component.getInstance("seamSynchronizedFactoryLock.testString", true);
      }
      
      @Factory(value="seamSynchronizedFactoryLock.testString", scope=ScopeType.SESSION)
      public String getTestString() {
         return "testString";
      }
      @Remove
      public void remove() {}
   }
   
   
   @Name("factoryLock.testProducer")
   public static class TestProducer {
      @Factory(value="factoryLock.foo", scope=ScopeType.SESSION)
      public String getFoo() {
         return "foo";
      }
   }
}
