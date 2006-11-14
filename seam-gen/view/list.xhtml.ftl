<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
                             "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<#assign entityName = pojo.shortName>
<#assign componentName = util.lower(entityName)>
<#assign listName = componentName + "List">
<#assign pageName = entityName>
<#assign editPageName = entityName + "Edit">
<#assign listPageName = entityName + "List">

<ui:composition xmlns="http://www.w3.org/1999/xhtml"
                xmlns:s="http://jboss.com/products/seam/taglib"
                xmlns:ui="http://java.sun.com/jsf/facelets"
                xmlns:f="http://java.sun.com/jsf/core"
                xmlns:h="http://java.sun.com/jsf/html"
                template="layout/template.xhtml">
                       
<ui:define name="body">

    <h1>${entityName} list</h1>
    <p>Generated list page</p>
    
    <h:messages globalOnly="true" styleClass="message"/>
    
    <h:outputText value="No ${componentName} exists" 
            rendered="${'#'}{empty ${listName}.resultList}"/>
    <h:dataTable id="${listName}" var="${componentName}"
            value="${'#'}{${listName}.resultList}" 
            rendered="${'#'}{not empty ${listName}.resultList}">
<#foreach property in pojo.allPropertiesIterator>
<#if !c2h.isCollection(property) && !c2h.isManyToOne(property)>
		<h:column>
			<f:facet name="header">${property.name}</f:facet>
			${'#'}{${componentName}.${property.name}}
		</h:column>
</#if>
<#if c2h.isManyToOne(property)>
<#assign parentPojo = c2j.getPOJOClass(cfg.getClassMapping(property.value.referencedEntityName))>
		<h:column>
			<f:facet name="header">${property.name} ${parentPojo.identifierProperty.name}</f:facet>
			${'#'}{${componentName}.${property.name}.${parentPojo.identifierProperty.name}}
		</h:column>
</#if>
</#foreach>
        <h:column>
            <f:facet name="header">action</f:facet>
            <s:link id="${componentName}" value="Select" view="/${pageName}.xhtml">
                <f:param name="${componentName}Id" value="${'#'}{${componentName}.${pojo.identifierProperty.name}}"/>
            </s:link>
        </h:column>
    </h:dataTable>

    <div class="tableControl">
      
        <s:link view="/${listPageName}.xhtml" rendered="${'#'}{${listName}.previousExists}" value="&lt;&lt; First Page">
          <f:param name="firstResult" value="0"/>
        </s:link>
        
        <s:link view="/${listPageName}.xhtml" rendered="${'#'}{${listName}.previousExists}" value="&lt; Previous Page">
          <f:param name="firstResult" value="${'#'}{${listName}.previousFirstResult}"/>
        </s:link>
        
        <s:link view="/${listPageName}.xhtml" rendered="${'#'}{${listName}.nextExists}" value="Next Page &gt;">
          <f:param name="firstResult" value="${'#'}{${listName}.nextFirstResult}"/>
        </s:link>
        
        <s:link view="/${listPageName}.xhtml" rendered="${'#'}{${listName}.nextExists}" value="Last Page &gt;&gt;">
          <f:param name="firstResult" value="${'#'}{${listName}.lastFirstResult}"/>
        </s:link>
        
    </div>
    
    <div class="actionButtons">
        <s:button id="create" value="Create ${componentName}"
            view="/${editPageName}.xhtml" propagation="begin"/>			  
    </div>
    
</ui:define>

</ui:composition>

