<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://myfaces.apache.org/extensions" prefix="t"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<f:view>
 <head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  <title><h:outputText value="#{messages.Home}"/></title>
  <style type="text/css" media="all">
	@import "style/default/screen.css";
  </style>
 </head>
 <body>
 
   <h:form>

     <h1><h:outputText value="#{messages.Home}"/></h1>

     <%@ include file="switcher.jsp" %> 
     <%@ include file="projectList.jsp" %>
	 
	 </div>

   </h:form>

   <%@ include file="conversations.jsp" %>

 </body>
</f:view>
</html>