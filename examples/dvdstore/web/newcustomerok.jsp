<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
                      "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<f:view>
<html>
<head>
    <f:loadBundle basename="com.jboss.dvd.web.store" var="msgs" />     
    <link href="<c:url value="/style.css" />" rel="stylesheet" type="text/css" />
    <title><h:outputText value="#{msgs.newCustomerTitle}" /></title>
</head>
<body>
    <%@ include file="head.jsp" %> 
    <!-- <h1><h:outputText value="#{msgs.storeHeader}" /></h1> -->
    <h2><h:outputText value="#{msgs.newCustomerOkHeader}" /></h2>

    <h:form>
        <h:commandButton action="browse" value="#{msgs.startShoppingButton}" />
    </h:form>

    <%@ include file="foot.jsp" %>

</body>
</html>
</f:view>
