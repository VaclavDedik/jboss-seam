package org.jboss.seam.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.model.DataModel;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueBinding;
import org.jboss.seam.jsf.ListDataModel;

/**
 * Superclass for components which manage a query
 * result set. This class may be reused by either
 * configuration or extension, and may be bound
 * directly to a view, or accessed by some
 * intermediate Seam component.
 * 
 * @author Gavin King
 *
 */
public abstract class Query
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
         dataModel = new ListDataModel( getResultList() );
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
         
         List<String> restrictionFragments = getRestrictions();
         parsedRestrictions = new ArrayList<String>( restrictionFragments.size() );
         restrictionParameters = new ArrayList<ValueBinding>( restrictionFragments.size() );
         
         for ( String restriction: restrictionFragments )
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
