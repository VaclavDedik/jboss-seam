package org.jboss.seam.framework;

import java.io.Serializable;

import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.jboss.seam.Component;

/**
 * Superclass for controller objects that perform
 * persistence operations using Hibernate. Adds
 * convenience methods for access to the Hibernate
 * Session object.
 * 
 * @author Gavin King
 *
 */
public class HibernateEntityController extends Controller
{
   private Session session;
   
   public Session getSession()
   {
      if (session==null)
      {
         session = (Session) Component.getInstance("session");
      }
      return session;
   }
   
   public void setSession(Session session)
   {
      this.session = session;
   }

   protected Criteria createCriteria(Class clazz)
   {
      return session.createCriteria(clazz);
   }

   protected Query createQuery(String hql) throws HibernateException
   {
      return session.createQuery(hql);
   }

   protected SQLQuery createSQLQuery(String sql) throws HibernateException
   {
      return session.createSQLQuery(sql);
   }

   protected void delete(Object entity) throws HibernateException
   {
      session.delete(entity);
   }

   protected Filter enableFilter(String name)
   {
      return session.enableFilter(name);
   }

   protected void flush() throws HibernateException
   {
      session.flush();
   }

   protected <T> T get(Class<T> clazz, Serializable id, LockMode lockMode) throws HibernateException
   {
      return (T) session.get(clazz, id, lockMode);
   }

   protected <T> T get(Class<T> clazz, Serializable id) throws HibernateException
   {
      return (T) session.get(clazz, id);
   }

   protected Query getNamedQuery(String name) throws HibernateException
   {
      return session.getNamedQuery(name);
   }

   protected <T> T load(Class<T> clazz, Serializable id, LockMode lockMode) throws HibernateException
   {
      return (T) session.load(clazz, id, lockMode);
   }

   protected <T> T load(Class<T> clazz, Serializable id) throws HibernateException
   {
      return (T) session.load(clazz, id);
   }

   protected void lock(Object entity, LockMode lockMode) throws HibernateException
   {
      session.lock(entity, lockMode);
   }

   protected <T> T merge(T entity) throws HibernateException
   {
      return (T) session.merge(entity);
   }

   protected void persist(Object entity) throws HibernateException
   {
      session.persist(entity);
   }

   protected void refresh(Object entity, LockMode lockMode) throws HibernateException
   {
      session.refresh(entity, lockMode);
   }

   protected void refresh(Object entity) throws HibernateException
   {
      session.refresh(entity);
   }

}
