<!DOCTYPE page PUBLIC
          "-//JBoss/Seam Pages Configuration DTD 1.1//EN"
          "http://jboss.com/products/seam/pages-1.1.dtd">

<#assign entityName = pojo.shortName>
<#assign componentName = util.lower(entityName)>
<#assign listName = componentName + "List">
<page>
   <param name="firstResult" value="${'#'}{${listName}.firstResult}"/>
   <param name="order" value="${'#'}{${listName}.order}"/>
   <param name="from"/>
<#foreach property in pojo.allPropertiesIterator>
<#if !c2h.isCollection(property) && !c2h.isManyToOne(property)>
<#if c2j.isComponent(property)>
<#foreach componentProperty in property.value.propertyIterator>
<#if componentProperty.value.typeName == "string">
   <param name="${componentProperty.name}" value="${'#'}{${listName}.${componentName}.${property.name}.${componentProperty.name}}"/>
</#if>
</#foreach>
<#else>
<#if property.value.typeName == "string">
   <param name="${property.name}" value="${'#'}{${listName}.${componentName}.${property.name}}"/>
</#if>
</#if>
</#if>
</#foreach>
</page>