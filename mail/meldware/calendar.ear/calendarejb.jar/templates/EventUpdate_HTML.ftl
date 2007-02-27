Event "${meeting.title}" changed-##-BODY-##-
<#setting url_escaping_charset='ISO-8859-1'>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <#if meeting.title?exists>
    <title>Event ${meeting.title} changed.</title>
  </#if>
</head>
<body>
<STYLE>TD {font-family: Verdana, Arial, Helvetica}</STYLE>
<TABLE width="100%" bgcolor="#59dc66">
  <tbody>
    <TR>
      <TD colspan="2">
      <#if meeting.title?exists><h2 style="font-family : serif;">Event ${meeting.title} changed.</h2></#if>
      </TD>
    </TR>
    <TR>
      <TD width="15%"></TD>
      <TD>
        <TABLE bgcolor="#baffb1" width="100%" border="2" style="border-style : solid;">
	  <TR>
            <TD  style="border-style : hidden;">Organizer:</TD>
            <TD bgcolor="#ffffff">
              <#if meeting.organizer?exists>
                <#list meeting.organizer as organizer>
                ${organizer.userName}<br/>
                </#list>
              </#if>
            </TD>
          </TR>
	  <TR>
            <TD  style="border-style : hidden;">Location:</TD>
            <TD bgcolor="#ffffff"><#if meeting.location?exists>${meeting.location}</#if></TD>
          </TR>
	  <TR>
            <TD  style="border-style : hidden;">Start:</TD>
            <TD bgcolor="#ffffff">${meeting.startDate}</TD>
          </TR>
	  <TR>
            <TD  style="border-style : hidden;">End:</TD>
            <TD bgcolor="#ffffff">${meeting.endDate}</TD>
          </TR>
          <#if meeting.note?exists>
	  <TR>
            <TD colspan="2" bgcolor="#ffffff">${meeting.note}</TD>
          </TR>
          </#if>
	  <TR>
	    <TD colspan="2" style="border-style : hidden;">
	         <A href="mailto:ACCEPT.${InviteUID?url}.${calendarAcct}?subject=ACCEPT%20${meeting.title?url}&X-CAL-Action=Accept&X-CAL-InviteUID=${InviteUID?url}&body=Accept!" target=_blank>Accept</A>
	         <A href="mailto:DECLINE.${InviteUID?url}.${calendarAcct}?subject=DECLINE%20${meeting.title?url}&X-CAL-Action=Decline&X-CAL-InviteUID=${InviteUID?url}&body=Not%0D%0AGoing!" target=_blank>Decline</A>
            </TD>
          </TR>
	</TABLE>
      </TD>
    </TR>
  </tbody>
</TABLE>
</body>
</html>
