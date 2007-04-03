package org.jboss.seam.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.model.DataModel;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Expressions.ValueBinding;
import org.jboss.seam.jsf.JsfProvider;
import org.jboss.seam.jsf.ListDataModel;
import org.jboss.seam.persistence.QueryParser;

/**
 * Base class for components which manage a query
 * result set. This class may be reused by either
 * configuration or extension, and may be bound
 * directly to a view, or accessed by some
 * intermediate Seam component.
 * 
 * @author Gavin King
 *
 */
public abstract class Query<T> 
      extends PersistenceController<T> //TODO: extend MutableController!
{
   private static final Pattern FROM_PATTERN = Pattern.compile("(^|\\s)(from)\\s", Pattern.CASE_INSENSITIVE);
   private static final Pattern ORDER_PATTERN = Pattern.compile("\\s(order)(\\s)+by\\s", Pattern.CASE_INSENSITIVE);
   private static final Pattern WHERE_PATTERN = Pattern.compile("\\s(where)\\s", Pattern.CASE_INSENSITIVE);

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
   
   private List<Object> queryParameterValues;
   private List<Object> restrictionParameterValues;
   
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
         dataModel = JsfProvider.instance().getDataModel(this);
      }
      return dataModel;
   }
   
   public void refresh()
   {
      clearDataModel();
   }
   
   public void last()
   {
      setFirstResult( (int) getLastFirstResult() );
      clearDataModel();
   }
   
   public void next()
   {
      setFirstResult( getNextFirstResult() );
      clearDataModel();
   }

   public void previous()
   {
      setFirstResult( getPreviousFirstResult() );
      clearDataModel();
   }
   
   public void first()
   {
      setFirstResult(0);
      clearDataModel();
   }
   
   protected void clearDataModel()
   {
      dataModel = null;
   }

   @Transactional
   public long getLastFirstResult()
   {
      return ( getResultCount() / getMaxResults() ) * getMaxResults();
   }
   
   public int getNextFirstResult()
   {
      return ( getFirstResult()==null ? 0 : getFirstResult() ) + getMaxResults();
   }

   public int getPreviousFirstResult()
   {
      if ( getMaxResults() > ( getFirstResult()==null ? 0 : getFirstResult() ) ) 
      {
         return 0;
      }
      else
      {
         return getFirstResult() - getMaxResults();
      }
   }
   
   protected void parseEjbql()
   {
      if (parsedEjbql==null)
      {
         QueryParser qp = new QueryParser( getEjbql() );
         queryParameters = qp.getParameterValueBindings();
         parsedEjbql = qp.getEjbql();
         
         List<String> restrictionFragments = getRestrictions();
         parsedRestrictions = new ArrayList<String>( restrictionFragments.size() );
         restrictionParameters = new ArrayList<ValueBinding>( restrictionFragments.size() );         
         for ( String restriction: restrictionFragments )
         {
            QueryParser rqp = new QueryParser( restriction, queryParameters.size() + restrictionParameters.size() );            
            if ( rqp.getParameterValueBindings().size()!=1 ) 
            {
               throw new IllegalArgumentException("there should be exactly one value binding in a restriction: " + restriction);
            }            
            parsedRestrictions.add( rqp.getEjbql() );
            restrictionParameters.addAll( rqp.getParameterValueBindings() );
         }
         
      }
   }
   
   protected String getRenderedEjbql()
   {
      StringBuilder builder = new StringBuilder().append(parsedEjbql);
      
      for (int i=0; i<getRestrictions().size(); i++)
      {
         Object parameterValue = restrictionParameters.get(i).getValue();
         if ( isRestrictionParameterSet(parameterValue) )
         {
            if ( WHERE_PATTERN.matcher(builder).find() )
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
         
      if ( getOrder()!=null ) builder.append(" order by ").append( getOrder() );
      
      return builder.toString();
   }
   
   protected boolean isRestrictionParameterSet(Object parameterValue)
   {
      return parameterValue!=null && !"".equals(parameterValue);
   }

   protected String getCountEjbql()
   {
      String ejbql = getRenderedEjbql();
      
      Matcher fromMatcher = FROM_PATTERN.matcher(ejbql);
      if ( !fromMatcher.find() )
      {
         throw new IllegalArgumentException("no from clause found in query");
      }
      int fromLoc = fromMatcher.start(2);
      
      Matcher orderMatcher = ORDER_PATTERN.matcher(ejbql);
      int orderLoc = orderMatcher.find() ? orderMatcher.start(1) : ejbql.length();

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
      return getFirstResult()!=null && getFirstResult()!=0;
   }

   public boolean isNextExists()
   {
      return getResultList()!=null && 
            getResultList().size() == getMaxResults();
   }

   public void setFirstResult(Integer firstResult)
   {
      this.firstResult = firstResult;
      clearDataModel();
   }

   public Integer getMaxResults()
   {
      return maxResults;
   }

   public void setMaxResults(Integer maxResults)
   {
      this.maxResults = maxResults;
      clearDataModel();
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
   
   private static boolean isAnyParameterDirty(List<ValueBinding> valueBindings, List<Object> lastParameterValues)
   {
      if (lastParameterValues==null) return true;
      for (int i=0; i<valueBindings.size(); i++)
      {
         Object parameterValue = valueBindings.get(i).getValue();
         Object lastParameterValue = lastParameterValues.get(i);
         if ( parameterValue!=lastParameterValue && ( parameterValue==null || !parameterValue.equals(lastParameterValue) ) )
         {
            return true;
         }
      }
      return false;
   }
   
   private static List<Object> getParameterValues(List<ValueBinding> valueBindings)
   {
      List<Object> values = new ArrayList<Object>( valueBindings.size() );
      for (int i=0; i<valueBindings.size(); i++)
      {
         values.add( valueBindings.get(i).getValue() );
      }
      return values;
   }
   
   protected void evaluateAllParameters()
   {
      setQueryParameterValues( getParameterValues( getQueryParameters() ) );
      setRestrictionParameterValues( getParameterValues( getRestrictionParameters() ) );
   }
   
   protected boolean isAnyParameterDirty()
   {
      return isAnyParameterDirty( getQueryParameters(), getQueryParameterValues() )
            || isAnyParameterDirty( getRestrictionParameters(), getRestrictionParameterValues() );
   }
   
   protected List<Object> getQueryParameterValues()
   {
      return queryParameterValues;
   }
   
   protected void setQueryParameterValues(List<Object> queryParameterValues)
   {
      this.queryParameterValues = queryParameterValues;
   }
   
   protected List<Object> getRestrictionParameterValues()
   {
      return restrictionParameterValues;
   }
   
   protected void setRestrictionParameterValues(List<Object> restrictionParameterValues)
   {
      this.restrictionParameterValues = restrictionParameterValues;
   }

}
