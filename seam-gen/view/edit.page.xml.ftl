<!DOCTYPE page PUBLIC
          "-//JBoss/Seam Pages Configuration DTD 1.3//EN"
          "http://jboss.com/products/seam/pages-1.3.dtd">

<#assign entityName = pojo.shortName>
<#assign componentName = util.lower(entityName)>
<#assign homeName = componentName + "Home">
<#assign masterPageName = entityName + "List">
<#assign pageName = entityName>
<page no-conversation-view-id="/${masterPageName}.xhtml"
               login-required="true">
   
   <begin-conversation join="true"/>
   
   <action execute="${'#'}{${homeName}.wire}"/>
   
   <param name="${componentName}From"/>
<#assign idName = componentName + util.upper(pojo.identifierProperty.name)>
<#if c2j.isComponent(pojo.identifierProperty)>
<#foreach componentProperty in pojo.identifierProperty.value.propertyIterator>
<#assign cidName = componentName + util.upper(componentProperty.name)>
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
