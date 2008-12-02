package org.jboss.seam.persistence;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.engine.ActionQueue;
import org.hibernate.engine.EntityEntry;
import org.hibernate.engine.EntityKey;
import org.hibernate.engine.PersistenceContext;
import org.hibernate.engine.QueryParameters;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
import org.hibernate.event.EventListeners;
import org.hibernate.event.EventSource;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.jdbc.Batcher;
import org.hibernate.jdbc.JDBCContext;
import org.hibernate.loader.custom.CustomQuery;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;
import org.jboss.seam.util.DelegatingInvocationHandler;

/**
 * Proxies the Session, and implements EL interpolation
 * in HQL. Needs to implement SessionImplementor because
 * DetachedCriteria casts the Session to SessionImplementor.
 * 
 * @author Gavin King
 * @author Emmanuel Bernard
 * @author Shane Bryzak
 *
 */
public class HibernateSessionProxy extends DelegatingInvocationHandler<Session>
{        
   /**
    * Don't use that constructor directly, use HibernatePersistenceProvider.proxySession()
    */
   public HibernateSessionProxy(Session session)
   {
      super(session);
   }  

   public Query createQuery(String hql) throws HibernateException
   {
      if ( hql.indexOf('#')>0 )
      {
         QueryParser qp = new QueryParser(hql);
         Query query = getDelegate().createQuery( qp.getEjbql() );
         for (int i=0; i<qp.getParameterValueBindings().size(); i++)
         {
            query.setParameter( 
                     QueryParser.getParameterName(i), 
                     qp.getParameterValueBindings().get(i).getValue() 
                  );
         }
         return query;
      }
      else
      {
         return getDelegate().createQuery(hql);
      }
   }

   public void reconnect() throws HibernateException
   {
      throw new UnsupportedOperationException("deprecated");
   }
   
   private SessionImplementor getDelegateSessionImplementor()
   {
      return (SessionImplementor) getDelegate();
   }

   private EventSource getDelegateEventSource()
   {
      return (EventSource) getDelegate();
   }

   public void afterScrollOperation()
   {
      getDelegateSessionImplementor().afterScrollOperation();
   }

   public void afterTransactionCompletion(boolean arg0, Transaction arg1)
   {
      getDelegateSessionImplementor().afterTransactionCompletion(arg0, arg1);
   }

   public void beforeTransactionCompletion(Transaction arg0)
   {
      getDelegateSessionImplementor().beforeTransactionCompletion(arg0);
   }

   public String bestGuessEntityName(Object arg0)
   {
      return getDelegateSessionImplementor().bestGuessEntityName(arg0);
   }

   public int executeNativeUpdate(NativeSQLQuerySpecification arg0, QueryParameters arg1) throws HibernateException
   {
      return getDelegateSessionImplementor().executeNativeUpdate(arg0, arg1);
   }

   public int executeUpdate(String arg0, QueryParameters arg1) throws HibernateException
   {
      return getDelegateSessionImplementor().executeUpdate(arg0, arg1);
   }

   public Batcher getBatcher()
   {
      return getDelegateSessionImplementor().getBatcher();
   }

   public Serializable getContextEntityIdentifier(Object arg0)
   {
      return getDelegateSessionImplementor().getContextEntityIdentifier(arg0);
   }

   public int getDontFlushFromFind()
   {
      return getDelegateSessionImplementor().getDontFlushFromFind();
   }

   public Map getEnabledFilters()
   {
      return getDelegateSessionImplementor().getEnabledFilters();
   }

   public EntityPersister getEntityPersister(String arg0, Object arg1) throws HibernateException
   {
      return getDelegateSessionImplementor().getEntityPersister(arg0, arg1);
   }

   public Object getEntityUsingInterceptor(EntityKey arg0) throws HibernateException
   {
      return getDelegateSessionImplementor().getEntityUsingInterceptor(arg0);
   }

   public SessionFactoryImplementor getFactory()
   {
      return getDelegateSessionImplementor().getFactory();
   }

   public String getFetchProfile()
   {
      return getDelegateSessionImplementor().getFetchProfile();
   }

   public Type getFilterParameterType(String arg0)
   {
      return getDelegateSessionImplementor().getFilterParameterType(arg0);
   }

   public Object getFilterParameterValue(String arg0)
   {
      return getDelegateSessionImplementor().getFilterParameterValue(arg0);
   }

   public Interceptor getInterceptor()
   {
      return getDelegateSessionImplementor().getInterceptor();
   }

   public JDBCContext getJDBCContext()
   {
      return getDelegateSessionImplementor().getJDBCContext();
   }

   public EventListeners getListeners()
   {
      return getDelegateSessionImplementor().getListeners();
   }

   public Query getNamedSQLQuery(String arg0)
   {
      return getDelegateSessionImplementor().getNamedSQLQuery(arg0);
   }

   public PersistenceContext getPersistenceContext()
   {
      return getDelegateSessionImplementor().getPersistenceContext();
   }

   public long getTimestamp()
   {
      return getDelegateSessionImplementor().getTimestamp();
   }

   public String guessEntityName(Object arg0) throws HibernateException
   {
      return getDelegateSessionImplementor().guessEntityName(arg0);
   }

   public Object immediateLoad(String arg0, Serializable arg1) throws HibernateException
   {
      return getDelegateSessionImplementor().immediateLoad(arg0, arg1);
   }

   public void initializeCollection(PersistentCollection arg0, boolean arg1) throws HibernateException
   {
      getDelegateSessionImplementor().initializeCollection(arg0, arg1);
   }

   public Object instantiate(String arg0, Serializable arg1) throws HibernateException
   {
      return getDelegateSessionImplementor().instantiate(arg0, arg1);
   }

   public Object internalLoad(String arg0, Serializable arg1, boolean arg2, boolean arg3) throws HibernateException
   {
      return getDelegateSessionImplementor().internalLoad(arg0, arg1, arg2, arg3);
   }

   public boolean isClosed()
   {
      return getDelegateSessionImplementor().isClosed();
   }

   public boolean isEventSource()
   {
      return getDelegateSessionImplementor().isEventSource();
   }

   public boolean isTransactionInProgress()
   {
      return getDelegateSessionImplementor().isTransactionInProgress();
   }

   public Iterator iterate(String arg0, QueryParameters arg1) throws HibernateException
   {
      return getDelegateSessionImplementor().iterate(arg0, arg1);
   }

   public Iterator iterateFilter(Object arg0, String arg1, QueryParameters arg2) throws HibernateException
   {
      return getDelegateSessionImplementor().iterateFilter(arg0, arg1, arg2);
   }

   public List list(CriteriaImpl arg0)
   {
      return getDelegateSessionImplementor().list(arg0);
   }

   public List list(NativeSQLQuerySpecification arg0, QueryParameters arg1) throws HibernateException
   {
      return getDelegateSessionImplementor().list(arg0, arg1);
   }

   public List list(String arg0, QueryParameters arg1) throws HibernateException
   {
      return getDelegateSessionImplementor().list(arg0, arg1);
   }

   public List listCustomQuery(CustomQuery arg0, QueryParameters arg1) throws HibernateException
   {
      return getDelegateSessionImplementor().listCustomQuery(arg0, arg1);
   }

   public List listFilter(Object arg0, String arg1, QueryParameters arg2) throws HibernateException
   {
      return getDelegateSessionImplementor().listFilter(arg0, arg1, arg2);
   }

   public ScrollableResults scroll(CriteriaImpl arg0, ScrollMode arg1)
   {
      return getDelegateSessionImplementor().scroll(arg0, arg1);
   }

   public ScrollableResults scroll(NativeSQLQuerySpecification arg0, QueryParameters arg1) throws HibernateException
   {
      return getDelegateSessionImplementor().scroll(arg0, arg1);
   }

   public ScrollableResults scroll(String arg0, QueryParameters arg1) throws HibernateException
   {
      return getDelegateSessionImplementor().scroll(arg0, arg1);
   }

   public ScrollableResults scrollCustomQuery(CustomQuery arg0, QueryParameters arg1) throws HibernateException
   {
      return getDelegateSessionImplementor().scrollCustomQuery(arg0, arg1);
   }

   public void setAutoClear(boolean arg0)
   {
      getDelegateSessionImplementor().setAutoClear(arg0);
   }

   public void setFetchProfile(String arg0)
   {
      getDelegateSessionImplementor().setFetchProfile(arg0);
   }

	public ActionQueue getActionQueue() {
		return getDelegateEventSource().getActionQueue();
	}

	public Object instantiate(EntityPersister entityPersister, Serializable serializable) throws HibernateException {
		return getDelegateEventSource().instantiate( entityPersister, serializable );
	}

	public void forceFlush(EntityEntry entityEntry) throws HibernateException {
		getDelegateEventSource().forceFlush( entityEntry );
	}

	public void merge(String s, Object o, Map map) throws HibernateException {
		getDelegateEventSource().merge( s, o, map );
	}

	public void persist(String s, Object o, Map map) throws HibernateException {
		getDelegateEventSource().persist( s, o, map );
	}

	public void persistOnFlush(String s, Object o, Map map) {
		getDelegateEventSource().persistOnFlush( s, o, map );
	}

	public void refresh(Object o, Map map) throws HibernateException {
		getDelegateEventSource().refresh( o, map );
	}

	public void saveOrUpdateCopy(String s, Object o, Map map) throws HibernateException {
		getDelegateEventSource().saveOrUpdateCopy( s, o , map );
	}

	public void delete(String s, Object o, boolean b, Set set) {
		getDelegateEventSource().delete( s, o, b, set );
	}
}
