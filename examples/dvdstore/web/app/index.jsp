<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

This is a temporary index page - 
<f:view>
    <h:commandLink action="#{history.findProducts}"><h:outputText value="start the app" /></h:commandLink>
</f:view>
