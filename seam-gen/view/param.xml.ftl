<#foreach property in pojo.allPropertiesIterator>
<#if c2h.isManyToOne(property)>
<#assign parentPojo = c2j.getPOJOClass(cfg.getClassMapping(property.value.referencedEntityName))>
<#assign parentComponentName = util.lower(parentPojo.shortName)>
<#assign parentHomeName = parentComponentName + "Home">
<#assign parentIdName = parentComponentName + util.upper(parentPojo.identifierProperty.name)>
   <param name="${parentComponentName}From"/>
   <param name="${parentIdName}" value="${'#'}{${parentHomeName}.${parentIdName}}"/>
<#assign p = pojo>
<#assign pojo = parentPojo>
<#include "param.xml.ftl">
<#assign pojo = p>
</#if>
</#foreach>
