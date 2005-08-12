<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Caveat Emptor</title>
 </head>
 <body>
  <h2>Your item</h2>
  <f:view>
     <h:outputText value="#{jsfItemManager.item.name}"/>
     <h:outputText value="#{jsfItemManager.item.description}"/>

	Has been submitted ! Thank you !
  </f:view>
 </body>
</html>