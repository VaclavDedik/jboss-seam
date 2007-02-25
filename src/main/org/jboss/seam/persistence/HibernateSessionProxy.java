package org.jboss.seam.persistence;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.stat.SessionStatistics;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueBinding;

public class HibernateSessionProxy implements Session
{
   private Session delegate;

   public HibernateSessionProxy(Session session)
   {
      delegate = session;
   }

   public Transaction beginTransaction() throws HibernateException
   {
      return delegate.beginTransaction();
   }

   public void cancelQuery() throws HibernateException
   {
      delegate.cancelQuery();
   }

   public void clear()
   {
      delegate.clear();
   }

   public Connection close() throws HibernateException
   {
      return delegate.close();
   }

   public Connection connection() throws HibernateException
   {
      return delegate.connection();
   }

   public boolean contains(Object arg0)
   {
      return delegate.contains(arg0);
   }

   public Criteria createCriteria(Class arg0, String arg1)
   {
      return delegate.createCriteria(arg0, arg1);
   }

   public Criteria createCriteria(Class arg0)
   {
      return delegate.createCriteria(arg0);
   }

   public Criteria createCriteria(String arg0, String arg1)
   {
      return delegate.createCriteria(arg0, arg1);
   }

   public Criteria createCriteria(String arg0)
   {
      return delegate.createCriteria(arg0);
   }

   public Query createFilter(Object arg0, String arg1) throws HibernateException
   {
      return delegate.createFilter(arg0, arg1);
   }

   public Query createQuery(String hql) throws HibernateException
   {
      //TODO: horrible copy/paste from EntityManageProxy!
      if ( hql.indexOf('#')>0 )
      {
         List<ValueBinding> queryParameters = new ArrayList<ValueBinding>();
         StringTokenizer ejbqlTokens = new StringTokenizer( hql, "#}", true );
         StringBuilder ejbqlBuilder = new StringBuilder( hql.length() );
         while ( ejbqlTokens.hasMoreTokens() )
         {
            String token = ejbqlTokens.nextToken();
            if ( "#".equals(token) )
            {
               String expression = token + ejbqlTokens.nextToken() + ejbqlTokens.nextToken();
               queryParameters.add( Expressions.instance().createValueBinding(expression) );
               ejbqlBuilder.append(":el").append( queryParameters.size() );
            }
            else
            {
               ejbqlBuilder.append(token);
            }
         }
         Query query = delegate.createQuery( ejbqlBuilder.toString() );
         for (int i=1; i<=queryParameters.size(); i++)
         {
            query.setParameter( "el" + i, queryParameters.get(i-1).getValue() );
         }
         return query;
      }
      else
      {
         return delegate.createQuery(hql);
      }
   }

   public SQLQuery createSQLQuery(String arg0) throws HibernateException
   {
      return delegate.createSQLQuery(arg0);
   }

   public void delete(Object arg0) throws HibernateException
   {
      delegate.delete(arg0);
   }

   public void delete(String arg0, Object arg1) throws HibernateException
   {
      delegate.delete(arg0, arg1);
   }

   public void disableFilter(String arg0)
   {
      delegate.disableFilter(arg0);
   }

   public Connection disconnect() throws HibernateException
   {
      return delegate.disconnect();
   }

   public Filter enableFilter(String arg0)
   {
      return delegate.enableFilter(arg0);
   }

   public void evict(Object arg0) throws HibernateException
   {
      delegate.evict(arg0);
   }

   public void flush() throws HibernateException
   {
      delegate.flush();
   }

   public Object get(Class arg0, Serializable arg1, LockMode arg2) throws HibernateException
   {
      return delegate.get(arg0, arg1, arg2);
   }

   public Object get(Class arg0, Serializable arg1) throws HibernateException
   {
      return delegate.get(arg0, arg1);
   }

   public Object get(String arg0, Serializable arg1, LockMode arg2) throws HibernateException
   {
      return delegate.get(arg0, arg1, arg2);
   }

   public Object get(String arg0, Serializable arg1) throws HibernateException
   {
      return delegate.get(arg0, arg1);
   }

   public CacheMode getCacheMode()
   {
      return delegate.getCacheMode();
   }

   public LockMode getCurrentLockMode(Object arg0) throws HibernateException
   {
      return delegate.getCurrentLockMode(arg0);
   }

   public Filter getEnabledFilter(String arg0)
   {
      return delegate.getEnabledFilter(arg0);
   }

   public EntityMode getEntityMode()
   {
      return delegate.getEntityMode();
   }

   public String getEntityName(Object arg0) throws HibernateException
   {
      return delegate.getEntityName(arg0);
   }

   public FlushMode getFlushMode()
   {
      return delegate.getFlushMode();
   }

   public Serializable getIdentifier(Object arg0) throws HibernateException
   {
      return delegate.getIdentifier(arg0);
   }

   public Query getNamedQuery(String arg0) throws HibernateException
   {
      return delegate.getNamedQuery(arg0);
   }

   public Session getSession(EntityMode arg0)
   {
      return delegate.getSession(arg0);
   }

   public SessionFactory getSessionFactory()
   {
      return delegate.getSessionFactory();
   }

   public SessionStatistics getStatistics()
   {
      return delegate.getStatistics();
   }

   public Transaction getTransaction()
   {
      return delegate.getTransaction();
   }

   public boolean isConnected()
   {
      return delegate.isConnected();
   }

   public boolean isDirty() throws HibernateException
   {
      return delegate.isDirty();
   }

   public boolean isOpen()
   {
      return delegate.isOpen();
   }

   public Object load(Class arg0, Serializable arg1, LockMode arg2) throws HibernateException
   {
      return delegate.load(arg0, arg1, arg2);
   }

   public Object load(Class arg0, Serializable arg1) throws HibernateException
   {
      return delegate.load(arg0, arg1);
   }

   public void load(Object arg0, Serializable arg1) throws HibernateException
   {
      delegate.load(arg0, arg1);
   }

   public Object load(String arg0, Serializable arg1, LockMode arg2) throws HibernateException
   {
      return delegate.load(arg0, arg1, arg2);
   }

   public Object load(String arg0, Serializable arg1) throws HibernateException
   {
      return delegate.load(arg0, arg1);
   }

   public void lock(Object arg0, LockMode arg1) throws HibernateException
   {
      delegate.lock(arg0, arg1);
   }

   public void lock(String arg0, Object arg1, LockMode arg2) throws HibernateException
   {
      delegate.lock(arg0, arg1, arg2);
   }

   public Object merge(Object arg0) throws HibernateException
   {
      return delegate.merge(arg0);
   }

   public Object merge(String arg0, Object arg1) throws HibernateException
   {
      return delegate.merge(arg0, arg1);
   }

   public void persist(Object arg0) throws HibernateException
   {
      delegate.persist(arg0);
   }

   public void persist(String arg0, Object arg1) throws HibernateException
   {
      delegate.persist(arg0, arg1);
   }

   public void reconnect() throws HibernateException
   {
      throw new UnsupportedOperationException("deprecated");
   }

   public void reconnect(Connection arg0) throws HibernateException
   {
      delegate.reconnect(arg0);
   }

   public void refresh(Object arg0, LockMode arg1) throws HibernateException
   {
      delegate.refresh(arg0, arg1);
   }

   public void refresh(Object arg0) throws HibernateException
   {
      delegate.refresh(arg0);
   }

   public void replicate(Object arg0, ReplicationMode arg1) throws HibernateException
   {
      delegate.replicate(arg0, arg1);
   }

   public void replicate(String arg0, Object arg1, ReplicationMode arg2) throws HibernateException
   {
      delegate.replicate(arg0, arg1, arg2);
   }

   public Serializable save(Object arg0) throws HibernateException
   {
      return delegate.save(arg0);
   }

   public Serializable save(String arg0, Object arg1) throws HibernateException
   {
      return delegate.save(arg0, arg1);
   }

   public void saveOrUpdate(Object arg0) throws HibernateException
   {
      delegate.saveOrUpdate(arg0);
   }

   public void saveOrUpdate(String arg0, Object arg1) throws HibernateException
   {
      delegate.saveOrUpdate(arg0, arg1);
   }

   public void setCacheMode(CacheMode arg0)
   {
      delegate.setCacheMode(arg0);
   }

   public void setFlushMode(FlushMode arg0)
   {
      delegate.setFlushMode(arg0);
   }

   public void setReadOnly(Object arg0, boolean arg1)
   {
      delegate.setReadOnly(arg0, arg1);
   }

   public void update(Object arg0) throws HibernateException
   {
      delegate.update(arg0);
   }

   public void update(String arg0, Object arg1) throws HibernateException
   {
      delegate.update(arg0, arg1);
   }
}
