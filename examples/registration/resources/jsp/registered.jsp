<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Successfully Registered New User</title>
 </head>
 <body>
  <f:view>
    Welcome, <h:outputText value="#{user.name}"/>, 
    you are successfully registered as <h:outputText value="#{user.username}"/>.
  </f:view>
 </body>
</html>