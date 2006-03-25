package org.jboss.seam.ui;

import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;

public class HtmlQueryTable extends HtmlDataTable {
   
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.HtmlQueryTable";
   
   private String persistenceContext = "em";
   private String ejbql;
   private int maxResults = -1;
   private int firstResult = -1;

   @Override
   public Object getValue() {
      if ( super.getValue()==null )
      {
         EntityManager em = (EntityManager) Component.getInstance(persistenceContext, true);
         Query query = em.createQuery(ejbql);
         Object pageParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("page");
         int page = pageParam==null ? 0 : Integer.parseInt( pageParam.toString() );
         int first = 0;
         if (maxResults>=0) 
         {
            query.setMaxResults(maxResults);
            first += page*maxResults;
         }
         if (firstResult>=0) first+=firstResult;
         if (first>0) query.setFirstResult(first);
         super.setValue( query.getResultList() );
      }
      return super.getValue();
   }

   public String getPersistenceContext() {
      return persistenceContext;
   }

   public void setPersistenceContext(String persistenceContext) {
      this.persistenceContext = persistenceContext;
   }

   public String getEjbql() {
      return ejbql;
   }

   public void setEjbql(String ejbql) {
      this.ejbql = ejbql;
   }

   public int getMaxResults() {
      return maxResults;
   }

   public void setMaxResults(int maxResults) {
      this.maxResults = maxResults;
   }

   public int getFirstResult() {
      return firstResult;
   }

   public void setFirstResult(int firstResult) {
      this.firstResult = firstResult;
   }

   @Override
   public void restoreState(FacesContext context, Object state) {
      Object[] values = (Object[]) state;
      super.restoreState(context, values[0]);
      ejbql = (String) values[1];
      maxResults = (Integer) values[2];
      firstResult = (Integer) values[3];
   }

   @Override
   public Object saveState(FacesContext context) {
      Object[] values = new Object[4];
      values[0] = super.saveState(context);
      values[1] = ejbql;
      values[2] = maxResults;
      values[3] = firstResult;
      return values;
   }

}
