package org.jboss.seam.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.model.DataModel;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueBinding;
import org.jboss.seam.jsf.ListDataModel;

public class Query
{
   private String ejbql;
   private Integer firstResult;
   private Integer maxResults;
   private EntityManager entityManager;
   private Map<String, String> hints;
   private List<String> restrictions;
   private String order;
   
   private List resultList;
   private DataModel dataModel;
   private Object singleResult;
   private Long resultCount;
   
   private String parsedEjbql;
   private List<ValueBinding> queryParameters;
   private List<String> parsedRestrictions;
   private List<ValueBinding> restrictionParameters;

   @Transactional
   public List getResultList()
   {
      if (resultList==null)
      {
         javax.persistence.Query query = createQuery();
         resultList = query==null ? null : query.getResultList();
      }
      return resultList;
   }
   
   @Transactional
   public Object getSingleResult()
   {
      if (singleResult==null)
      {
         javax.persistence.Query query = createQuery();
         singleResult = query==null ? 
               null : query.getSingleResult();
      }
      return singleResult;
   }

   @Transactional
   public Long getResultCount()
   {
      if (resultCount==null)
      {
         javax.persistence.Query query = createCountQuery();
         resultCount = query==null ? 
               null : (Long) query.getSingleResult();
      }
      return (Long) resultCount;
   }

   @Transactional
   public DataModel getDataModel()
   {
      if (dataModel==null)
      {
         dataModel = new ListDataModel( getResultList() );
      }
      return dataModel;
   }
   
   protected javax.persistence.Query createQuery()
   {
      parseEjbql();
      
      getEntityManager().joinTransaction();
      javax.persistence.Query query = getEntityManager().createQuery( getRenderedEjbql() );
      setParameters(query, queryParameters, 0);
      setParameters(query, restrictionParameters, queryParameters.size());
      if (firstResult!=null) query.setFirstResult(firstResult);
      if (maxResults!=null) query.setMaxResults(maxResults);
      if (hints!=null)
      {
         for (Map.Entry<String, String> me: hints.entrySet())
         {
            query.setHint(me.getKey(), me.getValue());
         }
      }
      return query;
   }
   
   protected javax.persistence.Query createCountQuery()
   {
      parseEjbql();
      
      getEntityManager().joinTransaction();
      
      String countEjbql = getCountEjbql();
      
      javax.persistence.Query query = getEntityManager().createQuery(countEjbql);
      setParameters(query, queryParameters, 0);
      setParameters(query, restrictionParameters, queryParameters.size());
      return query;
   }

   private void setParameters(javax.persistence.Query query, List<ValueBinding> parameters, int start)
   {
      for (int i=0; i<parameters.size(); i++)
      {
         Object parameterValue = parameters.get(i).getValue();
         if (parameterValue!=null)
         {
            query.setParameter(start++, parameterValue);
         }
      }
   }

   public void refresh()
   {
      dataModel = null;
      resultCount = null;
      resultList = null;
      singleResult = null;
   }
   
   public void last()
   {
      firstResult = (int) getLastFirstResult();
      dataModel = null;
   }
   
   public void next()
   {
      firstResult = getNextFirstResult();
      dataModel = null;
   }

   public void previous()
   {
      firstResult = getPreviousFirstResult();
      dataModel = null;
   }
   
   public void first()
   {
      firstResult = 0;
      dataModel = null;
   }

   @Transactional
   public long getLastFirstResult()
   {
      return ( getResultCount() / maxResults ) * maxResults;
   }
   
   public int getNextFirstResult()
   {
      return ( firstResult==null ? 0 : firstResult ) + maxResults;
   }

   public int getPreviousFirstResult()
   {
      if (maxResults>firstResult) 
      {
         return 0;
      }
      else
      {
         return firstResult - maxResults;
      }
   }
   
   protected void parseEjbql()
   {
      if (parsedEjbql==null)
      {
         
         queryParameters = new ArrayList<ValueBinding>();
         StringTokenizer ejbqlTokens = new StringTokenizer(ejbql, "#}", true);
         StringBuilder ejbqlBuilder = new StringBuilder();
         while ( ejbqlTokens.hasMoreTokens() )
         {
            String token = ejbqlTokens.nextToken();
            if ( "#".equals(token) )
            {
               String expression = token + ejbqlTokens.nextToken() + ejbqlTokens.nextToken();
               queryParameters.add( Expressions.instance().createValueBinding(expression) );
               ejbqlBuilder.append("?").append( queryParameters.size() );
            }
            else
            {
               ejbqlBuilder.append(token);
            }
         }
         parsedEjbql = ejbqlBuilder.toString();
         
         parsedRestrictions = new ArrayList<String>( restrictions.size() );
         restrictionParameters = new ArrayList<ValueBinding>( restrictions.size() );
         
         for (String restriction: restrictions)
         {
            StringTokenizer tokens = new StringTokenizer(restriction, "#}", true);
            StringBuilder builder = new StringBuilder();
            ValueBinding valueBinding = null;
            while ( tokens.hasMoreTokens() )
            {
               String token = tokens.nextToken();
               if ( "#".equals(token) )
               {
                  String expression = token + tokens.nextToken() + tokens.nextToken();
                  valueBinding = Expressions.instance().createValueBinding(expression);
                  builder.append("?").append( queryParameters.size() + restrictionParameters.size() );
               }
               else
               {
                  builder.append(token);
               }
               
            }
            
            if (valueBinding==null) 
            {
               throw new IllegalArgumentException("no value binding in restriction: " + restriction);
            }
            
            parsedRestrictions.add(builder.toString());
            restrictionParameters.add(valueBinding);
         }
         
      }
   }
   
   protected String getRenderedEjbql()
   {
      StringBuilder builder = new StringBuilder()
            .append(parsedEjbql);
      
      for (int i=0; i<restrictions.size(); i++)
      {
         Object parameterValue = restrictionParameters.get(i).getValue();
         if (parameterValue!=null)
         {
            if ( builder.toString().toLowerCase().indexOf("where")>0 )
            {
               builder.append(" and ");
            }
            else
            {
               builder.append(" where ");
            }
            builder.append( parsedRestrictions.get(i) );
         }
      }
         
      if (order!=null) builder.append(" order by ").append(order);
      
      return builder.toString();
   }

   protected String getCountEjbql()
   {
      String ejbql = getRenderedEjbql();    
      int fromLoc = ejbql.indexOf("from");
      int orderLoc = ejbql.indexOf("order");
      if (orderLoc<0) orderLoc = ejbql.length();
      return "select count(*) " + ejbql.substring(fromLoc, orderLoc);
   }
   
   public String getEjbql()
   {
      return ejbql;
   }

   public void setEjbql(String ejbql)
   {
      this.ejbql = ejbql;
   }

   public EntityManager getEntityManager()
   {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   public Integer getFirstResult()
   {
      return firstResult;
   }
   
   public boolean isPreviousExists()
   {
      return firstResult!=null && firstResult!=0;
   }

   public boolean isNextExists()
   {
      return resultList!=null && resultList.size() == maxResults;
   }

   public void setFirstResult(Integer firstResult)
   {
      dataModel = null;
      this.firstResult = firstResult;
   }

   public Integer getMaxResults()
   {
      return maxResults;
   }

   public void setMaxResults(Integer maxResults)
   {
      dataModel = null;
      this.maxResults = maxResults;
   }

   public Map<String, String> getHints()
   {
      return hints;
   }

   public void setHints(Map<String, String> hints)
   {
      this.hints = hints;
   }

   public List<String> getRestrictions()
   {
      return restrictions;
   }

   public void setRestrictions(List<String> restrictions)
   {
      this.restrictions = restrictions;
   }

   public String getOrder()
   {
      return order;
   }

   public void setOrder(String order)
   {
      this.order = order;
   }

}
