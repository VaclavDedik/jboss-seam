<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Booking Confirmed</title>
 </head>
 <body>
  <f:view>
    Thank you, <h:outputText value="#{user.name}"/>, your confimation number is <h:outputText value="#{booking.id}"/>.
   <br/>
    <h:commandLink action="main">
      <h:outputText value="Done"/>
    </h:commandLink>
    <br/>
    <h:commandLink action="#{logout.logout}">
      <h:outputText value="Logout"/>
    </h:commandLink>
    <h:commandLink action="password">
      <h:outputText value="Change password"/>
    </h:commandLink>
  </f:view>
 </body>
</html>