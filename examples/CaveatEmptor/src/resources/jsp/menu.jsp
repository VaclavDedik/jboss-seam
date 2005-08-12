<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<html>
 <head>
  <title>Caveat Emptor</title>
 </head>
 <body>
  <h2>Menu</h2>
  <f:view>
    <h:commandLink value="Create a new item" action="#{jsfItemManager.start}"/>
    <h:commandLink value="Admin items" action="admin"/>
  </f:view>
 </body>
</html>
    