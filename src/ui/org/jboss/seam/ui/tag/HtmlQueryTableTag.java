/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
