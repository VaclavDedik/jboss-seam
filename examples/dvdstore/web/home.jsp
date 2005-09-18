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

    <h2><h:outputText value="#{msgs.loginPagePrompt}" /></h2>
    <h3><h:outputText value="#{msgs.loginPageInfo}" /></h3>
    
    <h:messages globalOnly="true" 
                layout="list" 
                errorClass="error"/>

    <h:form>
        <h:panelGrid columns="2">
            <h:outputText value="#{msgs.loginUser}" /> 
            <h:inputText value="#{customer.userName}" size="16" />

            <h:outputText value="#{msgs.loginPass}" /> 
            <h:inputSecret value="#{customer.password}" size="16"/>
        </h:panelGrid>
        <h:commandButton action="#{login.login}" />
    </h:form>
    
    <h2><h:outputText value="#{msgs.newCustomerPrompt}"/></h2>
    
    <h:form>
        <h:commandButton action="#{editCustomer.startEdit}" value="#{msgs.newCustomerButton}" />
    </h:form>
    <%@ include file="foot.jsp" %> 
</body>
</html>
</f:view>
 
