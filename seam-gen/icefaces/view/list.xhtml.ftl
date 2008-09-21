<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
                             "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<#include "../util/TypeInfo.ftl">

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
    
    <ice:form id="list${componentName}FormId" styleClass="edit">
      <ice:panelGroup  id="searchGroup" styleClass="formBorderHighlight">
          <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                  <td class="iceDatTblColHdr2">
                    <ice:outputText id="list${entityName}Id" value="${entityName} search"/>
                 </td>
              </tr>
         </table>	
         <ice:panelGroup id="listPanelGroup${entityName}Id" styleClass="edit">
		
<#foreach property in pojo.allPropertiesIterator>
<#if !c2h.isCollection(property) && !util.isToOne(property) && property != pojo.versionProperty!>
<#if c2j.isComponent(property)>
<#foreach componentProperty in property.value.propertyIterator>
<#if isString(componentProperty)>
            <s:decorate id="${componentProperty.name}decId" template="layout/display.xhtml">
                <ui:define name="label">${componentProperty.name}</ui:define>
                  <ice:inputText id="${componentProperty.name}TextId" 
                          value="${'#'}{${listName}.${componentName}.${property.name}.${componentProperty.name}}"
				  partialSubmit="true"/>
            </s:decorate>

</#if>
</#foreach>
<#else>
<#if isString(property)>
            <s:decorate id="${property.name}decId" template="layout/display.xhtml">
                <ui:define name="label">${property.name}</ui:define>
                <ice:inputText id="list${property.name}TextId" 
                          value="${'#'}{${listName}.${componentName}.${property.name}}"
				  partialSubmit="true"/>
            </s:decorate>

</#if>
</#if>
</#if>
</#foreach>
          
   
	  </ice:panelGroup>
  
        
        <div class="actionButtons">
            <ice:commandButton id="listSearchButtonId" value="Search" action="/${listPageName}.xhtml"/>
        </div>
      </ice:panelGroup> 
    </ice:form>
    
    <ice:panelGroup styleClass="formBorderHighlight">

    <h3>${componentName}  search results</h3>

    <div class="searchResults" id="list${componentName}Results">
    <ice:outputText value="The ${componentName} search returned no results." 
               rendered="${'#'}{empty ${listName}.resultList}"/>
               
    <ice:dataTable id="${listName}TableId" 
                  var="${componentName}"
                value="${'#'}{${listName}.resultList}" 
            resizable="true"
	columnClasses="allCols"
             rendered="${'#'}{not empty ${listName}.resultList}">
<#foreach property in pojo.allPropertiesIterator>
<#if !c2h.isCollection(property) && !util.isToOne(property) && property != pojo.versionProperty!>
<#if pojo.isComponent(property)>
<#foreach componentProperty in property.value.propertyIterator>
        <ice:column id="list${componentProperty.name}Id">
            <f:facet name="header">${componentProperty.name}</f:facet>
            ${'#'}{${componentName}.${property.name}.${componentProperty.name}}
        </ice:column>
</#foreach>
<#else>
        <ice:column id="list${property.name}Id">
            <f:facet name="header">
                <s:link styleClass="columnHeader"
		             id="list${property.name}LinkId"
		             value="${property.name} ${'#'}{${listName}.orderColumn=='${property.name}' ? (${listName}.orderDirection=='desc' ? messages.down : messages.up)  : ''}">                   
                    <f:param name="sort" value="${property.name}" />
                    <f:param name="dir" value="${'#'}{${listName}.orderDirection=='asc' ? 'desc' : 'asc'}"/>
                </s:link>
            </f:facet>
            ${'#'}{${componentName}.${property.name}}&amp;nbsp;
        </ice:column>
</#if>
</#if>
<#if util.isToOne(property)>
<#assign parentPojo = c2j.getPOJOClass(cfg.getClassMapping(property.value.referencedEntityName))>
<#if parentPojo.isComponent(parentPojo.identifierProperty)>
<#foreach componentProperty in parentPojo.identifierProperty.value.propertyIterator>
        <ice:column id="listColumn${componentProperty}${listName}Id">
            <f:facet name="header">
<#assign propertyPath = property.name + '.' + parentPojo.identifierProperty.name + '.' + componentProperty.name>
                <s:link styleClass="columnHeader"
		                id="listColumnHeader${componentProperty.name}Id"
                             value="${property.name} ${componentProperty.name} ${'#'}{${listName}.order=='${propertyPath} asc' ? messages.down : ( ${listName}.order=='${propertyPath} desc' ? messages.up : '' )}">
                    <f:param name="order" value="${'#'}{${listName}.order=='${propertyPath} asc' ? '${propertyPath} desc' : '${propertyPath} asc'}"/>
                </s:link>
            </f:facet>
            ${'#'}{${componentName}.${propertyPath}}&amp;nbsp;
        </ice:column>
</#foreach>
<#else>
        <ice:column id="listColumn${property.name}Id">
            <f:facet name="header">
<#assign propertyPath = property.name + '.' + parentPojo.identifierProperty.name>
                <s:link styleClass="columnHeader"
		                id="listcolumnHeader${property.name}Id"
                             value="${property.name} ${parentPojo.identifierProperty.name} ${'#'}{${listName}.order=='${propertyPath} asc' ? messages.down : ( ${listName}.order=='${propertyPath} desc' ? messages.up : '' )}">
                    <f:param name="order" value="${'#'}{${listName}.order=='${propertyPath} asc' ? '${propertyPath} desc' : '${propertyPath} asc'}"/>
                </s:link>
            </f:facet>
            ${'#'}{${componentName}.${propertyPath}}
        </ice:column>
</#if>
</#if>
</#foreach>
        <ice:column id="listColumn${pageName}Id">
            <f:facet name="header">action</f:facet>
            <s:link view="/${'#'}{empty from ? '${pageName}' : from}.xhtml" 
                   value="Select" 
                      id="list${componentName}LinkId">
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
                  id="firstPage${listName}Id">
          <f:param name="firstResult" value="0"/>
        </s:link>
        
        <s:link view="/${listPageName}.xhtml" 
            rendered="${'#'}{${listName}.previousExists}" 
               value="${'#'}{messages.left} Previous Page"
                  id="previousPage${listName}Id">
            <f:param name="firstResult" 
                    value="${'#'}{${listName}.previousFirstResult}"/>
        </s:link>
        
        <s:link view="/${listPageName}.xhtml" 
            rendered="${'#'}{${listName}.nextExists}" 
               value="Next Page ${'#'}{messages.right}"
                  id="nextPage${listName}Id">
            <f:param name="firstResult" 
                    value="${'#'}{${listName}.nextFirstResult}"/>
        </s:link>
        
        <s:link view="/${listPageName}.xhtml" 
            rendered="${'#'}{${listName}.nextExists}" 
               value="Last Page ${'#'}{messages.right}${'#'}{messages.right}"
                  id="lastPage${listName}Id">
            <f:param name="firstResult" 
                    value="${'#'}{${listName}.lastFirstResult}"/>
        </s:link>
        
    </div>
    
    <s:div styleClass="actionButtons" rendered="${'#'}{empty from}">
        <s:button view="/${editPageName}.xhtml"
                    id="listCreate${componentName}Id" 
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

