<#include "../util/TypeInfo.ftl">

<#if !property.equals(pojo.identifierProperty) || property.value.identifierGeneratorStrategy == "assigned">
<#if c2j.isComponent(property)>
<#foreach componentProperty in property.value.propertyIterator>

        <s:decorate id="${componentProperty.name}" template="layout/display.xhtml">
            <ui:define name="label">${componentProperty.name}</ui:define>
<#if isDate(componentProperty)>
            <h:outputText value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                <s:convertDateTime type="date" dateStyle="short"/>
            </h:outputText>
<#elseif isTime(componentProperty)>
            <h:outputText value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                <s:convertDateTime type="time"/>
            </h:outputText>
<#elseif isTimestamp(componentProperty)>
            <h:outputText value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                <s:convertDateTime type="both" dateStyle="short"/>
            </h:outputText>
<#elseif isBigDecimal(componentProperty)>
            <h:outputText value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                <f:convertNumber/>
            </h:outputText>
<#elseif isBigInteger(componentProperty)>
            <h:outputText value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                <f:convertNumber integerOnly="true"/>
            </h:outputText>
<#else>
            ${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}
</#if>
        </s:decorate>
</#foreach>
<#else>

        <s:decorate id="${property.name}" template="layout/display.xhtml">
            <ui:define name="label">${property.name}</ui:define>
<#if isDate(property)>
            <h:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                <s:convertDateTime type="date" dateStyle="short"/>
            </h:outputText>
<#elseif isTime(property)>
            <h:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                <s:convertDateTime type="time"/>
            </h:outputText>
<#elseif isTimestamp(property)>
            <h:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                <s:convertDateTime type="both" dateStyle="short"/>
            </h:outputText>
<#elseif isBigDecimal(property)>
            <h:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                <f:convertNumber/>
            </h:outputText>
<#elseif isBigInteger(property)>
            <h:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                <f:convertNumber integerOnly="true"/>
            </h:outputText>
<#else>
            ${'#'}{${homeName}.instance.${property.name}}
</#if>
        </s:decorate>
</#if>
</#if>
