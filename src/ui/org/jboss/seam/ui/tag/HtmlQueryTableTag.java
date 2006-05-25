package org.jboss.seam.ui.tag;

import javax.faces.component.UIComponent;

import org.apache.myfaces.shared_impl.taglib.html.HtmlDataTableTagBase;
import org.jboss.seam.ui.HtmlQueryTable;

/**
 * @author Gavin King
 */
public class HtmlQueryTableTag extends HtmlDataTableTagBase
{
    private String ejbql;
    private String maxResults;
    private String firstResult;
    
    @Override
    public String getComponentType()
    {
        return HtmlQueryTable.COMPONENT_TYPE;
    }
    
    @Override
    public String getRendererType()
    {
        return "javax.faces.Table";
    }

    public void setEjbql(String ejbql) 
    {
       this.ejbql = ejbql;
    }

    public void setFirstResult(String firstResult) 
    {
       this.firstResult = firstResult;
    }

    public void setMaxResults(String maxResults) 
    {
       this.maxResults = maxResults;
    }

   @Override
   protected void setProperties(UIComponent component) {
      super.setProperties(component);
      setStringProperty(component, "ejbql", ejbql);
      setIntegerProperty(component, "maxResults", maxResults);
      setIntegerProperty(component, "firstResult", firstResult);
   }
   
}
