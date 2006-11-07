<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
                             "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<#assign entityName = pojo.shortName>
<#assign componentName = util.lower(entityName)>
<#assign homeName = componentName + "Home">
<#assign masterPageName = entityName + "List">

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
        <s:validateAll>
<#foreach property in pojo.allPropertiesIterator>
<#if !c2h.isCollection(property) && !c2h.isManyToOne(property)>
            <div class="prop">
                <span class="name">${property.name}</span>
                <span class="value">
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
                </span>
            </div>
</#if>
</#foreach>
        </s:validateAll>
        </div>
        <div class="actionButtons">
            <h:commandButton id="save" value="Save" 
                action="${'#'}{${homeName}.persist}"
                rendered="${'#'}{!${homeName}.managed}"/>     			  
            <h:commandButton id="update" value="Save" 
                action="${'#'}{${homeName}.update}"
                rendered="${'#'}{${homeName}.managed}"/>    			  
            <h:commandButton id="delete" value="Delete" 
                action="${'#'}{${homeName}.remove}"
                rendered="${'#'}{${homeName}.managed}"/>
            <s:link id="done" value="Done" linkStyle="button"
                propagation="end" view="/${masterPageName}.xhtml"/>			  
        </div>
    </h:form>
    
</ui:define>

</ui:composition>
