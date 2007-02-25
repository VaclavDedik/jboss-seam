<#foreach property in pojo.allPropertiesIterator>
<#if c2h.isManyToOne(property)>
<#assign parentPojo = c2j.getPOJOClass(cfg.getClassMapping(property.value.referencedEntityName))>
<#if entities.add(parentPojo.shortName)>
<#assign parentComponentName = util.lower(parentPojo.shortName)>
<#assign parentHomeName = parentComponentName + "Home">
   <param name="${parentComponentName}From"/>
<#assign parentIdName = parentComponentName + util.upper(parentPojo.identifierProperty.name)>
<#if c2j.isComponent(parentPojo.identifierProperty)>
<#foreach parentComponentProperty in parentPojo.identifierProperty.value.propertyIterator>
<#assign parentCidName = parentComponentName + util.upper(parentComponentProperty.name)>
   <param name="${parentCidName}" value="${'#'}{${parentHomeName}.${parentIdName}.${parentComponentProperty.name}}"/>
</#foreach>
<#else>
   <param name="${parentIdName}" value="${'#'}{${parentHomeName}.${parentIdName}}"/>
</#if>
<#assign p = pojo>
<#assign pojo = parentPojo>
<#include "param.xml.ftl">
<#assign pojo = p>
</#if>
</#if>
</#foreach>

