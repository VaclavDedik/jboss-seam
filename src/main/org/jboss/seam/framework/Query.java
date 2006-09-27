package org.jboss.seam.framework;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.model.DataModel;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.jsf.ListDataModel;

@Intercept(NEVER)
public class Query
{
   private String ejbql;
   private String queryName;
   private List<ValueBinding> queryParameters;
   private Integer firstResult;
   private Integer maxResults;
   private EntityManager entityManager;
   private Map<String, String> hints;
   private List<String> restrictions;
   private String order;
   
   private List resultList;
   private DataModel dataModel;
   private Object singleResult;
   
   public List getResultList()
   {
      //if (resultList==null)
      {
         javax.persistence.Query query = createQuery();
         resultList = query==null ? null : query.getResultList();
      }
      return resultList;
   }
   
   public Object getSingleResult()
   {
      if (singleResult==null)
      {
         javax.persistence.Query query = createQuery();
         singleResult = query==null ? null : query.getSingleResult();
      }
      return singleResult;
   }

   public DataModel getDataModel()
   {
      if (dataModel==null)
      {
         dataModel = new ListDataModel( getResultList() );
      }
      return dataModel;
   }
   
   private javax.persistence.Query createQuery()
   {
      prepareEjbql();
      
      entityManager.joinTransaction();
      javax.persistence.Query query = entityManager.createQuery(ejbql);
      for (int i=0; i<queryParameters.size(); i++)
      {
         Object parameterValue = queryParameters.get(i).getValue( FacesContext.getCurrentInstance() );
         if (parameterValue==null)
         {
            return null;
         }
         else
         {
            query.setParameter( i, parameterValue );
         }
      }
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
   
   public void refresh()
   {
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

   public int getNextFirstResult()
   {
      //TODO: check to see if there are more results
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
   
   public void prepareEjbql()
   {
      //if (ejbql!=null)
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
               queryParameters.add( FacesContext.getCurrentInstance().getApplication().createValueBinding(expression) );
               ejbqlBuilder.append("?").append( queryParameters.size() );
            }
            else
            {
               ejbqlBuilder.append(token);
            }
         }
         
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
                  valueBinding = FacesContext.getCurrentInstance().getApplication().createValueBinding(expression);
                  builder.append("?").append( queryParameters.size() );
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
            
            Object parameterValue = valueBinding.getValue( FacesContext.getCurrentInstance() );
            if (parameterValue!=null)
            {
               queryParameters.add(valueBinding);
               if ( ejbqlBuilder.toString().toLowerCase().indexOf("where")>0 )
               {
                  ejbqlBuilder.append(" and ");
               }
               else
               {
                  ejbqlBuilder.append(" where ");
               }
               ejbqlBuilder.append(builder);
            }
         }
         
         if (order!=null) ejbqlBuilder.append(order);
         
         ejbql = ejbqlBuilder.toString();
      }
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
      return true; //TODO!
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

   public String getQueryName()
   {
      return queryName;
   }

   public void setQueryName(String queryName)
   {
      this.queryName = queryName;
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
