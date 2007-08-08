package org.jboss.seam.persistence;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.transaction.Synchronization;

import org.hibernate.EntityMode;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.StaleStateException;
import org.hibernate.TransientObjectException;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.VersionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

/**
 * Support for non-standardized features of Hibernate, when
 * used as the JPA persistence provider.
 * 
 * @author Gavin King
 *
 */
@Name("org.jboss.seam.persistence.persistenceProvider")
@Scope(ScopeType.STATELESS)
@BypassInterceptors
@Install(precedence=FRAMEWORK, classDependencies="org.hibernate.Session", genericDependencies=ManagedPersistenceContext.class)
public class HibernatePersistenceProvider extends PersistenceProvider
{
   
   private static Log log = Logging.getLog(HibernatePersistenceProvider.class);

   private static Constructor FULL_TEXT_SESSION_PROXY_CONSTRUCTOR;
   private static Method FULL_TEXT_SESSION_CONSTRUCTOR;
   static 
   {
      try
      {
         Class searchClass = Class.forName("org.hibernate.search.Search");
         FULL_TEXT_SESSION_CONSTRUCTOR = searchClass.getDeclaredMethod("createFullTextSession", Session.class);
         Class fullTextSessionProxyClass = Class.forName("org.jboss.seam.persistence.FullTextHibernateSessionProxy");
         Class fullTextSessionClass = Class.forName("org.hibernate.search.FullTextSession");
         FULL_TEXT_SESSION_PROXY_CONSTRUCTOR = fullTextSessionProxyClass.getDeclaredConstructor(fullTextSessionClass);
         log.debug("Hibernate Search is available :-)");
      }
      catch (Exception e)
      {
         log.debug("no Hibernate Search, sorry :-(", e);
      }
   }
   
   /**
    * Wrap the Hibernate Session in a proxy that supports HQL
    * EL interpolation and implements FullTextSession if Hibernate
    * Search is available in the classpath.
    */
   static Session proxySession(Session session) throws Exception
   {
      if (FULL_TEXT_SESSION_PROXY_CONSTRUCTOR==null)
      {
         return new HibernateSessionProxy(session);
      }
      else
      {
         return (Session) FULL_TEXT_SESSION_PROXY_CONSTRUCTOR.newInstance( FULL_TEXT_SESSION_CONSTRUCTOR.invoke(null, session) );
      }
   }
   
   /**
    * Wrap the delegate Hibernate Session in a proxy that supports HQL
    * EL interpolation and implements FullTextSession if Hibernate
    * Search is available in the classpath.
    */
   @Override
   public Object proxyDelegate(Object delegate)
   {
      try
      {
         return proxySession( (Session) delegate );
      }
      catch (Exception e)
      {
         throw new RuntimeException("could not proxy delegate", e);
      }
   }
   
   @Override
   public void setFlushModeManual(EntityManager entityManager)
   {
      getSession(entityManager).setFlushMode(FlushMode.MANUAL);
   }

   @Override
   public boolean isDirty(EntityManager entityManager)
   {
      return getSession(entityManager).isDirty();
   }

   @Override
   public Object getId(Object bean, EntityManager entityManager) 
   {
      try
      {
         return getSession(entityManager).getIdentifier(bean);
      }
      catch (TransientObjectException e) 
      {
         return super.getId(bean, entityManager);
      }
   }

   @Override
   public Object getVersion(Object bean, EntityManager entityManager) 
   {
      return getVersion( bean, getSession(entityManager) );
   }
   
   @Override
   public void checkVersion(Object bean, EntityManager entityManager, Object oldVersion, Object version)
   {
      checkVersion(bean, getSession(entityManager), oldVersion, version);
   }

   @Override
   public void enableFilter(Filter f, EntityManager entityManager)
   {
      org.hibernate.Filter filter = getSession(entityManager).enableFilter( f.getName() );
      for ( Map.Entry<String, ValueExpression> me: f.getParameters().entrySet() )
      {
         filter.setParameter( me.getKey(), me.getValue().getValue() );
      }
      filter.validate();
   }
   
   @Override
   public boolean registerSynchronization(Synchronization sync, EntityManager entityManager)
   {
      //TODO: just make sure that a Hibernate JPA EntityTransaction
      //      delegates to the Hibernate Session transaction
      getSession(entityManager).getTransaction().registerSynchronization(sync);
      return true;
   }
   
   @Override
   public FlushModeType getRenderFlushMode()
   {
      return FlushModeType.MANUAL;
   }
   
   private Session getSession(EntityManager entityManager)
   {
      return (Session) entityManager.getDelegate();
   }

   public static void checkVersion(Object value, Session session, Object oldVersion, Object version)
   {
      ClassMetadata classMetadata = getClassMetadata(value, session);
      VersionType versionType = (VersionType) classMetadata.getPropertyTypes()[ classMetadata.getVersionProperty() ];
      if ( !versionType.isEqual(oldVersion, version) )
      {
         throw new StaleStateException("current database version number does not match passivated version number");
      }
   }

   public static Object getVersion(Object value, Session session)
   {
      ClassMetadata classMetadata = getClassMetadata(value, session);
      return classMetadata!=null && classMetadata.isVersioned() ? 
               classMetadata.getVersion(value, EntityMode.POJO) : null;
   }

   private static ClassMetadata getClassMetadata(Object value, Session session)
   {
      Class entityClass = Seam.getEntityClass( value.getClass() );
      ClassMetadata classMetadata = null;
      if (entityClass!=null)
      {
         classMetadata = session.getSessionFactory().getClassMetadata(entityClass);
         if (classMetadata==null)
         {
            throw new IllegalArgumentException( 
                     "Could not find ClassMetadata object for entity class: " + 
                     entityClass.getName() 
                  );
         }
      }
      return classMetadata;
   }
   
   public String getName(Object bean, EntityManager entityManager)
   {
      return getSession(entityManager).getEntityName(bean);
   }

}
