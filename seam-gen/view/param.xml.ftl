<#foreach property in pojo.allPropertiesIterator>
<#if c2h.isManyToOne(property)>
<#assign parentPojo = c2j.getPOJOClass(cfg.getClassMapping(property.value.referencedEntityName))>
<#assign componentName = util.lower(parentPojo.shortName)>
<#assign homeName = componentName + "Home">
<#assign idName = componentName + util.upper(parentPojo.identifierProperty.name)>
   <param name="${componentName}From"/>
   <param name="${idName}"
         value="${'#'}{${homeName}.${idName}}"/>
<#assign p = pojo>
<#assign pojo = parentPojo>
<#include "param.xml.ftl">
<#assign pojo = p>
</#if>
</#foreach>
