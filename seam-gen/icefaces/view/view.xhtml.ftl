<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
                             "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<#assign entityName = pojo.shortName>
<#assign componentName = util.lower(entityName)>
<#assign homeName = componentName + "Home">
<#assign masterPageName = entityName + "List">
<#assign editPageName = entityName + "Edit">

<ui:composition xmlns="http://www.w3.org/1999/xhtml"
                xmlns:s="http://jboss.com/products/seam/taglib"
                xmlns:ui="http://java.sun.com/jsf/facelets"
                xmlns:f="http://java.sun.com/jsf/core"
                xmlns:h="http://java.sun.com/jsf/html"
		xmlns:ice="http://www.icesoft.com/icefaces/component"   
                template="/layout/template.xhtml">
                       
<ui:define name="body">
    
    <h:messages globalOnly="true" styleClass="message" id="globalMessages"/>
 
      <ice:panelGroup  id="view${homeName}PanelGroupId" styleClass="formBorderHighlight">
          <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                  <td class="iceDatTblColHdr2">
			    <ice:outputText id="viewText${homeName}Id" value="${homeName}"/>
                  </td>
              </tr>
          </table>
      
<#foreach property in pojo.allPropertiesIterator>
<#if !c2h.isCollection(property) && !c2h.isManyToOne(property)>
<#include "viewproperty.xhtml.ftl">
</#if>
</#foreach>

          <div style="clear:both"/>
 

    <div id="view${editPageName}searchButtons" class="actionButtons">      

        <s:button view="/${editPageName}.xhtml" 
                    id="viewEdit${editPageName}" 
                 value="Edit"/>

        <s:button view="/${'#'}{empty ${componentName}From ? '${masterPageName}' : ${componentName}From}.xhtml"
                    id="viewDone${editPageName}"
                 value="Done"/>

    </div>
  </ice:panelGroup>

<#assign hasAssociations=false>
<#foreach property in pojo.allPropertiesIterator>
<#if c2h.isManyToOne(property) || c2h.isOneToManyCollection(property)>
<#assign hasAssociations=true>
</#if>
</#foreach>

<#if hasAssociations>
    <ice:form id="view2${homeName}FormId">
       <ice:panelTabSet id="view${homeName}TabSetId" 
                styleClass="componentPanelTabSetLayout" 
		style="margin-bottom:5px;margin-top:10px;">
</#if>
<#foreach property in pojo.allPropertiesIterator>
<#if c2h.isManyToOne(property)>
<#assign parentPojo = c2j.getPOJOClass(cfg.getClassMapping(property.value.referencedEntityName))>
<#assign parentPageName = parentPojo.shortName>
<#assign parentName = util.lower(parentPojo.shortName)>

	<ice:panelTab id="view${property.name}panelTab" label="${property.name}">
   		<div class="association" id="${property.name}Parent">
        
       		 <h:outputText value="No ${property.name}" 
		                  id="view${property.name}TextId"
                   rendered="${'#'}{${homeName}.instance.${property.name} == null}"/>
   
        	<ice:dataTable  var="${parentName}" 
                   value="${'#'}{${homeName}.instance.${property.name}}" 
                rendered="${'#'}{${homeName}.instance.${property.name} != null}"
              rowClasses="rvgRowOne,rvgRowTwo"
	        columnClasses="allCols"
                      id="view${property.name}TableId">
<#foreach parentProperty in parentPojo.allPropertiesIterator>
<#if !c2h.isCollection(parentProperty) && !c2h.isManyToOne(parentProperty)>
<#if parentPojo.isComponent(parentProperty)>
<#foreach componentProperty in parentProperty.value.propertyIterator>
            <ice:column id="viewColumn${componentProperty.name}Id">
                <f:facet name="header">${componentProperty.name}</f:facet>
                ${'#'}{${parentName}.${parentProperty.name}.${componentProperty.name}}
            </ice:column>
</#foreach>
<#else>
            <ice:column id="view${parentProperty.name}Id">
                <f:facet name="header">${parentProperty.name}</f:facet>
                ${'#'}{${parentName}.${parentProperty.name}}
            </ice:column>
</#if>
</#if>
<#if c2h.isManyToOne(parentProperty)>
<#assign parentParentPojo = c2j.getPOJOClass(cfg.getClassMapping(parentProperty.value.referencedEntityName))>
<#if parentParentPojo.isComponent(parentParentPojo.identifierProperty)>
<#foreach componentProperty in parentParentPojo.identifierProperty.value.propertyIterator>
            <ice:column id="view${parentProperty.name}ColumnId">
	    	    <f:facet name="header">${parentProperty.name} ${componentProperty.name}</f:facet>
		    	${'#'}{${parentName}.${parentProperty.name}.${parentParentPojo.identifierProperty.name}.${componentProperty.name}}
            </ice:column>
</#foreach>
<#else>
            <ice:column id="view$${parentParentPojo.identifierProperty.name}ColumnName">
	    	    <f:facet name="header">${parentProperty.name} ${parentParentPojo.identifierProperty.name}</f:facet>
		    	${'#'}{${parentName}.${parentProperty.name}.${parentParentPojo.identifierProperty.name}}
            </ice:column>
</#if>
</#if>
</#foreach>
            <ice:column id="view${parentName}ColumnId">
                <f:facet name="header">action</f:facet>
                <s:link id="view${parentName}LinkId" 
                     value="View" 
                      view="/${parentPageName}.xhtml">
<#if parentPojo.isComponent(parentPojo.identifierProperty)>
<#foreach componentProperty in parentPojo.identifierProperty.value.propertyIterator>
                    <f:param name="${parentName}${util.upper(componentProperty.name)}" 
                            value="${'#'}{${parentName}.${parentPojo.identifierProperty.name}.${componentProperty.name}}"/>
</#foreach>
<#else>
                    <f:param name="${parentName}${util.upper(parentPojo.identifierProperty.name)}" 
                           value="${'#'}{${parentName}.${parentPojo.identifierProperty.name}}"/>
</#if>
                </s:link>
            </ice:column>
       </ice:dataTable>
       
    </div>
    </ice:panelTab>
</#if>
<#if c2h.isOneToManyCollection(property)>

   <ice:panelTab label="${property.name}">
    <div class="association" id="view${property.name}ChildrenId">
        
<#assign childPojo = c2j.getPOJOClass(property.value.element.associatedClass)>
<#assign childPageName = childPojo.shortName>
<#assign childEditPageName = childPojo.shortName + "Edit">
<#assign childName = util.lower(childPojo.shortName)>
<#assign childHomeName = childName + "Home">
        <h:outputText value="No ${property.name}" 
	                 id="view${property.name}ChildTextId"
                   rendered="${'#'}{empty ${homeName}.${property.name}}"/>
        
        <ice:dataTable value="${'#'}{${homeName}.${property.name}}" 
                       var="${childName}" 
                  rendered="${'#'}{not empty ${homeName}.${property.name}}" 
                rowClasses="rvgRowOne,rvgRowTwo"
		    columnClasses="allCols"
                        id="view${property.name}TableId">
<#foreach childProperty in childPojo.allPropertiesIterator>
<#if !c2h.isCollection(childProperty) && !c2h.isManyToOne(childProperty)>
<#if childPojo.isComponent(childProperty)>
<#foreach componentProperty in childProperty.value.propertyIterator>
            <ice:column id="view${componentProperty.name}Id">
                <f:facet name="header">${componentProperty.name}</f:facet>
                ${'#'}{${childName}.${childProperty.name}.${componentProperty.name}}
            </ice:column>
</#foreach>
<#else>
            <ice:column id="view${childProperty.name}Id">
                <f:facet name="header">${childProperty.name}</f:facet>
                <h:outputText id="view${childProperty.name}TextId" 
		           value="${'#'}{${childName}.${childProperty.name}}"/>
            </ice:column>
</#if>
</#if>
</#foreach>
            <ice:column>
                <f:facet name="header">action</f:facet>
                <s:link id="select${childName}LinkId" 
                     value="Select" 
                      view="/${childPageName}.xhtml">
<#if childPojo.isComponent(childPojo.identifierProperty)>
<#foreach componentProperty in childPojo.identifierProperty.value.propertyIterator>
                    <f:param name="${childName}${util.upper(componentProperty.name)}" 
                            value="${'#'}{${childName}.${childPojo.identifierProperty.name}.${componentProperty.name}}"/>
</#foreach>
<#else>
                    <f:param name="${childName}${util.upper(childPojo.identifierProperty.name)}" 
                            value="${'#'}{${childName}.${childPojo.identifierProperty.name}}"/>
</#if>
                    <f:param name="${childName}From" value="${entityName}"/>
                </s:link>
            </ice:column>
        </ice:dataTable>
        
     </div>
    
    <div class="actionButtons">
        <s:button id="viewAdd${childName}ButtonId" 
               value="Add ${childName}"
                view="/${childEditPageName}.xhtml">
            <f:param name="${componentName}${util.upper(pojo.identifierProperty.name)}" 
                    value="${'#'}{${homeName}.instance.${pojo.identifierProperty.name}}"/>
            <f:param name="${childName}From" value="${entityName}"/>
        </s:button>
    </div>        
   </ice:panelTab>
</#if>
</#foreach>
<#if hasAssociations>
</ice:panelTabSet>
</ice:form>
</#if> 
</ui:define>

</ui:composition>

