package org.jboss.seam.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.model.DataModel;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueBinding;
import org.jboss.seam.jsf.ListDataModel;

public abstract class Query
{
   private String ejbql;
   private Integer firstResult;
   private Integer maxResults;
   private List<String> restrictions = new ArrayList<String>(0);
   private String order;
   
   private DataModel dataModel;
   
   private String parsedEjbql;
   private List<ValueBinding> queryParameters;
   private List<String> parsedRestrictions;
   private List<ValueBinding> restrictionParameters;
   
   public abstract List getResultList();
   public abstract Object getSingleResult();
   public abstract Long getResultCount();

   @Create
   public void validate()
   {
      if ( getEjbql()==null )
      {
         throw new IllegalStateException("ejbql is null");
      }
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
   
   public void refresh()
   {
      dataModel = null;
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
         StringTokenizer ejbqlTokens = new StringTokenizer( getEjbql(), "#}", true );
         StringBuilder ejbqlBuilder = new StringBuilder();
         while ( ejbqlTokens.hasMoreTokens() )
         {
            String token = ejbqlTokens.nextToken();
            if ( "#".equals(token) )
            {
               String expression = token + ejbqlTokens.nextToken() + ejbqlTokens.nextToken();
               queryParameters.add( Expressions.instance().createValueBinding(expression) );
               ejbqlBuilder.append(":p").append( queryParameters.size() );
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
                  builder.append(":p").append( queryParameters.size() + restrictionParameters.size() );
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
      return getResultList()!=null && 
            getResultList().size() == maxResults;
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
   
   protected List<ValueBinding> getQueryParameters()
   {
      return queryParameters;
   }
   
   protected List<ValueBinding> getRestrictionParameters()
   {
      return restrictionParameters;
   }

}
