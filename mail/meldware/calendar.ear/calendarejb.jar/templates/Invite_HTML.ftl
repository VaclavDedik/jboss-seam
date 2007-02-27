Invitation for: "${invite.event.title}"-##-BODY-##-
<#setting url_escaping_charset='ISO-8859-1'>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <#if invite.event.title?exists>
    <title>Invitation for: ${invite.event.title}</title>
  </#if>
</head>
<body>
<STYLE>TD {font-family: Verdana, Arial, Helvetica}</STYLE>
<TABLE width="100%" bgcolor="#6487dc">
  <tbody>
    <TR>
      <TD colspan="2">
      <#if invite.event.title?exists><h2 style="font-family : serif;">Invitation for: ${invite.event.title}</h2></#if>
      </TD>
    </TR>
    <TR>
      <TD width="15%"></TD>
      <TD>
        <TABLE bgcolor="#d9e3ff" width="100%" border="2" style="border-style : solid;">
	  <TR>
            <TD  style="border-style : hidden;">Organizer:</TD>
            <TD bgcolor="#ffffff">
              <#if invite.event.organizer?exists>
                <#list invite.event.organizer as organizer>
                ${organizer.userName}<br/>
                </#list>
              </#if>
            </TD>
          </TR>
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
            <TD colspan="2" bgcolor="#ffffff"><#if invite.event.note?exists>${invite.event.note}</#if></TD>
      </TR>
          </#if>
	  <TR>
	    <TD colspan="2" style="border-style : hidden;">
	    	 <A href="mailto:ACCEPT.${InviteUID?url}.${calendarAcct}?subject=ACCEPT%20${invite.event.title?url}&X-CAL-Action=Accept&X-CAL-InviteUID=${InviteUID?url}&body=Accept!" target=_blank>Accept</A>
	         <A href="mailto:DECLINE.${InviteUID?url}.${calendarAcct}?subject=DECLINE%20${invite.event.title?url}&X-CAL-Action=Decline&X-CAL-InviteUID=${InviteUID?url}&body=Not%0D%0AGoing!" target=_blank>Decline</A>
        </TD>
      </TR>
	</TABLE>
      </TD>
    </TR>
  </tbody>
</TABLE>
</body>
</html>