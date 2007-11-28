package org.jboss.seam.test.unit.bpm;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Status;
import javax.transaction.SystemException;

import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.bpm.PooledTaskInstanceList;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.mock.MockServletContext;
import org.jboss.seam.transaction.NoTransaction;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TaskListTest
{
   @BeforeMethod
   protected void setUp()
   {
      Lifecycle.beginApplication(new HashMap<String, Object>());
      new Initialization(new MockServletContext()).init();
      Lifecycle.mockApplication();
      installComponent(Contexts.getApplicationContext(), Actor.class);
      installComponent(Contexts.getApplicationContext(), PooledTaskInstanceList.class);
      installComponent(Contexts.getApplicationContext(), MockRolledBackTransaction.class);
      Lifecycle.beginCall();
   }

   @Test
   public void emptyPooledTaskListIfNoGroupActorIds()
   {
      Object pooledTaskInstanceList = Component.getInstance("org.jboss.seam.bpm.pooledTaskInstanceList");
      assert pooledTaskInstanceList instanceof List;
      assert ((List) pooledTaskInstanceList) == Collections.EMPTY_LIST;
   }

   @AfterMethod
   protected void tearDown()
   {
      Lifecycle.endCall();
      Lifecycle.unmockApplication();
   }

   private void installComponent(Context appContext, Class clazz)
   {
      appContext.set(Seam.getComponentName(clazz) + ".component", new Component(clazz));
   }

   public static class MockRolledBackTransaction extends NoTransaction
   {

      @Override
      public int getStatus() throws SystemException
      {
         return Status.STATUS_MARKED_ROLLBACK;
      }

   }
}
