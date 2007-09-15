<#if !property.equals(pojo.identifierProperty) || property.value.identifierGeneratorStrategy == "assigned">
<#if c2j.isComponent(property)>
<#foreach componentProperty in property.value.propertyIterator>
<#assign propertyType = componentProperty.value.typeName>

        <s:decorate id="${componentProperty.name}" template="layout/display.xhtml">
            <ui:define name="label">${componentProperty.name}</ui:define>
<#if propertyType == "date">
            <ice:outputText id="view${componentProperty.name}TextId"
	                 value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                <s:convertDateTime type="date" dateStyle="short"/>
            </ice:outputText>
<#elseif propertyType == "time">
             <ice:outputText id="view${componentProperty.name}TextId" 
	                  value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                <s:convertDateTime type="time"/>
             </ice:outputText>
<#elseif propertyType == "timestamp">
            <ice:outputText id="view${componentProperty.name}TextId"
	                 value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                <s:convertDateTime type="both" dateStyle="short"/>
            </ice:outputText>
<#elseif propertyType == "big_decimal">
            <ice:outputText id="view${componentProperty.name}TextId"
	                 value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                <f:convertNumber/>
            </ice:outputText>
<#elseif propertyType == "big_integer">
            <ice:outputText id="view${componentProperty.name}TextId"
	                 value="${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}">
                <f:convertNumber integerOnly="true"/>
            </ice:outputText>
<#else>
            ${'#'}{${homeName}.instance.${property.name}.${componentProperty.name}}&#160;
</#if>
        </s:decorate>
</#foreach>
<#else>
<#assign propertyType = property.value.typeName>

        <s:decorate id="${property.name}" template="layout/display.xhtml">
            <ui:define name="label">${property.name}</ui:define>
<#if propertyType == "date">
            <ice:outputText id="view${property.name}TextId"
	                 value="${'#'}{${homeName}.instance.${property.name}}">
                <s:convertDateTime type="date" dateStyle="short"/>
            </ice:outputText>
<#elseif propertyType == "time">
            <ice:outputText id="view${property.name}TextId"
	                 value="${'#'}{${homeName}.instance.${property.name}}">
                <s:convertDateTime type="time"/>
            </ice:outputText>
<#elseif propertyType == "timestamp">
            <ice:outputText id="view${property.name}TextId"
	                 value="${'#'}{${homeName}.instance.${property.name}}">
                <s:convertDateTime type="both" dateStyle="short"/>
            </ice:outputText>
<#elseif propertyType == "big_decimal">
            <ice:outputText id="view${property.name}TextId"
	                 value="${'#'}{${homeName}.instance.${property.name}}">
                <f:convertNumber/>
            </ice:outputText>
<#elseif propertyType == "big_integer">
            <ice:outputText id="view${property.name}TextId"
	                 value="${'#'}{${homeName}.instance.${property.name}}">
                <f:convertNumber integerOnly="true"/>
            </ice:outputText>
<#else>
            ${'#'}{${homeName}.instance.${property.name}}&#160;
</#if>
        </s:decorate>
</#if>
</#if>
