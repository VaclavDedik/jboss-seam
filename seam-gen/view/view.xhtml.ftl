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
                template="layout/template.xhtml">
                       
<ui:define name="body">

    <h1>${entityName}</h1>
    <p>Generated view page</p>
    
    <h:messages globalOnly="true" styleClass="message" id="globalMessages"/>
    
    <div id="${componentName}" class="dialog">
        <h:panelGrid columns="2" rowClasses="prop" columnClasses="name,value">
<#foreach property in pojo.allPropertiesIterator>
<#if !c2h.isCollection(property) && !c2h.isManyToOne(property)>
<#include "viewproperty.xhtml.ftl">
</#if>
</#foreach>

        </h:panelGrid>
    </div>
    
    <div class="actionButtons">      

        <s:button view="/${editPageName}.xhtml" 
                    id="edit" 
                 value="Edit"/>

        <s:button view="/${'#'}{empty ${componentName}From ? '${masterPageName}' : ${componentName}From}.xhtml"
                    id="done"
                 value="Done"/>

    </div>
<#foreach property in pojo.allPropertiesIterator>
<#if c2h.isManyToOne(property)>
<#assign parentPojo = c2j.getPOJOClass(cfg.getClassMapping(property.value.referencedEntityName))>
<#assign parentPageName = parentPojo.shortName>
<#assign parentName = util.lower(parentPojo.shortName)>

    <div class="association" id="${property.name}Parent">
    
        <h3>${property.name}</h3>
        
        <h:outputText value="No ${property.name}" 
                   rendered="${'#'}{${homeName}.instance.${property.name} == null}"/>
        
        <h:dataTable var="${parentName}" 
                   value="${'#'}{${homeName}.instance.${property.name}}" 
                rendered="${'#'}{${homeName}.instance.${property.name} != null}"
              rowClasses="rvgRowOne,rvgRowTwo"
                      id="${property.name}Table">
<#foreach parentProperty in parentPojo.allPropertiesIterator>
<#if !c2h.isCollection(parentProperty) && !c2h.isManyToOne(parentProperty)>
<#if parentPojo.isComponent(parentProperty)>
<#foreach componentProperty in parentProperty.value.propertyIterator>
            <h:column>
                <f:facet name="header">${componentProperty.name}</f:facet>
                ${'#'}{${parentName}.${parentProperty.name}.${componentProperty.name}}
            </h:column>
</#foreach>
<#else>
            <h:column>
                <f:facet name="header">${parentProperty.name}</f:facet>
                ${'#'}{${parentName}.${parentProperty.name}}
            </h:column>
</#if>
</#if>
<#if c2h.isManyToOne(parentProperty)>
<#assign parentParentPojo = c2j.getPOJOClass(cfg.getClassMapping(parentProperty.value.referencedEntityName))>
            <h:column>
                <f:facet name="header">${parentProperty.name} ${parentParentPojo.identifierProperty.name}</f:facet>
                ${'#'}{${parentName}.${parentProperty.name}.${parentParentPojo.identifierProperty.name}}
            </h:column>
</#if>
</#foreach>
            <h:column>
                <f:facet name="header">action</f:facet>
                <s:link id="view${parentName}" 
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
            </h:column>
        </h:dataTable>
        
    </div>
</#if>
<#if c2h.isOneToManyCollection(property)>

    <div class="association" id="${property.name}Children">
    
        <h3>${property.name}</h3>
        
<#assign childPojo = c2j.getPOJOClass(property.value.element.associatedClass)>
<#assign childPageName = childPojo.shortName>
<#assign childEditPageName = childPojo.shortName + "Edit">
<#assign childName = util.lower(childPojo.shortName)>
<#assign childHomeName = childName + "Home">
        <h:outputText value="No ${property.name}" 
                   rendered="${'#'}{empty ${homeName}.${property.name}}"/>
        
        <h:dataTable value="${'#'}{${homeName}.${property.name}}" 
                       var="${childName}" 
                  rendered="${'#'}{not empty ${homeName}.${property.name}}" 
                rowClasses="rvgRowOne,rvgRowTwo"
                        id="${property.name}Table">
<#foreach childProperty in childPojo.allPropertiesIterator>
<#if !c2h.isCollection(childProperty) && !c2h.isManyToOne(childProperty)>
<#if childPojo.isComponent(childProperty)>
<#foreach componentProperty in childProperty.value.propertyIterator>
            <h:column>
                <f:facet name="header">${componentProperty.name}</f:facet>
                ${'#'}{${childName}.${childProperty.name}.${componentProperty.name}}
            </h:column>
</#foreach>
<#else>
            <h:column>
                <f:facet name="header">${childProperty.name}</f:facet>
                <h:outputText value="${'#'}{${childName}.${childProperty.name}}"/>
            </h:column>
</#if>
</#if>
</#foreach>
            <h:column>
                <f:facet name="header">action</f:facet>
                <s:link id="select${childName}" 
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
            </h:column>
        </h:dataTable>
        
    </div>

    <div class="actionButtons">
        <s:button id="add${childName}" 
               value="Add ${childName}"
                view="/${childEditPageName}.xhtml">
            <f:param name="${componentName}${util.upper(pojo.identifierProperty.name)}" 
                    value="${'#'}{${homeName}.instance.${pojo.identifierProperty.name}}"/>
            <f:param name="${childName}From" value="${entityName}"/>
        </s:button>
    </div>        
</#if>
</#foreach>
    
</ui:define>

</ui:composition>
