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
        <table>
<#foreach property in pojo.allPropertiesIterator>
<#if !c2h.isCollection(property) && !c2h.isManyToOne(property)>
<#if !property.equals(pojo.identifierProperty) || property.value.identifierGeneratorStrategy == "assigned">

            <tr class="prop">
                <td class="name">${property.name}</td>
                <td class="value" id="${property.name}">
<#if property.value.typeName == "date">
                    <h:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                        <f:convertDateTime type="date" dateStyle="short"/>
                    </h:outputText>
<#elseif property.value.typeName == "time">
                    <h:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                        <f:convertDateTime type="time"/>
                    </h:outputText>
<#elseif property.value.typeName == "timestamp">
                    <h:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                        <f:convertDateTime type="both" dateStyle="short"/>
                    </h:outputText>
<#elseif property.value.typeName == "big_decimal">
                    <h:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                        <f:convertNumber/>
                    </h:outputText>
<#elseif property.value.typeName == "big_integer">
                    <h:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                        <f:convertNumber integerOnly="true"/>
                    </h:outputText>
<#else>
                    ${'#'}{${homeName}.instance.${property.name}}
</#if>
                </td>
            </tr>
</#if>
</#if>
</#foreach>

        </table>
    </div>
    
    <div class="actionButtons">
           
        <s:button id="edit" value="Edit" 
                view="/${editPageName}.xhtml" 
         propagation="begin">
            <f:param name="${componentName}Id" 
                    value="${'#'}{${homeName}.instance.${pojo.identifierProperty.name}}"/>
        </s:button>
        
        <s:button id="delete" value="Delete" 
              action="${'#'}{${homeName}.remove}"
            rendered="${'#'}{${homeName}.managed}"
                view="/${masterPageName}.xhtml">
            <f:param name="${componentName}Id" 
                    value="${'#'}{${homeName}.instance.${pojo.identifierProperty.name}}"/>
        </s:button>
        
    </div>
<#foreach property in pojo.allPropertiesIterator>
<#if c2h.isManyToOne(property)>
<#assign parentPojo = c2j.getPOJOClass(cfg.getClassMapping(property.value.referencedEntityName))>
<#assign parentPageName = parentPojo.shortName>
<#assign parentName = util.lower(parentPojo.shortName)>

    <div class="association" id="${property.name}">
    
        <h2>${property.name}</h2>
        
        <h:outputText value="No ${property.name}" 
                   rendered="${'#'}{${homeName}.instance.${property.name} == null}"/>
        
        <h:dataTable var="${parentName}" 
                   value="${'#'}{${homeName}.instance.${property.name}}" 
                rendered="${'#'}{${homeName}.instance.${property.name} != null}"
              rowClasses="rvgRowOne,rvgRowTwo"
                      id="${property.name}">
<#foreach parentProperty in parentPojo.allPropertiesIterator>
<#if !c2h.isCollection(parentProperty) && !c2h.isManyToOne(parentProperty)>
            <h:column>
                <f:facet name="header">${parentProperty.name}</f:facet>
                ${'#'}{${parentName}.${parentProperty.name}}
            </h:column>
</#if>
<#if c2h.isManyToOne(parentProperty)>
<#assign parentParentPojo = c2j.getPOJOClass(cfg.getClassMapping(parentProperty.value.referencedEntityName))>
            <h:column>
                <f:facet name="header">${parentProperty.name} ${parentParentPojo.identifierProperty.name}</f:facet>
                ${'#'}{${parentName}.${parentProperty.name}.${parentPojo.identifierProperty.name}}
            </h:column>
</#if>
</#foreach>
            <h:column>
                <f:facet name="header">action</f:facet>
                <s:link id="view${parentName}" value="View" view="/${parentPageName}.xhtml">
                    <f:param name="${parentName}Id" value="${'#'}{${parentName}.${parentPojo.identifierProperty.name}}"/>
                </s:link>
            </h:column>
        </h:dataTable>
        
    </div>
</#if>
<#if c2h.isOneToManyCollection(property)>

    <div class="association" id="${property.name}">
    
        <h2>${property.name}</h2>
        
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
                        id="${property.name}">
<#foreach childProperty in childPojo.allPropertiesIterator>
<#if !c2h.isCollection(childProperty) && !c2h.isManyToOne(childProperty)>
            <h:column>
                <f:facet name="header">${childProperty.name}</f:facet>
                <h:outputText value="${'#'}{${childName}.${childProperty.name}}"/>
            </h:column>
</#if>
</#foreach>
            <h:column>
                <f:facet name="header">action</f:facet>
                <s:link id="select${childName}" value="Select" view="/${childPageName}.xhtml">
                    <f:param name="${childName}Id" value="${'#'}{${childName}.${childPojo.identifierProperty.name}}"/>
                </s:link>
            </h:column>
        </h:dataTable>
        
    </div>

    <div class="actionButtons">
        <s:button id="add${childName}" value="Add ${childName}"
                view="/${childEditPageName}.xhtml" propagation="begin">
            <f:param name="${componentName}Id" 
                    value="${'#'}{${homeName}.instance.${pojo.identifierProperty.name}}"/>
        </s:button>
    </div>        
</#if>
</#foreach>
    
</ui:define>

</ui:composition>
