<!DOCTYPE page PUBLIC
          "-//JBoss/Seam Pages Configuration DTD 1.1//EN"
          "http://jboss.com/products/seam/pages-1.1.dtd">

<#assign entityName = pojo.shortName>
<#assign componentName = util.lower(entityName)>
<#assign homeName = componentName + "Home">
<#assign idName = componentName + util.upper(pojo.identifierProperty.name)>
<page>
   <param name="${componentName}From"/>
   <param name="${idName}"
         value="${'#'}{${homeName}.${idName}}"/>
<#include "param.xml.ftl">
</page>