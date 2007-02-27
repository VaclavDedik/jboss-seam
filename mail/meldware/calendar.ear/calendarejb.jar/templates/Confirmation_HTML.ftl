RSVP for: ${invite.event.title}-##-BODY-##-
<#setting url_escaping_charset='ISO-8859-1'>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <title>RSVP for ${invite.event.title}</title>
</head>
<body>
<STYLE>TD {font-family: Verdana, Arial, Helvetica}</STYLE>
<TABLE width="100%" bgcolor="#dadada">
  <tbody>
    <TR>
      <TD colspan="2"><h2 style="font-family : serif;">${invite.user.userName} has <#switch invite.status><#case 0>become TENATIVE<#break><#case 1>ACCEPTED<#break><#case 2>DECLINED<#break><#case 3>CANCELED<#break><#default>UNKNOWN STATE OF MIND</#switch> invitation <#if invite.event.title?exists>for ${invite.event.title} </#if>. </h2></TD>
    </TR>
    <TR>
      <TD width="15%"></TD>
      <TD>
        <TABLE bgcolor="#ffffff" width="100%" border="2" style="border-style : solid;">
	  <TR>
        <TD  style="border-style : hidden;">Location:</TD>
        <TD bgcolor="#ffffff"><#if invite.event.location?exists>${invite.event.location}</#if></TD>
      </TR>
	  <TR>
        <TD  style="border-style : hidden;">Start:</TD>
        <TD bgcolor="#ffffff">${invite.event.startDate}</TD>
      </TR>
	  <TR>
        <TD  style="border-style : hidden;">End:</TD>
        <TD bgcolor="#ffffff">${invite.event.endDate}</TD>
      </TR>
      <#if invite.event.note?exists>
	  <TR>
        <TD colspan="2" bgcolor="#ffffff">${invite.event.note}</TD>
      </TR>
      </#if>
	</TABLE>
      </TD>
    </TR>
  </tbody>
</TABLE>
</body>
</html>