package org.jboss.seam.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueBinding;

public class EntityManagerProxy implements EntityManager
{
   private EntityManager delegate;

   public EntityManagerProxy(EntityManager entityManager)
   {
      delegate = entityManager;
   }

   public void clear()
   {
      delegate.clear();
   }

   public void close()
   {
      delegate.close();
   }

   public boolean contains(Object entity)
   {
      return delegate.contains(entity);
   }

   public Query createNamedQuery(String name)
   {
      return delegate.createNamedQuery(name);
   }

   public Query createNativeQuery(String sql, Class clazz)
   {
      return delegate.createNativeQuery(sql, clazz);
   }

   public Query createNativeQuery(String sql, String lang)
   {
      return delegate.createNativeQuery(sql, lang);
   }

   public Query createNativeQuery(String sql)
   {
      return delegate.createNativeQuery(sql);
   }

   public Query createQuery(String ejbql)
   {
      //TODO: horrible copy/paste from HibernateSessionProxy!
      if ( ejbql.indexOf('#')>0 )
      {
         List<ValueBinding> queryParameters = new ArrayList<ValueBinding>();
         StringTokenizer ejbqlTokens = new StringTokenizer( ejbql, "#}", true );
         StringBuilder ejbqlBuilder = new StringBuilder( ejbql.length() );
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
         return delegate.createQuery(ejbql);
      }
   }

   public <T> T find(Class<T> clazz, Object id)
   {
      return delegate.find(clazz, id);
   }

   public void flush()
   {
      delegate.flush();
   }

   public Object getDelegate()
   {
      return delegate.getDelegate();
   }

   public FlushModeType getFlushMode()
   {
      return delegate.getFlushMode();
   }

   public <T> T getReference(Class<T> clazz, Object id)
   {
      return delegate.getReference(clazz, id);
   }

   public EntityTransaction getTransaction()
   {
      return delegate.getTransaction();
   }

   public boolean isOpen()
   {
      return delegate.isOpen();
   }

   public void joinTransaction()
   {
      delegate.joinTransaction();
   }

   public void lock(Object entity, LockModeType lm)
   {
      delegate.lock(entity, lm);
   }

   public <T> T merge(T entity)
   {
      return delegate.merge(entity);
   }

   public void persist(Object entity)
   {
      delegate.persist(entity);
   }

   public void refresh(Object entity)
   {
      delegate.refresh(entity);
   }

   public void remove(Object entity)
   {
      delegate.remove(entity);
   }

   public void setFlushMode(FlushModeType fm)
   {
      delegate.setFlushMode(fm);
   }
}
