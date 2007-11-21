<#function isTimestamp property>
	<#if property.value.typeName == "timestamp">
		<#return true>
	<#else>
		<#return false>
	</#if>
</#function>

<#function isTime property>
	<#if property.value.typeName == "time">
		<#return true>
	<#else>
		<#return false>
	</#if>
</#function>

<#function isDate property>
	<#if property.value.typeName == "date" || property.value.typeName =="java.util.Date">
		<#return true>
	<#else>
		<#return false>
	</#if>
</#function>

<#function isBigDecimal property>
	<#if property.value.typeName == "big_decimal" || property.value.typeName =="java.math.BigDecimal">
		<#return true>
	<#else>
		<#return false>
	</#if>
</#function>

<#function isBigInteger property>
	<#if property.value.typeName == "big_integer" || property.value.typeName =="java.util.BigInteger">
		<#return true>
	<#else>
		<#return false>
	</#if>
</#function>

<#function isBoolean property>
	<#if property.value.typeName == "boolean" || property.value.typeName =="yes_no" || property.value.typeName =="true_false" || property.value.typeName == "java.lang.Boolean">
		<#return true>
	<#else>
		<#return false>
	</#if>
</#function>

<#function isString property>
	<#if property.value.typeName == "string" || property.value.typeName =="java.lang.String">
		<#return true>
	<#else>
		<#return false>
	</#if>
</#function>
