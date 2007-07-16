<#if !property.equals(pojo.identifierProperty) || property.value.identifierGeneratorStrategy == "assigned">
<#if c2j.isComponent(property)>
<#foreach componentProperty in property.value.propertyIterator>
<#assign propertyType = componentProperty.value.typeName>

                <ice:outputLabel>${componentProperty.name}</ice:outputLabel>
<#if propertyType == "date">
            <ice:outputText value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                <s:convertDateTime type="date" dateStyle="short"/>
              </ice:outputText>
<#elseif propertyType == "time">
             <ice:outputText value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                <s:convertDateTime type="time"/>
             </ice:outputText>
<#elseif propertyType == "timestamp">
            <ice:outputText value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                <s:convertDateTime type="both" dateStyle="short"/>
            </ice:outputText>
<#elseif propertyType == "big_decimal">
            <ice:outputText value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                <f:convertNumber/>
            </ice:outputText>
<#elseif propertyType == "big_integer">
            <ice:outputText value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                <f:convertNumber integerOnly="true"/>
            </ice:outputText>
<#else>
            <ice:outputText value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}"/>
</#if>
        
</#foreach>
<#else>
<#assign propertyType = property.value.typeName>

                  <ice:outputLabel>${property.name}</ice:outputLabel>
<#if propertyType == "date">
            <ice:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                <s:convertDateTime type="date" dateStyle="short"/>
            </ice:outputText>
<#elseif propertyType == "time">
            <ice:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                <s:convertDateTime type="time"/>
            </ice:outputText>
<#elseif propertyType == "timestamp">
            <ice:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                <s:convertDateTime type="both" dateStyle="short"/>
            </ice:outputText>
<#elseif propertyType == "big_decimal">
            <ice:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                <f:convertNumber/>
            </ice:outputText>
<#elseif propertyType == "big_integer">
            <ice:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                <f:convertNumber integerOnly="true"/>
            </ice:outputText>
<#else>
            <ice:outputText value="${'#'}{${homeName}.instance.${property.name}}"/>

</#if>
</#if>
</#if>
