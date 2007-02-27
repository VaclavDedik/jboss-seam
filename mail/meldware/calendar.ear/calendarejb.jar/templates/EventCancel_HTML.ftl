Event ${meeting.title} cancelled.-##-BODY-##-
<#setting url_escaping_charset='ISO-8859-1'>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <#if meeting.title?exists>
    <title>Event ${meeting.title} cancelled.</title>
  </#if>
</head>
<body>
<STYLE>TD {font-family: Verdana, Arial, Helvetica}</STYLE>
<TABLE width="100%" bgcolor="#ff0000">
  <tbody>
    <TR>
      <TD colspan="2">
      <#if meeting.title?exists><h2 style="font-family : serif;">Event ${meeting.title} cancelled.</h2></#if>
      </TD>
    </TR>
    <TR>
      <TD width="15%"></TD>
      <TD>
        <TABLE bgcolor="#ffbbbb" width="100%" border="2" style="border-style : solid;">
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
	</TABLE>
      </TD>
    </TR>
  </tbody>
</TABLE>
</body>
</html>
