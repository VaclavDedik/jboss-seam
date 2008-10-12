<?xml version="1.0" encoding="UTF-8"?>
<#assign entityName = pojo.shortName>
<#assign componentName = entityName?uncap_first>
<#assign homeName = componentName + "Home">
<#assign masterPageName = entityName + "List">
<#assign pageName = entityName>
<page xmlns="http://jboss.com/products/seam/pages"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://jboss.com/products/seam/pages http://jboss.com/products/seam/pages-2.1.xsd"
      no-conversation-view-id="/${masterPageName}.xhtml"
      login-required="true">

   <begin-conversation join="true"/>

   <action execute="${'#'}{${homeName}.wire}"/>

   <param name="${componentName}From"/>
<#assign idName = componentName + pojo.identifierProperty.name?cap_first>
<#if c2j.isComponent(pojo.identifierProperty)>
<#foreach componentProperty in pojo.identifierProperty.value.propertyIterator>
<#assign cidName = componentName + componentProperty.name?cap_first>
   <param name="${cidName}" value="${'#'}{${homeName}.${idName}.${componentProperty.name}}"/>
</#foreach>
<#else>
   <param name="${idName}" value="${'#'}{${homeName}.${idName}}"/>
</#if>
<#assign entities=util.set()>
<#if entities.add(pojo.shortName)>
<#include "param.xml.ftl">
</#if>

   <navigation from-action="${'#'}{${homeName}.persist}">
       <end-conversation/>
       <redirect view-id="/${pageName}.xhtml"/>
   </navigation>

   <navigation from-action="${'#'}{${homeName}.update}">
       <end-conversation/>
       <redirect view-id="/${pageName}.xhtml"/>
   </navigation>

   <navigation from-action="${'#'}{${homeName}.remove}">
       <end-conversation/>
       <redirect view-id="/${masterPageName}.xhtml"/>
   </navigation>

</page>
