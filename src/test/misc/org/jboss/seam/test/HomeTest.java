package org.jboss.seam.test;

import javax.persistence.EntityManager;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.Home;
import org.testng.annotations.Test;

public class HomeTest
{
   /**
    * Ensure that the add message methods do not trigger a null
    * pointer exception if the getEntityClass() method is overridden.
    */
   @Test
   public void testGetEntityClassOverride() {
      TestHome1 home = new TestHome1();
      // emulate @Create method
      home.create();
      home.triggerCreatedMessage();
   }
   
   /**
    * Ensure that an instance can be created when getEntityClass()
    * method is overridden.
    */
   @Test
   public void testCreateInstance() {
      TestHome1 home = new TestHome1();
      // emulate @Create method
      home.create();
      SimpleEntity entity = home.getInstance();
      assert entity != null : "Excepting a non-null instance";
      assert entity.getClass().equals(SimpleEntity.class) : "Expecting entity class to be " + SimpleEntity.class + " but got " + entity.getClass();
   }
   
   class TestHome1 extends Home<EntityManager, SimpleEntity> {

      private FacesMessages facesMessages;

      public TestHome1() {
         facesMessages = new FacesMessages();
      }
      
      public void triggerCreatedMessage() {
         createdMessage();
      }
      
      @Override
      protected void debug(Object object, Object... params)
      {
         // ignore
      }
      
      @Override
      protected FacesMessages getFacesMessages()
      {
         return facesMessages;
      }

      @Override
      public Class<SimpleEntity> getEntityClass()
      {
         return SimpleEntity.class;
      }

      @Override
      protected String getEntityName()
      {
         return "SimpleEntity";
      }

      @Override
      protected String getPersistenceContextName()
      {
         return "entityManager";
      }
      
   }
}
