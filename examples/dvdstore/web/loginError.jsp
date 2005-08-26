<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
                      "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="full_header" value="true" />
<f:view>
<html>
<head>
    <f:loadBundle basename="com.jboss.dvd.web.store" var="msgs" />     
    <link href="<c:url value="/style.css" />" rel="stylesheet" type="text/css" />
    <title><h:outputText value="#{msgs.loginPageTitle}" /></title>
</head>
<body>
    <%@ include file="head.jsp" %> 
    <!-- <h1><h:outputText value="#{msgs.storeHeader}" /></h1> -->
    <h2><h:outputText value="#{msgs.loginErrorPrompt}" /></h2>
    
    <form action="j_security_check" method="post">
        <h:outputText value="#{msgs.loginUser}" /> 
        <input type="text" name="j_username" size="16" maxlength="24" />
        <h:outputText value="#{msgs.loginPass}" /> 
        <input type="password" name="j_password" size="16" maxlength="24" />
        <input type="submit" value="<h:outputText value="#{msgs.loginPrompt}" />"/> 
    </form>
    
    <h2><h:outputText value="#{msgs.newCustomerPrompt}"/></h2>
    
    <h:form>
        <h:commandButton action="newcustomer" value="#{msgs.newCustomerButton}" />
    </h:form>

    <%@ include file="foot.jsp" %> 
</body>
</html>
</f:view>
