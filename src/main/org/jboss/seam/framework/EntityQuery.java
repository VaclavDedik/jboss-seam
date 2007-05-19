package org.jboss.seam.framework;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.persistence.QueryParser;

/**
 * A Query object for JPA.
 * 
 * @author Gavin King
 *
 */
public class EntityQuery extends Query<EntityManager>
{

   private List resultList;
   private Object singleResult;
   private Long resultCount;
   private Map<String, String> hints;

   @Override
   public void validate()
   {
      super.validate();
      if ( getEntityManager()==null )
      {
         throw new IllegalStateException("entityManager is null");
      }
   }
   
   @Override
   @Transactional
   public boolean isNextExists()
   {
      return resultList!=null && 
            resultList.size() > getMaxResults();
   }


   @Transactional
   @Override
   public List getResultList()
   {
      if ( isAnyParameterDirty() )
      {
         refresh();
      }
      initResultList();
      return truncResultList(resultList);
   }

   private void initResultList()
   {
      if (resultList==null)
      {
         javax.persistence.Query query = createQuery();
         resultList = query==null ? null : query.getResultList();
      }
   }
   
   @Transactional
   @Override
   public Object getSingleResult()
   {
      if (isAnyParameterDirty())
      {
         refresh();
      }
      initSingleResult();
      return singleResult;
   }

   private void initSingleResult()
   {
      if ( singleResult==null)
      {
         javax.persistence.Query query = createQuery();
         singleResult = query==null ? 
               null : query.getSingleResult();
      }
   }

   @Transactional
   @Override
   public Long getResultCount()
   {
      if (isAnyParameterDirty())
      {
         refresh();
      }
      initResultCount();
      return resultCount;
   }

   private void initResultCount()
   {
      if ( resultCount==null )
      {
         javax.persistence.Query query = createCountQuery();
         resultCount = query==null ? 
               null : (Long) query.getSingleResult();
      }
   }

   @Override
   public void refresh()
   {
      super.refresh();
      resultCount = null;
      resultList = null;
      singleResult = null;
   }
   
   public EntityManager getEntityManager()
   {
      return getPersistenceContext();
   }

   public void setEntityManager(EntityManager entityManager)
   {
      setPersistenceContext(entityManager);
   }

   @Override
   protected String getPersistenceContextName()
   {
      return "entityManager";
   }
   
   protected javax.persistence.Query createQuery()
   {
      parseEjbql();
      
      evaluateAllParameters();
      
      getEntityManager().joinTransaction();
      javax.persistence.Query query = getEntityManager().createQuery( getRenderedEjbql() );
      setParameters( query, getQueryParameterValues(), 0 );
      setParameters( query, getRestrictionParameterValues(), getQueryParameterValues().size() );
      if ( getFirstResult()!=null) query.setFirstResult( getFirstResult() );
      if ( getMaxResults()!=null) query.setMaxResults( getMaxResults()+1 ); //add one, so we can tell if there is another page
      if ( getHints()!=null )
      {
         for ( Map.Entry<String, String> me: getHints().entrySet() )
         {
            query.setHint(me.getKey(), me.getValue());
         }
      }
      return query;
   }
   
   protected javax.persistence.Query createCountQuery()
   {
      parseEjbql();

      evaluateAllParameters();

      getEntityManager().joinTransaction();
      javax.persistence.Query query = getEntityManager().createQuery( getCountEjbql() );
      setParameters( query, getQueryParameterValues(), 0 );
      setParameters( query, getRestrictionParameterValues(), getQueryParameterValues().size() );
      return query;
   }

   private void setParameters(javax.persistence.Query query, List<Object> parameters, int start)
   {
      for (int i=0; i<parameters.size(); i++)
      {
         Object parameterValue = parameters.get(i);
         if ( isRestrictionParameterSet(parameterValue) )
         {
            query.setParameter( QueryParser.getParameterName(start + i), parameterValue );
         }
      }
   }

   public Map<String, String> getHints()
   {
      return hints;
   }

   public void setHints(Map<String, String> hints)
   {
      this.hints = hints;
   }

}
