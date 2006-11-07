<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
                             "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<#assign entityName = pojo.shortName>
<#assign componentName = util.lower(entityName)>
<#assign listName = componentName + "List">
<#assign pageName = entityName>

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
            <s:link id="${componentName}" value="Select" view="/${pageName}.xhtml" propagation="begin">
                <f:param name="${componentName}Id" value="${'#'}{${componentName}.${pojo.identifierProperty.name}}"/>
            </s:link>
        </h:column>
    </h:dataTable>
    
    <div class="actionButtons">
        <s:link id="done" value="Create ${componentName}" linkStyle="button"
            view="/${pageName}.xhtml"/>			  
    </div>
    
</ui:define>

</ui:composition>

