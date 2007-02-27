RSVP for: ${invite.event.title}-##-BODY-##-
<#setting url_escaping_charset='ISO-8859-1'>
User ${invite.user.userName} has <#switch invite.status><#case 0>become TENATIVE<#break><#case 1>ACCEPTED<#break><#case 2>DECLINED<#break><#case 3>CANCELED<#break><#default>UNKNOWN STATE OF MIND</#switch> invitation <#if invite.event.title?exists>for ${invite.event.title} </#if>.