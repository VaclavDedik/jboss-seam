<!DOCTYPE page PUBLIC
          "-//JBoss/Seam Pages Configuration DTD 1.1//EN"
          "http://jboss.com/products/seam/pages-1.1.dtd">

<#assign entityName = pojo.shortName>
<#assign componentName = util.lower(entityName)>
<#assign homeName = componentName + "Home">
<#assign idName = componentName + util.upper(pojo.identifierProperty.name)>
<#assign masterPageName = entityName + "List">
<#assign pageName = entityName>
<page no-conversation-view-id="/${masterPageName}.xhtml"
        conversation-required="true">
   <restrict>${'#'}{identity.loggedIn}</restrict>
   
   <param name="${componentName}From"/>
   <param name="${idName}" value="${'#'}{${homeName}.${idName}}"/>
<#include "param.xml.ftl">

   <navigation from-action="${'#'}{${homeName}.persist}">
       <redirect view-id="/${pageName}.xhtml"/>
   </navigation>
   <navigation from-action="${'#'}{${homeName}.update}">
       <redirect view-id="/${pageName}.xhtml"/>
   </navigation>
   <navigation from-action="${'#'}{${homeName}.remove}">
       <redirect view-id="/${masterPageName}.xhtml"/>
   </navigation>
   
</page>