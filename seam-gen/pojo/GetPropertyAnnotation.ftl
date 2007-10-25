<#include "Ejb3PropertyGetAnnotation.ftl"/>
<#if !property.optional && ( !property.equals(pojo.identifierProperty) || property.value.identifierGeneratorStrategy == "assigned" )>
    @${pojo.importType("org.hibernate.validator.NotNull")}
</#if>
<#if property.columnSpan==1>
<#assign column = property.getColumnIterator().next()/>
<#if !c2h.isManyToOne(property) && !c2h.isTemporalValue(property) && column.length!=255>
    @${pojo.importType("org.hibernate.validator.Length")}(max=${column.length?c})
</#if>
</#if>
