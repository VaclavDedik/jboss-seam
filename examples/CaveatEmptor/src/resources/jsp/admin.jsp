<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Caveat Emptor</title>
 </head>
 <body>
  <h2>Admin items</h2>
  <f:view>
   <h:dataTable var="element" value="#{jsfItemManager.pendingTasks}">
     <h:column>
        <h:outputText value="#{element.name}" />
     </h:column>
     <h:column>
        <h:outputText value="#{element.description}" />
     </h:column>
   </h:dataTable>
  </f:view>
 </body>
</html>