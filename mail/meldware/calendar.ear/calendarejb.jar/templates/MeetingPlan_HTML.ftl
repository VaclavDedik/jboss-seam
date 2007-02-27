Available plans for meeting: ${title}-##-BODY-##-
<#setting url_escaping_charset='ISO-8859-1'>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<body>
<STYLE>TD {font-family: Verdana, Arial, Helvetica}</STYLE>
<TABLE width="100%">
  <tbody>
    <TR>
      <TH>Click on a proposal to schedule event:<#if title?has_content>${title}<#else>Unknown</#if></TH>
    </TR>
    <#list planedEvents as event>
    <TR>
      <TD>
      <#assign acctAlias = "start=" + event.startDate?string(calDateFormat) + ".end=" + event.endDate?string(calDateFormat) + "." + calendarAcct>
      <A href="mailto:SCHEDULE.${acctAlias?url}<#list invites as invited>%2C%20${invited}</#list>?subject=<#if event.title?has_content>${event.title?url}<#else>Enter Title</#if>&body=<#if event.note?has_content>${event.note?url}<#else>Enter Note</#if>" target=_blank>
      ${event.startDate?string("yyyy-MM-dd HH:mm")} - ${event.endDate?string("HH:mm")}
      </A>
      </TD>
    </TR>
    </#list> 
  </tbody>
</TABLE>
</body>
</html>