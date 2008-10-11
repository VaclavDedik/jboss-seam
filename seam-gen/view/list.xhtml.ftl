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
                xmlns:rich="http://richfaces.org/rich"
                template="layout/template.xhtml">

<ui:define name="body">

    <h:form id="${componentName}Search" styleClass="edit">

        <rich:simpleTogglePanel label="${entityName} search filter" switchType="ajax">

<#foreach property in pojo.allPropertiesIterator>
<#if !c2h.isCollection(property) && !util.isToOne(property) && property != pojo.versionProperty!>
<#if c2j.isComponent(property)>
<#foreach componentProperty in property.value.propertyIterator>
<#if isString(componentProperty)>
            <s:decorate template="layout/display.xhtml">
                <ui:define name="label">${componentProperty.name}</ui:define>
                <h:inputText id="${componentProperty.name}" value="${'#'}{${listName}.${componentName}.${property.name}.${componentProperty.name}}"/>
            </s:decorate>

</#if>
</#foreach>
<#else>
<#if isString(property)>
            <s:decorate template="layout/display.xhtml">
                <ui:define name="label">${property.name}</ui:define>
                <h:inputText id="${property.name}" value="${'#'}{${listName}.${componentName}.${property.name}}"/>
            </s:decorate>

</#if>
</#if>
</#if>
</#foreach>

        </rich:simpleTogglePanel>

        <div class="actionButtons">
            <h:commandButton id="search" value="Search" action="/${listPageName}.xhtml"/>
        </div>

    </h:form>

    <rich:panel>
        <f:facet name="header">${entityName} search results</f:facet>
    <div class="results" id="${componentName}List">

    <h:outputText value="The ${componentName} search returned no results."
               rendered="${'#'}{empty ${listName}.resultList}"/>

    <rich:dataTable id="${listName}"
                var="${componentName}"
              value="${'#'}{${listName}.resultList}"
           rendered="${'#'}{not empty ${listName}.resultList}">
<#foreach property in pojo.allPropertiesIterator>
<#if !c2h.isCollection(property) && !util.isToOne(property) && property != pojo.versionProperty!>
<#if pojo.isComponent(property)>
<#foreach componentProperty in property.value.propertyIterator>
        <h:column>
            <f:facet name="header">
<#assign propertyPath = property.name + '.' + componentProperty.name>
                <ui:include src="layout/sort.xhtml">
                    <ui:param name="entityList" value="${'#'}{${listName}}"/>
                    <ui:param name="propertyLabel" value="${componentProperty.name}"/>
                    <ui:param name="propertyPath" value="${propertyPath}"/>
                </ui:include>
            </f:facet>
            ${'#'}{${componentName}.${property.name}.${componentProperty.name}}
        </h:column>
</#foreach>
<#else>
        <h:column>
            <f:facet name="header">
                <ui:include src="layout/sort.xhtml">
                    <ui:param name="entityList" value="${'#'}{${listName}}"/>
                    <ui:param name="propertyLabel" value="${property.name}"/>
                    <ui:param name="propertyPath" value="${property.name}"/>
                </ui:include>
            </f:facet>
            ${'#'}{${componentName}.${property.name}}
        </h:column>
</#if>
</#if>
<#if util.isToOne(property)>
<#assign parentPojo = c2j.getPOJOClass(cfg.getClassMapping(property.value.referencedEntityName))>
<#if parentPojo.isComponent(parentPojo.identifierProperty)>
<#foreach componentProperty in parentPojo.identifierProperty.value.propertyIterator>
        <h:column>
            <f:facet name="header">
<#assign propertyPath = property.name + '.' + parentPojo.identifierProperty.name + '.' + componentProperty.name>
                <ui:include src="layout/sort.xhtml">
                    <ui:param name="entityList" value="${'#'}{${listName}}"/>
                    <ui:param name="propertyLabel" value="${property.name} ${componentProperty.name}"/>
                    <ui:param name="propertyPath" value="${propertyPath}"/>
                </ui:include>
            </f:facet>
            ${'#'}{${componentName}.${propertyPath}}
        </h:column>
</#foreach>
<#else>
        <h:column>
            <f:facet name="header">
<#assign propertyPath = property.name + '.' + parentPojo.identifierProperty.name>
                <ui:include src="layout/sort.xhtml">
                    <ui:param name="entityList" value="${'#'}{${listName}}"/>
                    <ui:param name="propertyLabel" value="${property.name} ${parentPojo.identifierProperty.name}"/>
                    <ui:param name="propertyPath" value="${propertyPath}"/>
                </ui:include>
            </f:facet>
            ${'#'}{${componentName}.${propertyPath}}
        </h:column>
</#if>
</#if>
</#foreach>
        <h:column>
            <f:facet name="header">action</f:facet>
            <s:link view="/${'#'}{empty from ? '${pageName}' : from}.xhtml"
                   value="${'#'}{empty from ? 'View' : 'Select'}"
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
        </h:column>
    </rich:dataTable>

    </div>
    </rich:panel>

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

