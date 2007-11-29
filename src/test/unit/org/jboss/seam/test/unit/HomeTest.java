package org.jboss.seam.test.unit;

import javax.persistence.EntityManager;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.framework.HibernateEntityHome;
import org.jboss.seam.framework.Home;
import org.jboss.seam.test.unit.entity.SimpleEntity;
import org.testng.annotations.Test;

public class HomeTest
{
   /**
    * The only condition under which the getEntityClass() should be able to
    * resolve the entity correctly is if the entityClass is provided or the Home
    * implementation extends either EntityHome or HibernateEntityHome and
    * provides a type parameter in the class definition
    */
   @Test
   public void testReadEntityClassFromTypeParameter()
   {
      EntityHome typelessHome = new EntityHome();
      typelessHome.setEntityClass(SimpleEntity.class);
      assert typelessHome.getEntityClass() == SimpleEntity.class;
      
      assert new SimpleEntityHomeWithType().getEntityClass() == SimpleEntity.class;

      try
      {
         Class ec = new SimpleEntityHomeSansType().getEntityClass();
         assert false : "Not expecting to have resolved a type, but got " + ec;
      }
      catch (IllegalArgumentException e)
      {
      }

      try
      {
         Class ec = new EntityHome<SimpleEntity>().getEntityClass();
         assert false : "Not expecting to have resolved a type, but got " + ec;
      }
      catch (IllegalArgumentException e)
      {
      }

      assert new SimpleHibernateEntityHomeWithType().getEntityClass() == SimpleEntity.class;

      try
      {
         Class ec = new SimpleHibernateEntityHomeSansType().getEntityClass();
         assert false : "Not expecting to have resolved a type, but got " + ec;
      }
      catch (IllegalArgumentException e)
      {
      }

      try
      {
         Class ec = new HibernateEntityHome<SimpleEntity>().getEntityClass();
         assert false : "Not expecting to have resolved a type, but got " + ec;
      }
      catch (IllegalArgumentException e)
      {
      }

      assert new Home<EntityManager, SimpleEntity>()
      {

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

      }.getEntityClass() == SimpleEntity.class;
   }
   
   /**
    * Ensure that the add message methods do not trigger a null pointer
    * exception if the getEntityClass() method is overridden.
    */
   @Test
   public void testGetEntityClassOverride() {
      SimpleEntityHomeWithMessageStubs home = new SimpleEntityHomeWithMessageStubs();
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
      SimpleEntityHomeWithMessageStubs home = new SimpleEntityHomeWithMessageStubs();
      // emulate @Create method
      home.create();
      SimpleEntity entity = home.getInstance();
      assert entity != null : "Excepting a non-null instance";
      assert entity.getClass().equals(SimpleEntity.class) : "Expecting entity class to be " + SimpleEntity.class + " but got " + entity.getClass();
   }
   
   public class SimpleEntityHomeSansType extends EntityHome {}
   
   public class SimpleEntityHomeWithType extends EntityHome<SimpleEntity> {}
   
   public class SimpleHibernateEntityHomeSansType extends HibernateEntityHome {}
   
   public class SimpleHibernateEntityHomeWithType extends HibernateEntityHome<SimpleEntity> {}
   
   public class SimpleEntityHomeWithMessageStubs extends Home<EntityManager, SimpleEntity> {

      private FacesMessages facesMessages;

      public SimpleEntityHomeWithMessageStubs() {
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
