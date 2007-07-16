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
		        xmlns:ice="http://www.icesoft.com/icefaces/component"  
                template="layout/template.xhtml">
                       
<ui:define name="body">
    
    <h:messages globalOnly="true" styleClass="message" id="globalMessages"/>
    
    <ice:form id="${componentName}Form" styleClass="edit">
      <ice:panelCollapsible expanded="true" styleClass="pnlClpsblDemo">
       <f:facet name="header">
        <ice:panelGroup styleClass="expandableStateIndicator">
                  <ice:outputText value="${entityName} search"/>
	  </ice:panelGroup>
       </f:facet>        
        <ice:panelGroup styleClass="edit">
		 <ice:panelGrid columns="2" rowClasses="prop" columnClasses="name,value">
<#foreach property in pojo.allPropertiesIterator>
<#if !c2h.isCollection(property) && !c2h.isManyToOne(property)>
<#if c2j.isComponent(property)>
<#foreach componentProperty in property.value.propertyIterator>
<#if componentProperty.value.typeName == "string">
	
                  <h:outputLabel for="${componentProperty.name}">${componentProperty.name}</h:outputLabel>
                  <ice:inputText id="${componentProperty.name}" 
                          value="${'#'}{${listName}.${componentName}.${property.name}.${componentProperty.name}}"
				partialSubmit="true"/>
	         
</#if>
</#foreach>
<#else>
<#if property.value.typeName == "string">
	
                <h:outputLabel for="${property.name}">${property.name}</h:outputLabel>
                <ice:inputText id="${property.name}" 
                          value="${'#'}{${listName}.${componentName}.${property.name}}"
				  partialSubmit="true"/>
	
</#if>
</#if>
</#if>
</#foreach>
            </ice:panelGrid>
   
	</ice:panelGroup>
   </ice:panelCollapsible>
        
        <div class="actionButtons">
            <ice:commandButton id="search" value="Search" action="/${listPageName}.xhtml"/>
        </div>
      
    </ice:form>
    
    <ice:panelGroup styleClass="formBorderHighlight">

    <h3>${componentName}  search results</h3>

    <div class="searchResults" id="contactgroupListResults">
    <ice:outputText value="No ${componentName} exists" 
               rendered="${'#'}{empty ${listName}.resultList}"/>
               
    <ice:dataTable id="${listName}" 
                var="${componentName}"
              value="${'#'}{${listName}.resultList}" 
		columnClasses="allCols"
           rendered="${'#'}{not empty ${listName}.resultList}">
<#foreach property in pojo.allPropertiesIterator>
<#if !c2h.isCollection(property) && !c2h.isManyToOne(property)>
<#if pojo.isComponent(property)>
<#foreach componentProperty in property.value.propertyIterator>
        <ice:column>
            <f:facet name="header">${componentProperty.name}</f:facet>
            ${'#'}{${componentName}.${property.name}.${componentProperty.name}}
        </ice:column>
</#foreach>
<#else>
        <ice:column>
            <f:facet name="header">
                <s:link styleClass="columnHeader"
                             value="${property.name} ${'#'}{${listName}.order=='${property.name} asc' ? messages.down : ( ${listName}.order=='${property.name} desc' ? messages.up : '' )}">
                    <f:param name="order" value="${'#'}{${listName}.order=='${property.name} asc' ? '${property.name} desc' : '${property.name} asc'}"/>
                </s:link>
            </f:facet>
            ${'#'}{${componentName}.${property.name}}&amp;nbsp;
        </ice:column>
</#if>
</#if>
<#if c2h.isManyToOne(property)>
<#assign parentPojo = c2j.getPOJOClass(cfg.getClassMapping(property.value.referencedEntityName))>
<#if parentPojo.isComponent(parentPojo.identifierProperty)>
<#foreach componentProperty in parentPojo.identifierProperty.value.propertyIterator>
        <ice:column>
            <f:facet name="header">
<#assign propertyPath = property.name + '.' + parentPojo.identifierProperty.name + '.' + componentProperty.name>
                <s:link styleClass="columnHeader"
                             value="${property.name} ${componentProperty.name} ${'#'}{${listName}.order=='${propertyPath} asc' ? messages.down : ( ${listName}.order=='${propertyPath} desc' ? messages.up : '' )}">
                    <f:param name="order" value="${'#'}{${listName}.order=='${propertyPath} asc' ? '${propertyPath} desc' : '${propertyPath} asc'}"/>
                </s:link>
            </f:facet>
            ${'#'}{${componentName}.${propertyPath}}&amp;nbsp;
        </ice:column>
</#foreach>
<#else>
        <ice:column>
            <f:facet name="header">
<#assign propertyPath = property.name + '.' + parentPojo.identifierProperty.name>
                <s:link styleClass="columnHeader"
                             value="${property.name} ${parentPojo.identifierProperty.name} ${'#'}{${listName}.order=='${propertyPath} asc' ? messages.down : ( ${listName}.order=='${propertyPath} desc' ? messages.up : '' )}">
                    <f:param name="order" value="${'#'}{${listName}.order=='${propertyPath} asc' ? '${propertyPath} desc' : '${propertyPath} asc'}"/>
                </s:link>
            </f:facet>
            ${'#'}{${componentName}.${propertyPath}}
        </ice:column>
</#if>
</#if>
</#foreach>
        <ice:column>
            <f:facet name="header">action</f:facet>
            <s:link view="/${'#'}{empty from ? '${pageName}' : from}.xhtml" 
                   value="Select" 
                      id="${componentName}">
<#if pojo.isComponent(pojo.identifierProperty)>
<#foreach componentProperty in pojo.identifierProperty.value.propertyIterator>
                <f:param name="${componentName}${util.upper(componentProperty.name)}" 
                        value="${'#'}{${componentName}.${pojo.identifierProperty.name}.${componentProperty.name}}"/>
</#foreach>
<#else>
                <f:param name="${componentName}${util.upper(pojo.identifierProperty.name)}" 
                        value="${'#'}{${componentName}.${pojo.identifierProperty.name}}"/>
</#if>
            </s:link>
        </ice:column>
    </ice:dataTable>

    </div>
</ice:panelGroup>

    <div class="tableControl">
      
        <s:link view="/${listPageName}.xhtml" 
            rendered="${'#'}{${listName}.previousExists}" 
               value="${'#'}{messages.left}${'#'}{messages.left} First Page"
                  id="firstPage">
          <f:param name="firstResult" value="0"/>
        </s:link>
        
        <s:link view="/${listPageName}.xhtml" 
            rendered="${'#'}{${listName}.previousExists}" 
               value="${'#'}{messages.left} Previous Page"
                  id="previousPage">
            <f:param name="firstResult" 
                    value="${'#'}{${listName}.previousFirstResult}"/>
        </s:link>
        
        <s:link view="/${listPageName}.xhtml" 
            rendered="${'#'}{${listName}.nextExists}" 
               value="Next Page ${'#'}{messages.right}"
                  id="nextPage">
            <f:param name="firstResult" 
                    value="${'#'}{${listName}.nextFirstResult}"/>
        </s:link>
        
        <s:link view="/${listPageName}.xhtml" 
            rendered="${'#'}{${listName}.nextExists}" 
               value="Last Page ${'#'}{messages.right}${'#'}{messages.right}"
                  id="lastPage">
            <f:param name="firstResult" 
                    value="${'#'}{${listName}.lastFirstResult}"/>
        </s:link>
        
    </div>
    
    <s:div styleClass="actionButtons" rendered="${'#'}{empty from}">
        <s:button view="/${editPageName}.xhtml"
                    id="create" 
                 value="Create ${componentName}">
<#assign idName = componentName + util.upper(pojo.identifierProperty.name)>
<#if c2j.isComponent(pojo.identifierProperty)>
<#foreach componentProperty in pojo.identifierProperty.value.propertyIterator>
<#assign cidName = componentName + util.upper(componentProperty.name)>
            <f:param name="${cidName}"/>
</#foreach>
<#else>
            <f:param name="${idName}"/>
</#if>
        </s:button>
    </s:div>
    
</ui:define>

</ui:composition>

