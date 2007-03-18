<#if !property.equals(pojo.identifierProperty) || property.value.identifierGeneratorStrategy == "assigned">
<#if c2j.isComponent(property)>
<#foreach componentProperty in property.value.propertyIterator>
<#assign propertyType = componentProperty.value.typeName>

            <s:span id="${componentProperty.name}">
                <f:facet name="label">${componentProperty.name}</f:facet>
<#if propertyType == "date">
                <h:outputText value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                    <s:convertDateTime type="date" dateStyle="short"/>
                </h:outputText>
<#elseif propertyType == "time">
                <h:outputText value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                    <s:convertDateTime type="time"/>
                </h:outputText>
<#elseif propertyType == "timestamp">
                <h:outputText value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                    <s:convertDateTime type="both" dateStyle="short"/>
                </h:outputText>
<#elseif propertyType == "big_decimal">
                <h:outputText value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                    <f:convertNumber/>
                </h:outputText>
<#elseif propertyType == "big_integer">
                <h:outputText value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                    <f:convertNumber integerOnly="true"/>
                </h:outputText>
<#else>
                ${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}
</#if>
            </s:span>
</#foreach>
<#else>
<#assign propertyType = property.value.typeName>

            <s:span id="${property.name}">
                <f:facet name="label">${property.name}</f:facet>
<#if propertyType == "date">
                <h:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                    <s:convertDateTime type="date" dateStyle="short"/>
                </h:outputText>
<#elseif propertyType == "time">
                <h:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                    <s:convertDateTime type="time"/>
                </h:outputText>
<#elseif propertyType == "timestamp">
                <h:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                    <s:convertDateTime type="both" dateStyle="short"/>
                </h:outputText>
<#elseif propertyType == "big_decimal">
                <h:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                    <f:convertNumber/>
                </h:outputText>
<#elseif propertyType == "big_integer">
                <h:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                    <f:convertNumber integerOnly="true"/>
                </h:outputText>
<#else>
                ${'#'}{${homeName}.instance.${property.name}}
</#if>
            </s:span>
</#if>
</#if>
