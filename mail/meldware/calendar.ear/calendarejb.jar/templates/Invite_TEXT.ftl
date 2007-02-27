Invitation for: "${invite.event.title}"-##-BODY-##-
<#setting url_escaping_charset='ISO-8859-1'>
Hi ${invite.user.userName}

You are invited <#if invite.event.title?exists>for ${invite.event.title} </#if>	<#if invite.event.location?exists>at ${invite.event.location}</#if>.
The event is from ${invite.event.startDate} until ${invite.event.endDate}.
<#if invite.event.organizer?exists>
Organized by <#list invite.event.organizer as organizer>${organizer.userName} </#list>. 
</#if>
