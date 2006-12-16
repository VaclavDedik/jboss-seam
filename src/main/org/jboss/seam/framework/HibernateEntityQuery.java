package org.jboss.seam.framework;

import java.util.List;

import org.hibernate.Session;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Transactional;

public class HibernateEntityQuery extends Query
{
   private Session session;

   private List resultList;
   private Object singleResult;
   private Integer resultCount;
   
   private Boolean cacheable;
   private String cacheRegion;
   private Integer fetchSize;
   
   @Override
   public void validate()
   {
      super.validate();
      if ( getSession()==null )
      {
         throw new IllegalStateException("session is null");
      }
   }

   @Transactional
   @Override
   public List getResultList()
   {
      if (resultList==null || isAnyParameterDirty())
      {
         org.hibernate.Query query = createQuery();
         resultList = query==null ? null : query.list();
      }
      return resultList;
   }
   
   @Transactional
   @Override
   public Object getSingleResult()
   {
      if (singleResult==null || isAnyParameterDirty())
      {
         org.hibernate.Query query = createQuery();
         singleResult = query==null ? 
               null : query.uniqueResult();
      }
      return singleResult;
   }

   @Transactional
   @Override
   public Long getResultCount()
   {
      if (resultCount==null || isAnyParameterDirty())
      {
         org.hibernate.Query query = createCountQuery();
         resultCount = query==null ? 
               null : (Integer) query.uniqueResult();
      }
      return resultCount.longValue();
   }

   @Override
   public void refresh()
   {
      super.refresh();
      resultCount = null;
      resultList = null;
      singleResult = null;
   }
   
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

   protected org.hibernate.Query createQuery()
   {
      parseEjbql();
      
      evaluateAllParameters();
      
      org.hibernate.Query query = getSession().createQuery( getRenderedEjbql() );
      setParameters( query, getQueryParameterValues(), 0 );
      setParameters( query, getRestrictionParameterValues(), getQueryParameterValues().size() );
      if ( getFirstResult()!=null) query.setFirstResult( getFirstResult() );
      if ( getMaxResults()!=null) query.setMaxResults( getMaxResults() );
      if ( getCacheable()!=null ) query.setCacheable( getCacheable() );
      if ( getCacheRegion()!=null ) query.setCacheRegion( getCacheRegion() );
      if ( getFetchSize()!=null ) query.setFetchSize( getFetchSize() );
      return query;
   }
   
   protected org.hibernate.Query createCountQuery()
   {
      parseEjbql();
      
      evaluateAllParameters();
      
      org.hibernate.Query query = getSession().createQuery( getCountEjbql() );
      setParameters( query, getQueryParameterValues(), 0 );
      setParameters( query, getRestrictionParameterValues(), getQueryParameterValues().size() );
      return query;
   }

   private void setParameters(org.hibernate.Query query, List<Object> parameters, int start)
   {
      for (int i=0; i<parameters.size(); i++)
      {
         Object parameterValue = parameters.get(i);
         if ( isRestrictionParameterSet(parameterValue) )
         {
            query.setParameter( "p" + (start + i), parameterValue );
         }
      }
   }

   protected Boolean getCacheable()
   {
      return cacheable;
   }

   protected void setCacheable(Boolean cacheable)
   {
      this.cacheable = cacheable;
   }

   protected String getCacheRegion()
   {
      return cacheRegion;
   }

   protected void setCacheRegion(String cacheRegion)
   {
      this.cacheRegion = cacheRegion;
   }

   protected Integer getFetchSize()
   {
      return fetchSize;
   }

   protected void setFetchSize(Integer fetchSize)
   {
      this.fetchSize = fetchSize;
   }

}
