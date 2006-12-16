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
    
    <h:messages globalOnly="true" styleClass="message" id="globalMessages"/>
    
    <h:form id="${componentName}" styleClass="edit">
    
        <div class="dialog">
            <table>
                <s:validateAll>
<#foreach property in pojo.allPropertiesIterator>
<#if !c2h.isCollection(property) && !c2h.isManyToOne(property) && property.columnSpan==1>
<#assign propertyIsId = property.equals(pojo.identifierProperty)>
<#if !propertyIsId || property.value.identifierGeneratorStrategy == "assigned">
<#assign column = property.columnIterator.next()>

                    <tr class="prop">
                        <td class="name">${property.name}</td>
                        <td class="value">
                            <s:decorate>
<#if property.value.typeName == "date">
	        		           <h:inputText id="${property.name}" 
	        		                 maxlength="10"
	        		                      size="10"
<#if propertyIsId>
                                      disabled="${'#'}{${homeName}.managed}"
</#if>
<#if !column.nullable>
                                      required="true"
</#if>
		        	                     value="${'#'}{${homeName}.instance.${property.name}}">
		        	               <f:convertDateTime type="date" dateStyle="short" pattern="MM/dd/yyyy"/>
		        	           </h:inputText>
		        	           <s:selectDate for="${property.name}">
		        	               <h:graphicImage url="img/dtpick.gif" style="margin-left:5px"/>
		        	           </s:selectDate>
<#elseif property.value.typeName == "time">
		        	           <h:inputText id="${property.name}" 
	        		                      size="5"
<#if !column.nullable>
                                      required="true"
</#if>
		        	                     value="${'#'}{${homeName}.instance.${property.name}}">
		        	               <f:convertDateTime type="time"/>
		        	           </h:inputText>
<#elseif property.value.typeName == "timestamp">
		        	           <h:inputText id="${property.name}" 
	        		                      size="16"
<#if !column.nullable>
                                      required="true"
</#if>
			                             value="${'#'}{${homeName}.instance.${property.name}}">
			                       <f:convertDateTime type="both" dateStyle="short"/>
			                   </h:inputText>
<#elseif property.value.typeName == "big_decimal">
			                   <h:inputText id="${property.name}" 
<#if !column.nullable>
                                      required="true"
</#if>
			                             value="${'#'}{${homeName}.instance.${property.name}}"
			                              size="${column.precision+7}"/>
<#elseif property.value.typeName == "big_integer">
			                   <h:inputText id="${property.name}" 
<#if propertyIsId>
                                      disabled="${'#'}{${homeName}.managed}"
</#if>
<#if !column.nullable>
                                      required="true"
</#if>
			                             value="${'#'}{${homeName}.instance.${property.name}}"
			                              size="${column.precision+6}"/>
<#elseif property.value.typeName == "boolean">
			                   <h:selectBooleanCheckbox id="${property.name}"
<#if !column.nullable>
                                                  required="true"
</#if>
		        	                                 value="${'#'}{${homeName}.instance.${property.name}}"/>
<#elseif property.value.typeName == "string">
<#if column.length gt 160>
<#if column.length gt 800>
<#assign rows = 10>
<#else>
<#assign rows = (column.length/80)?int>
</#if>
	        		           <h:inputTextarea id="${property.name}"
	        		                           cols="80"
	        		                           rows="${rows}"
<#if propertyIsId>
                                         disabled="${'#'}{${homeName}.managed}"
</#if>
<#if !column.nullable>
                                          required="true"
</#if>
		        	                         value="${'#'}{${homeName}.instance.${property.name}}"/>
<#else>
<#if column.length gt 100>
<#assign size = 100>
<#else>
<#assign size = column.length>
</#if>
                               <h:inputText id="${property.name}" 
<#if propertyIsId>
                                      disabled="${'#'}{${homeName}.managed}"
</#if>
<#if !column.nullable>
                                      required="true"
</#if>
                                          size="${size}"
                                     maxlength="${column.length}"
                                         value="${'#'}{${homeName}.instance.${property.name}}"/>
</#if>
<#else>
		        	           <h:inputText id="${property.name}"
<#if !column.nullable>
                                      required="true"
</#if>
<#if propertyIsId>
                                      disabled="${'#'}{${homeName}.managed}"
</#if>
			                             value="${'#'}{${homeName}.instance.${property.name}}"/>
</#if>
                            </s:decorate>
                        </td>
                    </tr>
</#if>
</#if>
</#foreach>

               </s:validateAll>
            </table>
        </div>
        
        <div class="actionButtons">
        
            <h:commandButton id="save" 
                          value="Save" 
                         action="${'#'}{${homeName}.persist}"
                       rendered="${'#'}{!${homeName}.managed}"/>  
                          			  
            <h:commandButton id="update" 
                          value="Save" 
                         action="${'#'}{${homeName}.update}"
                       rendered="${'#'}{${homeName}.managed}"/>
                        			  
            <s:button id="delete" 
                   value="Delete" 
                  action="${'#'}{${homeName}.remove}"
                rendered="${'#'}{${homeName}.managed}"
             propagation="end"
                    view="/${masterPageName}.xhtml"/>
                    
            <s:button id="done" 
                   value="Done"
             propagation="end" 
                    view="/${pageName}.xhtml"
                rendered="${'#'}{${homeName}.managed}"/>
                
            <s:button id="cancel" 
                   value="Cancel"
             propagation="end" 
                    view="/${masterPageName}.xhtml"
                rendered="${'#'}{!${homeName}.managed}"/>
                
        </div>
        
    </h:form>
<#foreach property in pojo.allPropertiesIterator>
<#if c2h.isManyToOne(property)>
<#assign parentPojo = c2j.getPOJOClass(cfg.getClassMapping(property.value.referencedEntityName))>
<#assign parentPageName = parentPojo.shortName>
<#assign parentName = util.lower(parentPojo.shortName)>

    <div class="association" id="${property.name}">
    
        <h3>${property.name}</h3>
    
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
                <s:link view="/${parentPageName}.xhtml" 
                         id="view${parentName}" 
                      value="View" 
                propagation="none">
                    <f:param name="${parentName}${util.upper(parentPojo.identifierProperty.name)}" 
                            value="${'#'}{${parentName}.${parentPojo.identifierProperty.name}}"/>
                </s:link>
            </h:column>
        </h:dataTable>

    </div>
</#if>
<#if c2h.isOneToManyCollection(property)>

    <f:subview rendered="${'#'}{${homeName}.managed}" id="${property.name}">
    
        <div class="association" id="${property.name}">
        
            <h3>${property.name}</h3>
        
<#assign childPojo = c2j.getPOJOClass(property.value.element.associatedClass)>
<#assign childPageName = childPojo.shortName>
<#assign childEditPageName = childPojo.shortName + "Edit">
<#assign childName = util.lower(childPojo.shortName)>
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
                    <s:link view="/${childPageName}.xhtml" 
                              id="select${childName}" 
                           value="Select"
                     propagation="none">
                        <f:param name="${childName}${util.upper(childPojo.identifierProperty.name)}" 
                                value="${'#'}{${childName}.${childPojo.identifierProperty.name}}"/>
                        <f:param name="${childName}From" value="${entityName}"/>
                    </s:link>
                </h:column>
            </h:dataTable>
        
        </div>
          
        <div class="actionButtons">
            <s:button id="add${childName}" 
                   value="Add ${childName}"
                    view="/${childEditPageName}.xhtml" 
             propagation="begin">
                 <f:param name="${componentName}${util.upper(pojo.identifierProperty.name)}" 
                         value="${'#'}{${homeName}.instance.${pojo.identifierProperty.name}}"/>
                 <f:param name="${childName}From" value="${entityName}"/>
            </s:button>
        </div>
        
    </f:subview>
</#if>
</#foreach>
    
</ui:define>

</ui:composition>
