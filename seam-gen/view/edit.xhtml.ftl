<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
                             "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<#assign entityName = pojo.shortName>
<#assign componentName = util.lower(entityName)>
<#assign homeName = componentName + "Home">
<#assign masterPageName = entityName + "List">
<#assign pageName = entityName>

<ui:composition xmlns="http://www.w3.org/1999/xhtml"
                xmlns:s="http://jboss.com/products/seam/taglib"
                xmlns:ui="http://java.sun.com/jsf/facelets"
                xmlns:f="http://java.sun.com/jsf/core"
                xmlns:h="http://java.sun.com/jsf/html"
                template="layout/template.xhtml">
                       
<ui:define name="body">

    <h1>${entityName}</h1>
    <p>Generated edit page</p>
    
    <h:messages globalOnly="true" styleClass="message"/>
    
    <h:form id="${componentName}">
        <div class="dialog">
        <table>
        <s:validateAll>
<#foreach property in pojo.allPropertiesIterator>
<#if !c2h.isCollection(property) && !c2h.isManyToOne(property)>
            <tr class="prop">
                <td class="name">${property.name}</td>
                <td class="value">
                    <s:decorate>
<#if property.equals(pojo.identifierProperty)>
<#if property.value.identifierGeneratorStrategy == "assigned">
                        <h:inputText id="${property.name}"
                            value="${'#'}{${homeName}.instance.${property.name}}" 
                            disabled="${'#'}{${homeName}.managed}"/>
</#if>
<#else>
<#if property.value.typeName == "date">
			           <h:inputText id="${property.name}" value="${'#'}{${homeName}.instance.${property.name}}">
			               <f:convertDateTime type="date" dateStyle="short"/>
			           </h:inputText>
<#elseif property.value.typeName == "time">
			           <h:inputText id="${property.name}" value="${'#'}{${homeName}.instance.${property.name}}">
			               <f:convertDateTime type="time"/>
			           </h:inputText>
<#elseif property.value.typeName == "timestamp">
			           <h:inputText id="${property.name}" value="${'#'}{${homeName}.instance.${property.name}}">
			               <f:convertDateTime type="both" dateStyle="short"/>
			           </h:inputText>
<#elseif property.value.typeName == "boolean">
			           <h:selectBooleanCheckbox id="${property.name}"
			               value="${'#'}{${homeName}.instance.${property.name}}"/>
<#else>
                        <h:inputText id="${property.name}"
                            value="${'#'}{${homeName}.instance.${property.name}}"/>
</#if>
</#if>
                    </s:decorate>
                </td>
            </tr>
</#if>
</#foreach>
        </s:validateAll>
        </table>
        </div>
        <div class="actionButtons">
            <h:commandButton id="save" value="Save" 
                action="${'#'}{${homeName}.persist}"
                rendered="${'#'}{!${homeName}.managed}"/>     			  
            <h:commandButton id="update" value="Save" 
                action="${'#'}{${homeName}.update}"
                rendered="${'#'}{${homeName}.managed}"/>    			  
            <s:button id="delete" value="Delete" 
                action="${'#'}{${homeName}.remove}"
                rendered="${'#'}{${homeName}.managed}"
                propagation="end"
                view="/${masterPageName}.xhtml"/>
            <s:button id="done" value="Done"
                propagation="end" 
                view="/${pageName}.xhtml"/>
        </div>
    </h:form>
    
<#foreach property in pojo.allPropertiesIterator>

<#if c2h.isManyToOne(property)>
<#assign parentPojo = c2j.getPOJOClass(cfg.getClassMapping(property.value.referencedEntityName))>
<#assign parentPageName = parentPojo.shortName>
<#assign parentName = util.lower(parentPojo.shortName)>
           <h2>${property.name}</h2>
           <h:outputText value="No ${property.name}" rendered="${'#'}{${homeName}.instance.${property.name} == null}"/>
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
		           <s:link id="view${parentName}" value="View" view="/${parentPageName}.xhtml" propagation="end">
		               <f:param name="${parentName}Id" value="${'#'}{${parentName}.${parentPojo.identifierProperty.name}}"/>
		           </s:link>
               </h:column>
           </h:dataTable>
</#if>

<#if c2h.isOneToManyCollection(property)>
          <h2>${property.name}</h2>
<#assign childPojo = c2j.getPOJOClass(property.value.element.associatedClass)>
<#assign childPageName = childPojo.shortName>
<#assign childEditPageName = childPojo.shortName + "Edit">
<#assign childName = util.lower(childPojo.shortName)>
          <h:outputText value="No ${property.name}" rendered="${'#'}{empty ${homeName}.${property.name}}"/>
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
		        <s:link id="select${childName}" value="Select" view="/${childPageName}.xhtml" propagation="end">
		            <f:param name="${childName}Id" value="${'#'}{${childName}.${childPojo.identifierProperty.name}}"/>
		        </s:link>
            </h:column>
          </h:dataTable>
          
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
