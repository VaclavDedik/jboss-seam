<#if !property.equals(pojo.identifierProperty) || property.value.identifierGeneratorStrategy == "assigned">
<#if c2j.isComponent(property)>
<#foreach componentProperty in property.value.propertyIterator>
<#assign propertyType = componentProperty.value.typeName>

            <tr class="prop">
                <td class="name">${componentProperty.name}</td>
                <td class="value" id="${componentProperty.name}">
<#if propertyType == "date">
                    <h:outputText value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                        <f:convertDateTime type="date" dateStyle="short"/>
                    </h:outputText>
<#elseif propertyType == "time">
                    <h:outputText value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                        <f:convertDateTime type="time"/>
                    </h:outputText>
<#elseif propertyType == "timestamp">
                    <h:outputText value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                        <f:convertDateTime type="both" dateStyle="short"/>
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
                </td>
            </tr>
</#foreach>
<#else>
<#assign propertyType = property.value.typeName>

            <tr class="prop">
                <td class="name">${property.name}</td>
                <td class="value" id="${property.name}">
<#if propertyType == "date">
                    <h:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                        <f:convertDateTime type="date" dateStyle="short"/>
                    </h:outputText>
<#elseif propertyType == "time">
                    <h:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                        <f:convertDateTime type="time"/>
                    </h:outputText>
<#elseif propertyType == "timestamp">
                    <h:outputText value="${'#'}{${homeName}.instance.${property.name}}">
                        <f:convertDateTime type="both" dateStyle="short"/>
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
                </td>
            </tr>
</#if>
</#if>
