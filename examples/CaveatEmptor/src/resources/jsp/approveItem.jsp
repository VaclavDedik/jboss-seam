<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<html>
 <head>
  <title>Caveat Emptor</title>
 </head>
 <body>
  <h2>Admin item</h2>
  <f:view>
   <h:form>
     <h:outputText value="#{item.name}"/>
     <h:outputText value="#{item.description}"/>

     <h:commandLink value="Edit Item" action="#{jsfItemManager.editItem}">
		<f:param name="processInstanceId" value="#{jsfItemManager.processInstanceId}"/>
     </h:commandLink>
     <h:commandLink value="Approve Item" action="#{jsfItemManager.approveItem}">
		<f:param name="processInstanceId" value="#{jsfItemManager.processInstanceId}"/>
     </h:commandLink>
   </h:form>
  </f:view>
 </body>
</html>