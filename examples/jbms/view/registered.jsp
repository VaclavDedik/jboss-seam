<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Please confirm your registration</title>
 </head>
 <body>
  <f:view>
    Welcome, <h:outputText value="#{user.name}"/>, 
    a confirmation email was sent to <h:outputText value="#{user.email}"/>. 
    Please reply to this email to confirm your registration as 
    <h:outputText value="#{user.username}"/>.
  </f:view>
 </body>
</html>